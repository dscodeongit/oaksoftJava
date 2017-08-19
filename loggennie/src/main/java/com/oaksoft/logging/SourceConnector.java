package com.oaksoft.logging;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.oaksoft.enums.SourceType;
import com.oaksoft.logging.config.CollectorFactory;

public class SourceConnector
{
	private static final Logger logger = LogManager.getLogger();
	
	private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	private static final ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");

	private static final long LOGGER_FAILURE_SCAN_INTERVAL = 1;
	    
    public static void connect(final String configFile, final SourceType sourceType){    	
    	
    	logger.info(new ParameterizedMessage("Initializing Source connection from configration [{}] for type [{}] ", configFile, sourceType ));   	
    	/*
    	if (Pattern.matches(getPatternString(CONFIG_TYPE_XML), configFile))
    	 {
    	 */
    	new Thread (() -> {
    		try {
    			final Collection<DataCollector> collectors = CollectorFactory.buildFromConfig(configFile, sourceType);
    			if(CollectionUtils.isNotEmpty(collectors)) {
    		
    		    	for (DataCollector collector: collectors)
    		    	{
    		    		new Thread (() -> {
    		    			collector.start();
    		    			}).start();
    		    	}
    	    	}
    	    	scanForServerFailure(collectors);    	    	
    	    	
    	    	Runtime.getRuntime().addShutdownHook(new Thread(){
    				@Override
    				public void run() {
    					logger.info("Shutting down Loggenie ... ");
    					for (DataCollector dataLogger : collectors) {
    						dataLogger.stop();
    					}
    					((ConfigurableApplicationContext)context).close();
    					logger.info("Shut down complete");
    				}
    			});   
			} catch (IOException e) {
				logger.error("Server configuration file not found: " + configFile, e);	
				System.exit(1);
			}
    	}).start();
    	/*
    	}
    	else if (Pattern.matches(getPatternString(CONFIG_TYPE_JSON), configFile))
		{
    		System.err.println("JSON");
        	logger.error("JSON Configuration type is not supported yet. Service exiting ... ");
        	System.exit(1);
		}   
		*/ 	
    	
    }

    private static void scanForServerFailure(Collection<DataCollector> collectors) {
		service.scheduleAtFixedRate(() -> scan(collectors), 1, LOGGER_FAILURE_SCAN_INTERVAL, TimeUnit.MINUTES);
	}
    
    private static void scan(Collection<DataCollector> collectors){
    	for (DataCollector collector : collectors) {
			if(collector.needsRestart()){
				logger.info("Re-srarting: " + collector);
				collector.reStart();
			}
		}
    }

	private static String getPatternString(String configType)
    {
    	return ".*\\." + configType + "$";
    }
}
