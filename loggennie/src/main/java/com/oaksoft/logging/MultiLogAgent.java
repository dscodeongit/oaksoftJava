package com.oaksoft.logging;

import java.util.Enumeration;
import java.util.Properties;

import com.oaksoft.util.properties.PropertiesUtil;

public class MultiLogAgent
{
	public static final String FILE_PROPERTY = "file";
	private static final String FILE_PROPERTY_POLLING_SUFFIX = ".poll_millis";
	public static final String FILE_PROPERTY_FILE_NAME_SUFFIX = ".name";
	private static final String FILE_PROPERTY_LOG_MESSAGE_FORMAT_SUFFIX = ".logMessageFormat";

	public static void main(String[] args) throws Exception
    {
    	String propertiesFileName = new String();

    	if (args.length == 0)
    	{
    		throwUnsupportedException("No parameters specified.");
    	}
    	else
    	{
    		propertiesFileName = args[0];

    		Properties fileNames = PropertiesUtil.getProperties(propertiesFileName, "^" + FILE_PROPERTY + "\\.[0-9]+\\.name");

    		//	Negative lookahead (?![0-9]+) to exclude numbered file properties
    		Properties defaultProperties = PropertiesUtil.getProperties(propertiesFileName, "^" + FILE_PROPERTY + "\\." + "(?![0-9]+)");

    		String defaultFilePollingRateString = defaultProperties.getProperty(FILE_PROPERTY + FILE_PROPERTY_POLLING_SUFFIX);
    		String defaultLogMessageFormatString = defaultProperties.getProperty(FILE_PROPERTY + FILE_PROPERTY_LOG_MESSAGE_FORMAT_SUFFIX);

    		for (Enumeration<?> e = fileNames.propertyNames(); e.hasMoreElements();)
    		{
    			String filePropertyName = (String) e.nextElement();

    			String filePrefix = filePropertyName.replace(FILE_PROPERTY_FILE_NAME_SUFFIX, "");

    			String fileIndexString = filePrefix.replace(FILE_PROPERTY + ".", "");

    			Properties fileProperties = PropertiesUtil.getProperties(propertiesFileName, "^" + filePrefix + "\\.*");

    			String filePollingRateString = fileProperties.getProperty(filePrefix + FILE_PROPERTY_POLLING_SUFFIX, defaultFilePollingRateString);
    			String logMessageFormatString = fileProperties.getProperty(filePrefix + FILE_PROPERTY_LOG_MESSAGE_FORMAT_SUFFIX, defaultLogMessageFormatString);

    			Integer filePollingRate = Integer.parseInt(filePollingRateString);

        		Integer fileIndex = Integer.parseInt(fileIndexString);

//        		new Thread(new TailerInstance(fileIndex, defaultProperties, fileProperties, filePrefix, filePollingRate, logMessagePatternString), "TailerInstance-" + fileIndexString).start();

                //String threadName = "TailerThread-" + fileIndex;
                Thread tailerThread = new Thread(new TailerInstance(fileIndex, defaultProperties, fileProperties, filePrefix, filePollingRate, logMessageFormatString), "TailerInstance-" + fileIndexString);

                //	Kicks everything off
            	tailerThread.start();

            	System.err.println("Tailing logfile: " + fileProperties.getProperty(filePrefix + FILE_PROPERTY_FILE_NAME_SUFFIX));
    		}

//    		while (true)
//    		{
//
//    		}
    	}
    }

	private static void throwUnsupportedException(String exceptionMessage)
	{
		throw new UnsupportedOperationException(exceptionMessage);
	}
}
