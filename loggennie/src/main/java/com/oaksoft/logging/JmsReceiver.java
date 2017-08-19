package com.oaksoft.logging;

import static com.oaksoft.logging.jms.AbstractJmsConfig.ACTIVE_MQ_TYPE;
import static com.oaksoft.logging.jms.AbstractJmsConfig.CONFIG_TYPE_JSON;
import static com.oaksoft.logging.jms.AbstractJmsConfig.CONFIG_TYPE_XML;
import static com.oaksoft.logging.jms.AbstractJmsConfig.TIBCO_TYPE;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Pattern;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oaksoft.commons.configuration.XMLConfigurationUtil;
import com.oaksoft.logging.jms.AbstractJmsLogger;

public class JmsReceiver
{
	private static final Logger logger = LogManager.getLogger();

	private static final String JMS_SERVER_NAME = "jmsServer";
	private static final String JMS_SERVER_POLL_RATE = "jmsServerPollRate";

	private static final String JMS_SERVER_CONFIG_DEFAULT = "jms-servers.xml";

	
	private static final Class<AbstractJmsLogger> BASE_LOGGER_CLASS = AbstractJmsLogger.class;
	private static final Package BASE_LOGGER_PACKAGE = BASE_LOGGER_CLASS.getPackage();
	
	private static Integer dataPollingThreadPoolSize = 5;

	private static final ArrayList<String> JMS_TYPES = new ArrayList<String>() {
		private static final long serialVersionUID = 1740612323480651490L;
		{
			add(TIBCO_TYPE);
			add(ACTIVE_MQ_TYPE);
		}
	};

	private static final ArrayList<AbstractJmsLogger> JMS_LOGGER_ARRAYLIST = new ArrayList<>();

    public static void main(String[] args) throws FileNotFoundException
    {
    	logger.info("JmsReceiver starting ...");
    	String configFile = JMS_SERVER_CONFIG_DEFAULT;
    	if(args.length !=0 ){
    		configFile = args[0];
    	}
    	
    	if(args.length > 1){
    		dataPollingThreadPoolSize = Integer.valueOf(args[1]);
    	}
    	
    	logger.info("Loading EMS server configuration from : " + configFile);
    	
    	if (Pattern.matches(getPatternString(CONFIG_TYPE_XML), configFile))
    	{
    		handleXmlConfig(configFile);
    	}
    	else if (Pattern.matches(getPatternString(CONFIG_TYPE_JSON), configFile))
		{
    		System.err.println("JSON");
        	logger.error("JSON Configuration type is not supported yet. Service exiting ... ");
        	System.exit(1);
		}

    	for (AbstractJmsLogger jmsLogger: JMS_LOGGER_ARRAYLIST)
    	{
			jmsLogger.start();				

    	}
	}


    private static String getPatternString(String configType)
    {
    	return ".*\\." + configType + "$";
    }

    private static void handleXmlConfig(String configFile) throws FileNotFoundException
    {
		XMLConfiguration xmlConfiguration = XMLConfigurationUtil.getXMLConfiguration(configFile);

		HierarchicalConfiguration hierarchicalConfiguration;
		
		ScheduledExecutorService service = Executors.newScheduledThreadPool(dataPollingThreadPoolSize);

		for (String type: JMS_TYPES)
		{
			try
			{
				hierarchicalConfiguration = XMLConfigurationUtil.getSubConfiguration(xmlConfiguration, "JMS.".concat(type));

        		if (!hierarchicalConfiguration.isEmpty())
        		{
        			logger.trace(type + " XML config found");

        			ArrayList<ConfigurationNode> configurationNodesArrayList = XMLConfigurationUtil.parseHierarchicalConfiguration(hierarchicalConfiguration, "ConnectionNode");

        			for (ConfigurationNode configurationNode: configurationNodesArrayList)
        			{
        				Class<?> jmsLoggerClass = Class.forName(BASE_LOGGER_PACKAGE.getName() + "." + type + "Logger");

//        				System.err.println(jmsLoggerClass.getCanonicalName());
        				logger.info("Logger to use: " + jmsLoggerClass.getCanonicalName());

        				Constructor<?> jmsLoggerConstructor = jmsLoggerClass.getConstructor(ConfigurationNode.class, String.class, ScheduledExecutorService.class);

        				Object jmsLogger = jmsLoggerConstructor.newInstance(configurationNode, CONFIG_TYPE_XML, service);

        				JMS_LOGGER_ARRAYLIST.add(BASE_LOGGER_CLASS.cast(jmsLogger));

//        				TibcoXmlConfig tibcoXmlConfig = new TibcoXmlConfig(configurationNode);

//        				tibcoLogger.getInstance(configurationNode);
//
//        				String ancestor = AbstractJmsConfig.getAncestor();
        			}
        		}
			}
			catch (IllegalArgumentException e)
			{
				logger.trace(type + " XML config not found - skipping");
			}
			catch (InstantiationException e)
			{
				logger.error("Error occured: ", e);
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				logger.error("Error occured: ", e);
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				logger.error("Error occured: ", e);
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				logger.error("Error occured: ", e);
				e.printStackTrace();
			}
			catch (SecurityException e)
			{
				logger.error("Error occured: ", e);
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				logger.error("Error occured: ", e);
				e.printStackTrace();
			}
		}
    }
}
