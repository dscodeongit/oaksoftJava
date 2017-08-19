package com.oaksoft.logging;

import static com.oaksoft.logging.MultiLogAgent.FILE_PROPERTY;
import static com.oaksoft.logging.MultiLogAgent.FILE_PROPERTY_FILE_NAME_SUFFIX;
import static com.oaksoft.logging.util.LoggingUtils.ETX_DELIMITER;
import static com.oaksoft.logging.util.LoggingUtils.STX_DELIMITER;

import groovy.json.JsonBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oi.thekraken.grok.api.Grok;
import oi.thekraken.grok.api.exception.GrokException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.google.common.net.MediaType;
import com.oaksoft.commons.io.input.NameableTailer;
import com.oaksoft.enums.Codecs;
import com.oaksoft.enums.MessageStatusEnum;
import com.oaksoft.logging.config.Constants;
import com.oaksoft.logging.grok.GrokUtils;
import com.oaksoft.logging.log4j.message.LogGenieMessage;
import com.oaksoft.logging.regex.RegexUtils;
import com.oaksoft.logging.util.LoggingUtils;
import com.oaksoft.util.datetime.DateTimeUtils;

public class TailerInstance implements Runnable
{
	private Logger logger = null;
	private final Integer threadID;
	private long pollMillis;

	private Properties defaultProperties;
	private Properties properties;
	private String filePathString;

	private String logMessageFormatString;
	private String grokPatternsFile;

	private static String md5Hex = new String();
	private String filePrefix;

//	private AtomicInteger fileNumber = new AtomicInteger(0);

	private static final Integer FILE_BUSY = -1;

	private static final ThreadLocal<Boolean> pauseProcessing = new ThreadLocal<Boolean>()
	{
	    @Override
	    protected Boolean initialValue()
	    {
	        return Boolean.FALSE;
	    }

	};

	private static final ThreadLocal<Boolean> inRecoveryMode = new ThreadLocal<Boolean>()
	{
	    @Override
	    protected Boolean initialValue()
	    {
	        return Boolean.FALSE;
	    }

	};

//	private static final ThreadLocal<Boolean> inMessage = new ThreadLocal<Boolean>()
//	{
//	    @Override
//	    protected Boolean initialValue()
//	    {
//	        return Boolean.FALSE;
//	    }
//
//	};

	private static final ThreadLocal<Grok> grok = new ThreadLocal<Grok>()
	{
	    @Override
	    protected Grok initialValue()
	    {
	        return new Grok();
	    }
	};

	private static final ThreadLocal<Boolean> logMessageTimerActive = new ThreadLocal<Boolean>()
	{
	    @Override
	    protected Boolean initialValue()
	    {
	        return Boolean.FALSE;
	    }

	};

//	public static final ThreadLocal<Boolean> firstMessage = new ThreadLocal<Boolean>()
//	{
//	    @Override
//	    protected Boolean initialValue()
//	    {
//	        return Boolean.TRUE;
//	    }
//
//	};

//	private static final ThreadLocal<Pattern> headerPattern = new ThreadLocal<Pattern>()
//	{
//	    @Override
//	    protected Pattern initialValue()
//	    {
//	        return Pattern.compile("");
//	    }
//
//	};

	private static final ThreadLocal<Timer> logMessageTimer = new ThreadLocal<Timer>();

	private TreeMap<String, String> conversionPatterns = new TreeMap<String, String>();

	private static final String FILE_PROPERTY_GROK_PATTERNS_SUFFIX = ".grokPatterns";
	private static final String FILE_PROPERTY_TIMESTAMP_FORMAT_SUFFIX = ".timeStampFormat";

	private static final ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>> FILE_BUFFER_QUEUE_MAP = new ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>>();
    private static final ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>> FILE_RECOVER_QUEUE_MAP = new ConcurrentHashMap<AtomicInteger, ConcurrentLinkedQueue<String>>();
	private static final ConcurrentHashMap<String, Integer> LOG_FILES = new ConcurrentHashMap<String, Integer>();

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static final int MD5_STRING_LENGTH = 256;

