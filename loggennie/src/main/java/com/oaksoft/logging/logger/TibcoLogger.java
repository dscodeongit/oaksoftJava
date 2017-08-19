package com.oaksoft.logging.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Booleans;

import com.oaksoft.enums.SourceType;
import com.oaksoft.logging.SourceConnector;
import com.oaksoft.logging.jms.TibcoCollector;

@Plugin(name = "TibcoLogger", category = Node.CATEGORY, printObject = true)
public class TibcoLogger extends LoggerConfig{
    private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger();
	
    @PluginFactory
    public static LoggerConfig createLogger(
            @PluginAttribute("additivity") final String additivity,
            @PluginAttribute("level") final Level level,
            @PluginAttribute("includeLocation") final String includeLocation,           
            @PluginAttribute("sourceConfig") final String sourceConfig,
            @PluginElement("AppenderRef") final AppenderRef[] refs,
            @PluginElement("Properties") final Property[] properties,
            @PluginConfiguration final Configuration config,
            @PluginElement("Filter") final Filter filter) {
    	
    	SourceConnector.connect(sourceConfig, SourceType.TIBCO);
    	    	
		Level actualLevel = (level == null) ? Level.ERROR : level;
		boolean additive = Booleans.parseBoolean(additivity, true);
		return LoggerConfig.createLogger(additive, actualLevel, TibcoCollector.class.getPackage().getName(), includeLocation, refs, properties, config, filter);
               
    }
}
