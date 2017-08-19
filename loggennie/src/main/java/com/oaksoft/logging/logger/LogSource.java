package com.oaksoft.logging.logger;

import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(name = "TibcoLoge", category = "Core", elementType = "source", printObject = true)
public class LogSource extends AbstractLifeCycle{
	
    private static final long serialVersionUID = 1L;
    
    private LogSource(final String configFile, final int retryLimit, final long retryInterval){
    	
    }

    @PluginFactory
    public static LogSource createLogSource(
            
            @PluginAttribute("sourceServerConfig") final String configFile,
            @PluginAttribute("connectRetryLimit") final int retryLimit,
            @PluginAttribute("connectRetryInterval") final long retryInterval) {
        
    	return new LogSource(configFile, retryLimit, retryInterval);
      
    }
	

    @Override
    public void stop() {
        super.stop();
        
    }
}