	public TailerInstance(Integer threadID, Properties defaultProperties, Properties properties, String filePrefix, long pollMillis, String logMessageFormatString)
	{
		this.logger = LogManager.getLogger(this.getClass().getSimpleName() + "-" + threadID);
		this.threadID = threadID;
		this.filePathString = properties.getProperty(filePrefix + FILE_PROPERTY_FILE_NAME_SUFFIX);
		this.grokPatternsFile = properties.getProperty(filePrefix + FILE_PROPERTY_GROK_PATTERNS_SUFFIX, defaultProperties.getProperty(FILE_PROPERTY + FILE_PROPERTY_GROK_PATTERNS_SUFFIX));

		this.pollMillis = pollMillis;

		this.logMessageFormatString = logMessageFormatString;

		//this.properties = PropertiesUtil.getProperties(propertiesFileName, "^file\\." + threadID + "\\.*");
		this.properties = properties;
		this.defaultProperties = defaultProperties;
		this.filePrefix = filePrefix;

	}

	public void run()
	{
        Path filePath = Paths.get(filePathString);
        File file = filePath.toFile();

        grok.set(initializeGrok(grokPatternsFile));

		String x = properties.getProperty(filePrefix + FILE_PROPERTY_TIMESTAMP_FORMAT_SUFFIX, defaultProperties.getProperty(FILE_PROPERTY + FILE_PROPERTY_TIMESTAMP_FORMAT_SUFFIX));

//		Pattern headerPattern = getLogMessagePattern(x);
//
//		System.err.println("logMessageFormatString: " + getLogMessagePattern(logMessageFormatString).toString());

      //  System.err.println(file.exists());

		//	Creates log tailer
//		TailerListenerAdapterImpl listener = new TailerListenerAdapterImpl(threadID, logMessageFormatString, false, getLogMessagePattern(x), filePathString.replace("\\", "/"));
		TailerListenerAdapterImpl listener = new TailerListenerAdapterImpl(threadID, logMessageFormatString, false, getLogMessagePattern(x), filePathString.replace("\\", "\\\\"));

		//	Extended Tailer that allows the daemon thread to be named
		//	Otherwise functionality the same as Apache version
		NameableTailer.create(file, listener, pollMillis, true, this.getClass().getSimpleName() + "Thread" + "-" + threadID);

		//	Creates thread that watches for filesystem events
		//	Without this thread or an infinite loop, the app would exit
//        Thread watchServiceThread = new Thread(new WatchServiceInstance(threadID, directory, file), "WatchServiceThread" + "-" + threadID);
//        watchServiceThread.start();

		while (true)
		{

		}
	}

    class TailerListenerAdapterImpl extends TailerListenerAdapter
    {
    	private boolean haveMd5String = false;
    	private int linesRead = 0;
    	private String md5String = new String();
		private Integer threadID;
		private Integer counter = 0;

		private static final int TIMEOUT = 5000;
		private Pattern headerPattern;
		private Pattern logMessagePattern;
		private ConcurrentHashMap<String, String> contentAttributesMap;

//		private static ThreadLocal <String> logMessage = new ThreadLocal <String>()
//		{
//		    @Override
//		    protected String initialValue()
//		    {
//		        return new String();
//		    }
//		};

		private String logMessage = new String();
		private String contentEntryPrefix;
		private String pathString;
		private String hostname;

