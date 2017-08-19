package com.oaksoft.logging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.exception.GrokException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.oaksoft.commons.io.input.NameableTailer;
import com.oaksoft.logging.grok.GrokUtils;
import com.oaksoft.logging.log4j.message.LogGenieMessage;
import com.oaksoft.logging.regex.RegexUtils;
import com.oaksoft.util.datetime.DateTimeUtils;

import static com.oaksoft.logging.util.LoggingUtils.EOT_DELIMITER;
import static com.oaksoft.logging.util.LoggingUtils.ETX_DELIMITER;
import static com.oaksoft.logging.util.LoggingUtils.SOH_DELIMITER;
import static com.oaksoft.logging.util.LoggingUtils.STX_DELIMITER;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class LogAgent
{
	private static final int MD5_STRING_LENGTH = 256;
    private static final int POLL_MILLIS = 50;
    private static final ConcurrentHashMap<String, Integer> LOG_FILES = new ConcurrentHashMap<String, Integer>();
//    private static final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> LOG_FILES_MAP = new ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>>();
//    private static final String DIRECTORY_TO_WATCH = "logs/2014-11";
//    private static final String FILE_TO_WATCH = "logs/app-rolling.log";
    private static final Integer FILE_BUSY = -1;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static String md5Hex = new String();
    //private static final ConcurrentHashMap<String, String> THREAD_MD5 = new ConcurrentHashMap<String, String>();

    private static final ThreadLocal<String> logMessageThreadLocal = new ThreadLocal<String>();

	private static final TreeMap<String, String> conversionPatterns = new TreeMap<>();

	private static final Grok grok = initializeGrok("conf/dev/patterns");

    private static final Level[] LOGGER_LEVELS = Level.values();
    private static final Integer LOGGER_LEVELS_LENGTH = Level.values().length;
    private static final Pattern LOGGER_LEVELS_PATTERN = getLevelPattern(true);
    private static final Pattern LOGGER_TIMESTAMP_PATTERN = getTimeStampPattern(true);
//    private static final Pattern HEADER_PATTERN = Pattern.compile(getLevelPattern(false).toString() + " +" + getTimeStampPattern(false).toString());
    private static final Pattern HEADER_PATTERN = Pattern.compile(getTimeStampPattern(false).toString() + " " + getLevelPattern(false).toString());

    private static final Pattern LOGGER_THREADNAME_PATTERN = getThreadNamePattern();
    private static final Pattern LOGGER_LOGGER_NAME_PATTERN = getLoggerNamePattern();

    //private static final Pattern LOG_MESSAGE_PATTERN = Pattern.compile(LOGGER_LEVELS_PATTERN.toString() + " +" + LOGGER_TIMESTAMP_PATTERN.toString() + " " + LOGGER_THREADNAME_PATTERN.toString() + " " + LOGGER_LOGGER_NAME_PATTERN.toString() + getMessageDelimiter() + getMessagePattern());
    //private static final Pattern LOG_MESSAGE_PATTERN = Pattern.compile(LOGGER_TIMESTAMP_PATTERN.toString() + " " + LOGGER_LEVELS_PATTERN.toString() + " +" + LOGGER_THREADNAME_PATTERN.toString() + " " + LOGGER_LOGGER_NAME_PATTERN.toString() + getMessageDelimiter() + getMessagePattern());

    private static final Pattern LOG_MESSAGE_PATTERN = getLogMessagePattern();

    private static final int MAX_LOG_MESSAGE_SIZE = 32768;

    //private static final ConcurrentHashMap<Integer, String> LINES_BY_FILE = new ConcurrentHashMap<Integer, String>();
    private static final ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>> FILE_BUFFER_QUEUE_MAP = new ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>>();
    private static final ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>> FILE_RECOVER_QUEUE_MAP = new ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>>();


    //private static final ConcurrentHashMap<AtomicInteger, CopyOnWriteArrayList<ConcurrentLinkedQueue<String>>> RECOVER_QUEUE_MAP = new ConcurrentHashMap<AtomicInteger, CopyOnWriteArrayList<ConcurrentLinkedQueue<String>>>();

    private static AtomicInteger threadID = new AtomicInteger(0);
    private static AtomicBoolean pauseProcessing = new AtomicBoolean(false);
    private static AtomicBoolean inRecoveryMode = new AtomicBoolean(false);
    private static AtomicInteger fileNumber = new AtomicInteger(0);

    private static Boolean bufferQueueTimerActive = false;
	private static Timer bufferQueueTimer;

	private static Boolean recoveryQueueTimerActive = false;
	private static Timer recoveryQueueTimer;

	private static String logMessage = new String();

	private static volatile boolean inMessage = false;

	private static Boolean logMessageTimerActive = false;
	private static Timer logMessageTimer;

//	private static Boolean processLineTimerActive = false;
//	private static Timer processLineTimer;


	private static void throwUnsupportedException(String exceptionMessage)
	{
		throw new UnsupportedOperationException(exceptionMessage);
	}

	private static Pattern getLogMessagePattern()
	{
		String patternString = "[%d{ISO8601}] %-5p [%t] [%c] - %m";

		String breakString = StringUtils.repeat("=", patternString.length());
		System.out.println("\n" + breakString);
		System.out.println(patternString);
		System.out.println(breakString);

		TreeMap<String, Pattern> patternTreeMap = new TreeMap<>();

		ClassLoader classLoader = LogAgent.class.getClassLoader();

		String loggingTypePackage = "com.oaksoft.logging.pattern.log4j.";
		String loggingType = "Log4jGrok";
		String loggingTypeSuffix = "Patterns";

		try
		{
			Class<?> loggingPatternsClass = classLoader.loadClass(loggingTypePackage + loggingType + loggingTypeSuffix);
			Field[] fields = loggingPatternsClass.getDeclaredFields();

			for (Field field : fields)
			{
				if (field.getType().isAssignableFrom(Pattern.class))
				{
					try
					{
						patternTreeMap.put(field.getName().replace("_PATTERN", ""), (Pattern) field.get(null));
					}
					catch (IllegalArgumentException | IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		catch (ClassNotFoundException e1)
		{
			e1.printStackTrace();
		}

		int mdcCount = 0;

		for (Entry<String, Pattern> entry : patternTreeMap.entrySet())
		{
			if (!entry.getKey().matches("MDC"))
			{
				getConversionPattern(entry.getValue(), patternString, entry.getKey());
			}
			else
			{
				ArrayList<String> mdcMatches = new ArrayList<String>();

				Matcher mdcMatcher = entry.getValue().matcher(patternString);

				while (mdcMatcher.find())
				{
					mdcMatches.add(mdcMatcher.group());
				}

				for (String mdc : mdcMatches)
				{
					mdcCount++;
					getConversionPattern(entry.getValue(), mdc, entry.getKey() + mdcCount);
				}
			}
		}

		for (Entry<String, String> entry : conversionPatterns.entrySet())
		{
			patternString = patternString.replace(entry.getValue(), RegexUtils.delimitPattern(entry.getKey()));
		}

		try
		{
			grok.compile(GrokUtils.buildGrokPattern(patternString));
		}
		catch (GrokException e)
		{
			e.printStackTrace();
		}

		String grokPatternString = grok.getNamedRegex();

		return Pattern.compile(grokPatternString);
	}

	public static void main(String[] args) throws Exception
    {

    	String fileToWatch = null;
    	String directoryToWatch = null;

    	System.err.println(LOG_MESSAGE_PATTERN);



//    	Properties properties = System.getProperties();
//
//    	for (Object key: properties.keySet())
//    	{
//    		System.err.println("Key: " + key + ": " + properties.get(key));
//    	}

    	logMessageThreadLocal.set(new String());

    	if (args.length == 0)
    	{
    		throwUnsupportedException("No parameters specified.");
    	}
    	else if (args.length == 2)
    	{
    		fileToWatch = args[0];
    		directoryToWatch = args[1];

//        	System.err.println(fileToWatch);
//        	System.err.println(directoryToWatch);
    	}
    	else
    	{
    		throwUnsupportedException("Too many parameters specified.");
    	}

        class TailerInstance implements Runnable
        {
        	private final Logger logger;
        	private final AtomicInteger threadID;
			private String file;
			private String directory;

    		public TailerInstance(AtomicInteger threadID, String file, String directory)
    		{
    			this.logger = LogManager.getLogger(this.getClass().getSimpleName() + "-" + threadID);
    			this.threadID = threadID;
    			this.file = file;
    			this.directory = directory;
    			//THREAD_MD5.put(threadID, new String());
			}

			public void run()
    		{
				//	Creates log tailer
    			TailerListenerAdapterImpl listener = new TailerListenerAdapterImpl(threadID);

    			//	Extended Tailer that allows the daemon thread to be named
    			//	Otherwise functionality the same as Apache version
    			NameableTailer.create(new File(file), listener, POLL_MILLIS, true, this.getClass().getSimpleName() + "Thread" + "-" + threadID);

    			//	Creates thread that watches for filesystem events
    			//	Without this thread or an infinite loop, the app would exit
    	        Thread watchServiceThread = new Thread(new WatchServiceInstance(threadID, directory, file), "WatchServiceThread" + "-" + threadID);
    	        watchServiceThread.start();
    		}

	        class WatchServiceInstance implements Runnable
	        {
	        	private final Logger logger;
	        	private final AtomicInteger threadID;
				private String directory;
				private String file;

	            public WatchServiceInstance(AtomicInteger threadID, String directory, String file)
	            {
	            	this.logger = LogManager.getLogger(this.getClass().getSimpleName() + "-" + threadID);
	            	this.threadID = threadID;
	            	this.directory = directory;
	            	this.file = file;
				}

				@Override
	            public void run()
	            {
	        		Path pathToWatch = Paths.get(directory);

	        		if (pathToWatch == null)
	        		{
	                    throw new UnsupportedOperationException("Directory " + directory + " not found");
	                }

	        		try
	        		{
	        			FileSystem fileSystem = pathToWatch.getFileSystem();

	        			WatchService watchService = fileSystem.newWatchService();

	                    Thread watcherThread = new Thread(new WatchQueueReader(watchService, threadID), this.getClass().getSimpleName() + "Thread" + "-" + threadID);

	                    pathToWatch.register(watchService, ENTRY_MODIFY, ENTRY_CREATE);

	                    watcherThread.start();
	        		}
	        		catch (IOException e)
	        		{
	        			e.printStackTrace();
	        		}
	            }

	            class WatchQueueReader implements Runnable
	            {
	                private WatchService watchService;
	                private CopyOnWriteArrayList<String> fileArray = new CopyOnWriteArrayList<String>();
					private AtomicInteger threadID;

	                public WatchQueueReader(WatchService watchService, AtomicInteger threadID)
	                {
	                    this.watchService = watchService;
	                    this.threadID = threadID;
	                }

	                @Override
	                public void run()
	                {
	                    try
	                    {
	                        // get the first event before looping
	                        WatchKey key = watchService.take();

	                        while (key != null)
	                        {
	                            // we have a polled event, now we traverse it and
	                            // receive all the states from it
	                            for (WatchEvent<?> event : key.pollEvents())
	                            {
	                            	//String fileName = DIRECTORY_TO_WATCH + "/" + event.context();
	                            	String fileName = directory + "/" + event.context();

	                            	System.err.println(fileName + " - " + getFileMd5(fileName));

//	                            	System.err.println("md5FileString: " + getFileMd5(fileName)  + " - size: " + getFileSize(fileName));

	                                if (!(getFileSize(fileName) < MD5_STRING_LENGTH))
	                                {
	                                	String md5FileString = getFileMd5(fileName);

		                                //	A file with the first MD5_STRING_LENGTH bytes exists, but it may not be complete
		                                if (md5FileString.matches(md5Hex))
		                                //if (md5FileString.matches(THREAD_MD5.get(threadID)))
		                                {
		                                	//	Wait for file to complete writing
		                                	//	May want to add Thread.sleep, but spin time is short

//		                                	try
//		                                	{
		                                		//pauseProcessing.set(true);

			                                	while (LOG_FILES.get(md5FileString).equals(FILE_BUSY))
			                                	{
//			                                		System.err.println("BUSY");
			                                	}

			                                	int readLines = LOG_FILES.get(md5FileString);
			                                	int fileLines = countLines(fileName, null);

			                                	//	Check for missing lines
			                                	if (readLines != fileLines)
			                                	{
			                                		inRecoveryMode.set(true);

			                                		//	Resend any missing lines
			                                		for (int i = readLines; i < fileLines; i++)
			                                		{
//			                                			System.err.println("processLine: true");
			                                			processLine(fileArray.get(i), logger, i, true);

			                                			//System.err.println("*** Recovered line: " + fileArray.get(i) + "***");
			                                		}
			                                	}
			                                	else
			                                	{
			                                		inRecoveryMode.set(false);
			                                	}


			                                	pauseProcessing.set(false);

			                                	//	Clear entry so that LOG_FILES map doesn't grow too big
			                                	//LOG_FILES.remove(md5FileString);

			                                	//	Clear fileArray so that next file's lines are recoverable
			                                	fileArray.clear();
//		                                	}
//		                                	catch (NullPointerException npe)
//		                                	{
//		                                		System.err.println("*** md5FileString: " + md5FileString);
//		                                		System.err.println("LOG_FILES: " + LOG_FILES.size());
//		                                		System.err.println("LOG_FILES.get(md5FileString): " + LOG_FILES.get(md5FileString));
//
//		                                		npe.printStackTrace();
//		                                	}
		                                }
		                            }
	                                else
	                                {
	                                	pauseProcessing.set(true);
	                                	//inRecoveryMode.set(false);
	                                	//dumpInfo();
	                                }

	                            }
	                            //	Get ready to process next event
	                            key.reset();
	                            key = watchService.take();
	                        }
	                    }
	                    catch (InterruptedException e)
	                    {
	                        e.printStackTrace();
	                    }
	                }

	                private String getFileMd5(String fileName)
	                {
	                	byte[] buffer = new byte[MD5_STRING_LENGTH];
	                	String bufferString = null;
	                	int bytesRead = 0;

	                	//	Java 7 try-with-resources
	                	try (InputStream input = new FileInputStream(fileName))
	                	{
	        	        	//	Ignores the degenerate case of logfile < MD5_STRING_LENGTH
	                		bytesRead = input.read(buffer);
	                		bufferString = new String(buffer);
						}
	                	catch (FileNotFoundException e)
	                	{
							e.printStackTrace();
						}
	                	catch (IOException e1)
	                	{
							e1.printStackTrace();
						}

	                	if (bytesRead < MD5_STRING_LENGTH)
	                	{
	                		return new String();
	                	}

	                	return getMd5(bufferString);

	                }

	                private int countLines(String filePath, String encoding)
	                {
	            		File file = new File(filePath);
	            		int lines = 0;

	            		//	Process the file line-by-line
	            		LineIterator lineIterator = null;

	            		try
	            		{
	            			lineIterator = FileUtils.lineIterator(file, encoding);

	            		    while (lineIterator.hasNext() )
	            		    {
	            		        lines++;
	            		        String line = lineIterator.nextLine();

	            		        //	Add the line being counted to the file array for later recovery
	            		        fileArray.add(line);
	            		    }
	            		}
	            		catch (IOException e)
	            		{
	            			e.printStackTrace();
	            		}
	            		finally
	            		{
	            			// LineIterator doesn't implement AutoCloseable, so use finally block to close resource
	            		    LineIterator.closeQuietly(lineIterator );
	            		}

	            		return lines;
	               	}
	            }
	        }

    	    class TailerListenerAdapterImpl extends TailerListenerAdapter
    	    {
    	    	private boolean haveMd5String = false;
    	    	private int linesRead = 0;
    	    	private String md5String = new String();
				private AtomicInteger threadID;

				public TailerListenerAdapterImpl(AtomicInteger threadID)
				{
					this.threadID = threadID;
				}

				@Override
    	        public void handle(String line)
    	        {
//					System.err.println("processLine: false");

					processLine(line, logger, linesRead, false);

    	        	linesRead++;

    	        	//	Need a string of length MD5_STRING_LENGTH to build md5
    	        	//	Ignores the degenerate case of logfile < MD5_STRING_LENGTH
    	        	if (!haveMd5String)
    	        	{
    	        		if (md5String.length() < MD5_STRING_LENGTH)
    	        		{
    	        			//	TailerListenerAdapter.handle methods strips line separator
    	        			//	This causes the md5 to mismatch vs file
    	        			md5String = md5String.concat(line + LINE_SEPARATOR);
    	        		}
    	        		else
    	        		{
    	        			haveMd5String = true;

    	        			md5String = md5String.substring(0, MD5_STRING_LENGTH);
    	        			String md5 = getMd5(md5String);

    	        			//	Clear entry so that LOG_FILES map doesn't grow too big
    	        			LOG_FILES.remove(md5Hex);

    	        			md5Hex = getMd5(md5String);
    	        			//THREAD_MD5.put(threadID, md5);

    	        			//	Put placeholder for md5 so that filewatcher can find reference
    	        			//	Set to FILE_BUSY to indicate the file is still being processed
    	        			//LOG_FILES.put(md5Hex, FILE_BUSY);
   	        				LOG_FILES.put(md5, FILE_BUSY);
    	        		}
    	        	}
    	        }

    	        @Override
    	        public void fileRotated()
    	        {
//    	        	dumpInfo();

    	        	System.err.println("************** File rotated " + " **************");

//    	        	FILE_BUFFER_QUEUE_MAP.put(fileNumber, new ConcurrentLinkedQueue<String>());
//    	        	FILE_RECOVER_QUEUE_MAP.put(fileNumber, new ConcurrentLinkedQueue<String>());

//    	        	CopyOnWriteArrayList<ConcurrentLinkedQueue<String>> recoveryArrayList = new CopyOnWriteArrayList<ConcurrentLinkedQueue<String>>();
//
//    	            recoveryArrayList.add(0, new ConcurrentLinkedQueue<String>());
//    	            recoveryArrayList.add(1, new ConcurrentLinkedQueue<String>());

    	            //RECOVER_QUEUE_MAP.put(fileNumber, recoveryArrayList);

    	        	//	Only process when a log fills up
    	        	//	Handles case where <OnStartupTriggeringPolicy/> is set for log4j2 in source
    	            if (haveMd5String)
    	            {
    	            	pauseProcessing.set(true);
//    	            	inRecoveryMode.set(false);

    	            	//	Update LOG_FILES to indicate file has been read
    	            	LOG_FILES.put(md5Hex, linesRead);
    	            	//LOG_FILES.put(THREAD_MD5.get(threadID), linesRead);
    	            }
    	            else
    	            {
    	            	pauseProcessing.set(false);
    	            	inRecoveryMode.set(false);
    	            }

    	            //THREAD_MD5.remove(threadID);
    	            md5String = new String();
    	            haveMd5String = false;
    	            linesRead = 0;
    	        }
    	    }
        }

        FILE_BUFFER_QUEUE_MAP.put(fileNumber, new ConcurrentLinkedQueue<String>());
        FILE_RECOVER_QUEUE_MAP.put(fileNumber, new ConcurrentLinkedQueue<String>());

//        CopyOnWriteArrayList<ConcurrentLinkedQueue<String>> recoveryArrayList = new CopyOnWriteArrayList<ConcurrentLinkedQueue<String>>();

        //	Buffer Queue
//        recoveryArrayList.add(0, new ConcurrentLinkedQueue<String>());

        //	Recover Queue
//        recoveryArrayList.add(1, new ConcurrentLinkedQueue<String>());

//        RECOVER_QUEUE_MAP.put(fileNumber, recoveryArrayList);


        //AtomicInteger threadID = new AtomicInteger(0);
        threadID.incrementAndGet();

        String threadName = "TailerThread-" + threadID;
        Thread tailerThread = new Thread(new TailerInstance(threadID, fileToWatch, directoryToWatch), threadName);

        //	Kicks everything off
    	tailerThread.start();

    	System.err.println("READY");
    }

    private static String getMd5(String md5)
    {
    	return DigestUtils.md5Hex(md5);
    }

    //	Outbound method
    private static void processLine(String line, Logger logger, int lineNumber, boolean isRecoverLine)
    {
//    	System.err.println(pauseProcessing.get() + " : " + fileNumber.get());

    	ConcurrentLinkedQueue<String> fileBufferQueue = FILE_BUFFER_QUEUE_MAP.get(fileNumber);
    	ConcurrentLinkedQueue<String> fileRecoveryQueue = FILE_RECOVER_QUEUE_MAP.get(fileNumber);

  /*
		class ProcessLineTimer extends TimerTask
		{
			public void run()
			{
//   				System.err.println("--- " + "TIMEOUT" + " ---");

   				md5Hex = new String();

//   				dumpInfo();

   				setProcessLineTimer(false);
			}
		}
*/

//    	CopyOnWriteArrayList<ConcurrentLinkedQueue<String>> recoveryArrayList = RECOVER_QUEUE_MAP.get(fileNumber);

//    	ConcurrentLinkedQueue<String> fileBufferQueue = recoveryArrayList.get(0);
//    	ConcurrentLinkedQueue<String> fileRecoveryQueue = recoveryArrayList.get(1);

    	//LINES_BY_FILE.put(lineNumber, line);

    	if (!pauseProcessing.get())
    	{
        	if (inRecoveryMode.get())
        	{
        		//System.err.println("###### fileNumber: " + fileNumber + " ######");

        		//fileNumber.incrementAndGet();

        		while (!fileRecoveryQueue.isEmpty())
        		{
//        			assembleLogMessage(logger, "+++ " + fileRecoveryQueue.remove());
        			assembleLogMessage(logger, fileRecoveryQueue.remove());
        		}

    			inRecoveryMode.set(false);
        	}

			while (!fileBufferQueue.isEmpty())
			{
//				assembleLogMessage(logger, "=== " + fileBufferQueue.remove());
				assembleLogMessage(logger, fileBufferQueue.remove());
			}

//	    		System.err.println(line);
			assembleLogMessage(logger, line);

//    		setProcessLineTimer(false);
    	}
    	else
    	{
    		if (!isRecoverLine)
    		{
//	    			System.err.println("%%%%%% " + fileNumber.get() + " %%%%%%");
//	    			fileBufferQueue.add(line);

    			sendToBufferQueue(fileBufferQueue, line, logger);
//    			setProcessLineTimer(false);
    		}
    		else
    		{
//	    			System.err.println("@@@@@@ " + fileNumber.get() + " @@@@@@");
//	    			fileRecoveryQueue.add(line);

    			sendToRecoveryQueue(fileRecoveryQueue, line, logger);
//    			setProcessLineTimer(false);
    		}
    	}

    	//logMessageThreadLocal.set(line);

    	//publishLogMessage(logger);

//	    startProcessLineTimer(new ProcessLineTimer(), 2000);
    }

/*
    private static void startProcessLineTimer(TimerTask timerTask, int timeout)
    {
    	processLineTimer = new Timer();
    	setProcessLineTimer(true);

		processLineTimer.schedule(timerTask, timeout);
    }

    private static void setProcessLineTimer(Boolean processLineTimerActive)
	{
		if (!processLineTimerActive)
		{
			if (processLineTimer != null)
			{
				processLineTimer.cancel();
			}
		}

		OldAgent.processLineTimerActive = processLineTimerActive;
	}
*/

    private static void sendToBufferQueue(final ConcurrentLinkedQueue<String> fileBufferQueue, String line, final Logger logger)
    {
		class BufferQueueTimer extends TimerTask
		{
			public void run()
			{
				if (isRecoveryQueueTimerActive())
				{
					setBufferQueueTimerActive(false);
					startBufferQueueTimer(new BufferQueueTimer(), 1000);
				}
				else
				{
					if (!fileBufferQueue.isEmpty())
					{
		    			while (!fileBufferQueue.isEmpty())
		    			{
//		    				System.err.println("!!! " + fileBufferQueue.remove());
		    				assembleLogMessage(logger, fileBufferQueue.remove());
		    			}

		    			pauseProcessing.set(false);
					}

					setBufferQueueTimerActive(false);
				}
			}
		}

		fileBufferQueue.add(line);

		startBufferQueueTimer(new BufferQueueTimer(), 500);
    }

//	private static Boolean isBufferQueueTimerActive()
//	{
//		return bufferQueueTimerActive;
//	}

    private static void startBufferQueueTimer(TimerTask timerTask, int timeout)
    {
    	bufferQueueTimer = new Timer("BufferQueueTimer");
		setBufferQueueTimerActive(true);

		bufferQueueTimer.schedule(timerTask, timeout);
    }

    private static void setBufferQueueTimerActive(Boolean bufferQueueTimerActive)
	{
		if (!bufferQueueTimerActive)
		{
			if (bufferQueueTimer != null)
			{
				bufferQueueTimer.cancel();
			}
		}

		LogAgent.bufferQueueTimerActive = bufferQueueTimerActive;
	}

    private static void sendToRecoveryQueue(final ConcurrentLinkedQueue<String> fileRecoveryQueue, String line, final Logger logger)
    {
		class RecoveryQueueTimer extends TimerTask
		{
			public void run()
			{
				if (!fileRecoveryQueue.isEmpty())
				{
	    			while (!fileRecoveryQueue.isEmpty())
	    			{
//	    				System.err.println("~~~ " + fileRecoveryQueue.remove());
//	    				assembleLogMessage(logger, "~~~ " + fileRecoveryQueue.remove());
	    				assembleLogMessage(logger, fileRecoveryQueue.remove());
	    			}

	    			inRecoveryMode.set(false);
				}

				setRecoveryQueueTimerActive(false);
			}
		}

		fileRecoveryQueue.add(line);

		startRecoveryQueueTimer(new RecoveryQueueTimer(), 500);
    }

	private static Boolean isRecoveryQueueTimerActive()
	{
		return recoveryQueueTimerActive;
	}

    private static void startRecoveryQueueTimer(TimerTask timerTask, int timeout)
    {
    	recoveryQueueTimer = new Timer("RecoveryQueueTimer");
    	setRecoveryQueueTimerActive(true);

		recoveryQueueTimer.schedule(timerTask, timeout);
    }

    private static void setRecoveryQueueTimerActive(Boolean recoveryQueueTimerActive)
	{
		if (!recoveryQueueTimerActive)
		{
			if (recoveryQueueTimer != null)
			{
				recoveryQueueTimer.cancel();
			}
		}

		LogAgent.recoveryQueueTimerActive = recoveryQueueTimerActive;
	}

    //	Outbound method
    private static void assembleLogMessage(final Logger logger, String line)
    {
    	//String line = logMessageThreadLocal.get();

    	line = line.concat("\n");

    	boolean headerFound = HEADER_PATTERN.matcher(line).find();

		class LogMessageTimer extends TimerTask
		{
			public void run()
			{
//				System.err.println("TIMEOUT: " + logMessage);

				publishLogMessage(logMessage, logger);
    			logMessage = new String();
			}
		}

    	if (headerFound)
    	{
    		//	Start the timer when the header is found
    		startLogMessageTimer(new LogMessageTimer(), 4000);

    		if (logMessage.length() > 0)
    		{
    			publishLogMessage(logMessage, logger);
    		}

    		inMessage = !inMessage;
    	}

    	if (inMessage)
    	{
    		//	Cancel and restart timer when logMessage is updated
    		if (isLogMessageTimerActive())
    		{
    			setLogMessageTimerActive(false);
    			startLogMessageTimer(new LogMessageTimer(), 4000);
    		}

    		logMessage = logMessage.concat(line);

//    		System.err.println("Building logMessage: " + logMessage + "******");
    	}
		//    	System.err.println("headerFound: " + headerFound + " - " + line);
    }

    private static void startLogMessageTimer(TimerTask timerTask, int timeout)
    {
		logMessageTimer = new Timer();
		setLogMessageTimerActive(true);

		logMessageTimer.schedule(timerTask, timeout);

//		System.err.println(Thread.currentThread().getName());
    }


	public static Boolean isLogMessageTimerActive()
	{
		return logMessageTimerActive;
	}

    private static void publishLogMessage(String message, Logger logger)
    {
    	String levelName = null;
    	String timeStampStr = null;
    	String threadName = null;
    	String loggerName = null;
    	String loggingMessage = null;

    	Matcher messsageMatcher = LOG_MESSAGE_PATTERN.matcher(message);
    	messsageMatcher.find();

    	if (messsageMatcher.groupCount() > 0)
    	{
    		try
    		{
//		    	levelName = messsageMatcher.group(1);
//		    	timeStampStr = messsageMatcher.group(2);

//		    	levelName = messsageMatcher.group(2);
		    	levelName = messsageMatcher.group("loglevel");

//		    	timeStampStr = messsageMatcher.group(1);
		    	timeStampStr = messsageMatcher.group("timestamp");

//		    	threadName = messsageMatcher.group(3);
		    	threadName = messsageMatcher.group("log4jThread");

//		    	loggerName = messsageMatcher.group(4);
		    	loggerName = messsageMatcher.group("log4jCategory");

		    	//	Group 5 is the message delimiter

//		    	loggingMessage = messsageMatcher.group(6).trim();
		    	loggingMessage = messsageMatcher.group("log4jMessage").trim();

//		    	if (loggingMessage.length() > MAX_LOG_MESSAGE_SIZE)
//		    	{
//		    		loggingMessage = StringUtils.abbreviate(loggingMessage, MAX_LOG_MESSAGE_SIZE);
//		    	}

	//	    	System.err.println("*** loggingMessage: " + loggingMessage);

		    	//logger.log(Level.getLevel(levelName), new LogGenieMessage(loggingMessage, DateTimeUtils.getJodaDateTimeUTC(timeStampStr), loggerName, threadName, ""));
    		}
    		catch (IllegalStateException e)
    		{
    			logger.error("Can't process: " + message);
    		}
    	}
    	else
    	{
        	logger.info(message);
    	}

    	messsageMatcher.reset();

		setLogMessageTimerActive(false);

		logMessage = new String();

		inMessage = false;
    }

	public static void setLogMessageTimerActive(Boolean timerActive)
	{
		if (!timerActive)
		{
			if (logMessageTimer != null)
			{
				logMessageTimer.cancel();
			}
		}

		LogAgent.logMessageTimerActive = timerActive;
	}

    private static Pattern getLevelPattern(boolean returnGroup)
    {
    	String levels = new String();

    	for (int i=0; i < LOGGER_LEVELS_LENGTH; i++)
    	{
    		 levels = levels.concat(LOGGER_LEVELS[i].toString() + "|");
    	}

    	if (returnGroup)
    	{
    		levels = "(" + levels.substring(0, levels.length() -1 ) + ")";
    	}
    	else
    	{
    		levels = "(?:" + levels.substring(0, levels.length() -1 ) + ")";
    	}

//    	if (returnGroup)
//    	{
//    		levels = "^(" + levels.substring(0, levels.length() -1 ) + ")";
//    	}
//    	else
//    	{
//    		levels = "^(?:" + levels.substring(0, levels.length() -1 ) + ")";
//    	}

    	return Pattern.compile(levels);
    }

    private static Pattern getThreadNamePattern()
    {
//    	return Pattern.compile("(\\[(?:[a-z]|[A-Z]|[0-9]|_|-)*\\])");
//    	return Pattern.compile("(?:(?:\\[)((?:[a-z]|[A-Z]|[0-9]|_|-| |\\(|\\)|\\.|\\[|\\])*)(?:\\]))");
    	return Pattern.compile("(?:(?:\\[)(\\[*(?:[a-z]|[A-Z]|[0-9]|_|-| |\\(|\\)|\\.|\\])*)(?:\\]))");
    }

    private static Pattern getLoggerNamePattern()
    {
//    	return Pattern.compile("(\\] (\\w|\\.)+ - )");
//       	return Pattern.compile("((?:\\w|\\.)+ - )");
//       	return Pattern.compile("((?:\\w|\\.)+)");
       	return Pattern.compile("(?:(?:\\[)((?:[a-z]|[A-Z]|[0-9]|_|-| |\\(|\\)|\\.)*)(?:\\]))");
    }

    private static Pattern getMessageDelimiter()
    {
//    	return Pattern.compile("( - |: )");
    	return Pattern.compile("( -[\\s\\S]| *:[\\s\\S])");
    }

    private static Pattern getTimeStampPattern(boolean returnGroup)
    {
//    	String basePattern = "\\[\\d{4}-(?:0[1-9]|1[0-2])-(?:[12]\\d|0[1-9]|3[01])T(?:(?:[0,1][0-9]|2[0-3])(?::[0-5][0-9]){2})\\,[0-9]{3}\\]";
    	String basePattern = "?:(?:\\[*)(\\d{4}-(?:0[1-9]|1[0-2])-(?:[12]\\d|0[1-9]|3[01])[T| ](?:(?:[0,1][0-9]|2[0-3])(?::[0-5][0-9]){2})\\,[0-9]{3})(?:\\]*)";

    	if (returnGroup)
    	{
    		return Pattern.compile("(" + basePattern + ")");
    	}
    	else
    	{
    		return Pattern.compile("(?:(" + basePattern + "))");
    	}
    }

    private static Pattern getMessagePattern()
    {
//    	return Pattern.compile("(.*)\\s*");
    	return Pattern.compile("([\\s\\S]*)", Pattern.DOTALL);
    }

    private static Pattern getGenericMessagePattern()
    {
    	return Pattern.compile("^[A-z]+: [0-9]+ rows inserted in [0-9]+ \\(ms\\) \\[[0-9]+\\]$");
    }

    private static long getFileSize(String filePath)
    {
		File file = new File(filePath);

		return file.length();
	}

//	private static DateTime getJodaDateTimeUTC(String dateString)
//	{
//		dateString = dateString.replace(" ", "T");
//		return getJodaDateTime(dateString, DateTimeZone.UTC);
//	}
//
//	private static DateTime getJodaDateTime(String dateString, DateTimeZone tz)
//	{
//		DateTime dateTime = new DateTime(dateString).toDateTime(tz);
//
//		return dateTime;
//	}

    private static void dumpInfo()
    {
		System.err.println("--- pauseProcessing: " + pauseProcessing + " ---");
		System.err.println("--- inRecoveryMode: " + inRecoveryMode + " ---");
		System.err.println("--- isRecoveryQueueTimerActive: " + isRecoveryQueueTimerActive() + " ---");
		System.err.println("--- bufferQueueTimerActive: " + bufferQueueTimerActive + " ---");
		System.err.println("--- fileNumber: " + fileNumber + " ---");
		System.err.println("--- md5Hex: " + md5Hex + " ---");
		System.err.println("--- FILE_RECOVER_QUEUE_MAP: " + FILE_RECOVER_QUEUE_MAP + " ---");
		System.err.println("--- FILE_BUFFER_QUEUE_MAP: " + FILE_BUFFER_QUEUE_MAP + " ---");
    }

	private static void getConversionPattern(Pattern pattern, String patternString, String type)
	{
		Matcher matcher = pattern.matcher(patternString);

		if (matcher.find())
		{
			String conversionPatternString = matcher.group(0);

			//System.out.println("\n" + type + ": " + pattern);

			try
			{
				if (!(matcher.group("formatModifier") == null))
				{
					//System.out.println(type + "(formatModifier): " + matcher.group("formatModifier"));
					type = type.concat(String.valueOf(STX_DELIMITER)).concat(matcher.group("formatModifier")).concat(String.valueOf(STX_DELIMITER));
				}
			}
			catch (IllegalArgumentException e)
			{

			}

			try
			{
				if (!(matcher.group("specifier") == null))
				{
					//System.out.println(type + "(specifier): " + matcher.group("specifier"));
					type = type.concat(String.valueOf(ETX_DELIMITER)).concat(matcher.group("specifier")).concat(String.valueOf(ETX_DELIMITER));
				}
			}
			catch (IllegalArgumentException e)
			{

			}

			conversionPatterns.put(type, conversionPatternString);
		}
	}

	private static Grok initializeGrok(String patternFileName)
	{
		Grok grok = null;

		try
		{
			grok = Grok.create(patternFileName);
		}
		catch (GrokException e)
		{
			throw new RuntimeException();
		}

		return grok;
	}
}