		public TailerListenerAdapterImpl(Integer threadID, String logMessageFormatString, boolean b, Pattern headerPattern, String pathString)
		{
			this.threadID = threadID;

			pauseProcessing.set(false);

			this.hostname = LoggingUtils.getHostName();

			//System.err.println("headerP: " + headerP.toString());

			this.headerPattern = headerPattern;

			this.logMessagePattern = getLogMessagePattern(logMessageFormatString);

			this.contentAttributesMap = new ConcurrentHashMap<>();

			Integer entryCount = 0;

			this.contentEntryPrefix = "content.entry[" + entryCount + "]";

			this.pathString = pathString;


//			System.err.println("DATE_PATTERN: " + DATE_PATTERN.toString());

			//getConversionPattern(DATE_PATTERN, x, DATE_PATTERN.toString());

//			System.err.println(getLogMessagePattern(x));

//			System.err.println(getLogMessagePattern(logMessageFormatString));

//			System.err.println(getTimeStampPattern(false));

			//this.logMessagePattern = getLogMessagePattern(logMessagePatternString);

			//System.err.println(logMessagePattern.toString());

		}

		@Override
        public void handle(String line)
        {
//			System.err.println("processLine: false");

//			String x = properties.getProperty(filePrefix + FILE_PROPERTY_TIMESTAMP_FORMAT_SUFFIX, defaultProperties.getProperty(FILE_PROPERTY + FILE_PROPERTY_TIMESTAMP_FORMAT_SUFFIX));

			//System.err.println(getLogMessagePattern(x));

//			System.err.println("headerPattern: " + headerPattern.get().toString());

			counter++;

			processLine(line, logger, linesRead, false);

//			System.err.println("haveMd5String: " + haveMd5String);

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

/*
        @Override
        public void fileRotated()
        {
//        	dumpInfo();

        	System.err.println("************** File rotated " + " **************");

//        	FILE_BUFFER_QUEUE_MAP.put(fileNumber, new ConcurrentLinkedQueue<String>());
//        	FILE_RECOVER_QUEUE_MAP.put(fileNumber, new ConcurrentLinkedQueue<String>());

//        	CopyOnWriteArrayList<ConcurrentLinkedQueue<String>> recoveryArrayList = new CopyOnWriteArrayList<ConcurrentLinkedQueue<String>>();
//
//            recoveryArrayList.add(0, new ConcurrentLinkedQueue<String>());
//            recoveryArrayList.add(1, new ConcurrentLinkedQueue<String>());

            //RECOVER_QUEUE_MAP.put(fileNumber, recoveryArrayList);

        	//	Only process when a log fills up
        	//	Handles case where <OnStartupTriggeringPolicy/> is set for log4j2 in source
            if (haveMd5String)
            {
            	pauseProcessing.set(true);
//            	inRecoveryMode.set(false);

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
*/

		private void assembleLogMessage(final Logger logger, String line)
        {
    		class LogMessageTimer extends TimerTask
    		{
				public void run()
    			{
//    				System.err.println("##### TIMEOUT");

    				publishLogMessage(logMessage, logger);

        			logMessage = new String();
    			}
    		}

    		if (logMessageTimerActive.get())
    		{
    			logMessageTimer.get().cancel();
    			logMessageTimerActive.set(false);
    		}

    		boolean headerFound = headerPattern.matcher(line).find();

//    		System.err.println("headerFound: " + headerFound);

//        	line = counter.toString() + ": " + line.concat("\n");
        	line = line.concat("\n");

        	if (headerFound)
        	{
        		if (logMessage.length() > 0)
        		{
//       			System.err.println("##### HEADER");
       				publishLogMessage(logMessage, logger);
        		}

        		logMessage = line;

        		//	Start the timer when the header is found
        		startLogMessageTimer(new LogMessageTimer(), TIMEOUT);
        	}
        	else
        	{
        		logMessage = logMessage.concat(line);

//        		System.err.println("startLogMessageTimer2: " + logMessage);

       			startLogMessageTimer(new LogMessageTimer(), TIMEOUT);
        	}
        }

        private void publishLogMessage(String message, Logger logger)
        {
        	String levelName = null;
        	String timeStampStr = null;
        	String threadName = null;
        	String loggerName = null;
        	String loggingMessage = null;

        	Matcher messsageMatcher = logMessagePattern.matcher(message);

        	messsageMatcher.find();

//        	System.err.println("*****" + message + "*****\n");

        	if (messsageMatcher.groupCount() > 0)
        	{
        		try
        		{
    		    	levelName = messsageMatcher.group("loglevel");

    		    	timeStampStr = messsageMatcher.group("timestamp");

    		    	threadName = messsageMatcher.group("log4jThread");

    		    	loggerName = messsageMatcher.group("log4jCategory");

    		    	loggingMessage = messsageMatcher.group("log4jMessage").trim();

    				Integer contentCount = 1;

    				String _id = UUID.randomUUID().toString();
    				String timeStampStrUTC = DateTimeUtils.getJodaDateTimeUTC(timeStampStr).toString();

    				contentAttributesMap.put("content.modifiedBy", Constants.APP_FRIENDLY_NAME);
    				contentAttributesMap.put("content.apiVersion", Constants.API_VERSION);
    				contentAttributesMap.put("content.count", contentCount.toString());
    				contentAttributesMap.put("content.transactionStatus", MessageStatusEnum.STATUS_ACTIVE.getMessageStatusEnum());

    				contentAttributesMap.put("_id", _id);

    				contentAttributesMap.put("content.id", _id);

    				//	TODO: TZ config?
    				contentAttributesMap.put("content.createDate", timeStampStrUTC);
    				contentAttributesMap.put("content.modifiedDate", timeStampStrUTC);

    				String entryID = UUID.randomUUID().toString();

    				contentAttributesMap.put(contentEntryPrefix + ".sourceURI", "file://" + hostname + "/" + pathString);
    				contentAttributesMap.put(contentEntryPrefix + ".sourceLocation", hostname);
    				contentAttributesMap.put(contentEntryPrefix + ".correlationId", entryID);
    				contentAttributesMap.put(contentEntryPrefix + ".id",  entryID);
    				contentAttributesMap.put(contentEntryPrefix + ".contentType", MediaType.JSON_UTF_8.toString());
    				contentAttributesMap.put(contentEntryPrefix + ".codec", Codecs.JSON.toString());
    				contentAttributesMap.put(contentEntryPrefix + ".sequence", "1");

    		    	JsonBuilder jsonBuilder = new JsonBuilder();

    		    	HashMap<String, String> logMessageMap = new HashMap<>();

//    		    	TODO: convert "?
    		    	//logMessageMap.put("loggingMessage", loggingMessage.replace("\"", "'").replace("\0", " "));
    		    	logMessageMap.put("loggingMessage", StringEscapeUtils.escapeJson(loggingMessage.replace("\"", "'")));


    		    	jsonBuilder.call(logMessageMap);

    		    	contentAttributesMap.put(contentEntryPrefix + ".payload", jsonBuilder.toString());

    				for (Entry<String, String> entry : contentAttributesMap.entrySet())
    				{
    					ThreadContext.put(entry.getKey(), entry.getValue());
    				}

    		    	//logger.log(Level.getLevel(levelName), new LogGenieMessage(loggingMessage, DateTimeUtils.getJodaDateTimeUTC(timeStampStr), loggerName, threadName, ""));

    				ThreadContext.clearMap();

    				contentAttributesMap.clear();
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

        	if (logMessageTimerActive.get())
        	{
//            	System.err.println("+++++ Timer canceled: publishLogMessage\n");
        		logMessageTimer.get().cancel();

        		logMessageTimerActive.set(false);
        	}

        	logMessage = new String();
        }

        private void startLogMessageTimer(TimerTask timerTask, int timeout)
        {
//    		System.err.println("+++++ startLogMessageTimer");

        	if (logMessageTimerActive.get())
        	{
        		logMessageTimer.get().cancel();
        	}

    		logMessageTimer.set(new Timer());
    		logMessageTimerActive.set(true);

    		logMessageTimer.get().schedule(timerTask, timeout);
        }

        private void processLine(String line, Logger logger, int lineNumber, boolean isRecoverLine)
        {
//        	ConcurrentLinkedQueue<String> fileBufferQueue = FILE_BUFFER_QUEUE_MAP.get(fileNumber);
//        	ConcurrentLinkedQueue<String> fileRecoveryQueue = FILE_RECOVER_QUEUE_MAP.get(fileNumber);

        	if (!pauseProcessing.get())
        	{
            	if (inRecoveryMode.get())
            	{
//            		while (!fileRecoveryQueue.isEmpty())
//            		{
//            			assembleLogMessage(logger, fileRecoveryQueue.remove());
//            		}
//
        			inRecoveryMode.set(false);
            	}

            	//System.err.println(line);

//    			while (!fileBufferQueue.isEmpty())
//    			{
//    				assembleLogMessage(logger, fileBufferQueue.remove());
//    			}
//
    			assembleLogMessage(logger, line);
        	}
        	else
        	{
//        		if (!isRecoverLine)
//        		{
//        			sendToBufferQueue(fileBufferQueue, line, logger);
//        		}
//        		else
//        		{
//        			sendToRecoveryQueue(fileRecoveryQueue, line, logger);
//        		}
        	}
        }

        private String getMd5(String md5)
        {
        	return DigestUtils.md5Hex(md5);
        }

        private Pattern getTimeStampPattern(boolean returnGroup)
        {
//        	String basePattern = "\\[\\d{4}-(?:0[1-9]|1[0-2])-(?:[12]\\d|0[1-9]|3[01])T(?:(?:[0,1][0-9]|2[0-3])(?::[0-5][0-9]){2})\\,[0-9]{3}\\]";
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

    }

	private Grok initializeGrok(String patternFileName)
	{
		Grok grok = null;

        Path patternFilePath = null;

        try
        {
        	patternFilePath = Paths.get(ClassLoader.getSystemResource(patternFileName).toURI());
		}
        catch (URISyntaxException e1)
        {
			e1.printStackTrace();
		}

		try
		{
			grok = Grok.create(patternFilePath.toString());
		}
		catch (GrokException e)
		{
			throw new RuntimeException();
		}

		return grok;
	}

	private Pattern getLogMessagePattern(String patternString)
	{
//		String breakString = StringUtils.repeat("=", patternString.length());
//		System.out.println("\n" + breakString);
//		System.out.println(patternString);
//		System.out.println(breakString);

		TreeMap<String, Pattern> patternTreeMap = new TreeMap<>();

		ClassLoader classLoader = LogAgent.class.getClassLoader();

		String loggingTypePackage = "com.oaksoft.logging.pattern.log4j.";
		String loggingType = "Log4jGrok";
		String loggingTypeSuffix = "Patterns";

		//Grok grok = initializeGrok(grokPatternsFile);

		//Grok grok1 = grok.get();

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
			grok.get().compile(GrokUtils.buildGrokPattern(patternString));
		}
		catch (GrokException e)
		{
			e.printStackTrace();
		}

		String grokPatternString = grok.get().getNamedRegex();

		return Pattern.compile(grokPatternString);
	}

	private void getConversionPattern(Pattern pattern, String patternString, String type)
	{
		Matcher matcher = pattern.matcher(patternString);

		if (matcher.find())
		{
			String conversionPatternString = matcher.group(0);

			try
			{
				if (!(matcher.group("formatModifier") == null))
				{
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
					type = type.concat(String.valueOf(ETX_DELIMITER)).concat(matcher.group("specifier")).concat(String.valueOf(ETX_DELIMITER));
				}
			}
			catch (IllegalArgumentException e)
			{

			}

			conversionPatterns.put(type, conversionPatternString);
		}
	}
}

