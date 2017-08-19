package com.oaksoft.logging.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.oaksoft.enums.SourceType;
import com.oaksoft.logging.DataCollector;
import com.oaksoft.logging.jdbc.JdbcCollector;
import com.oaksoft.logging.jdbc.JdbcServerConfig;
import com.oaksoft.logging.jms.TibcoCollector;
import com.oaksoft.logging.jms.TibcoXmlConfig;
import com.oaksoft.logging.jmx.JmxCollector;
import com.oaksoft.logging.jmx.JmxServerConfig;
import com.oaksoft.logging.river.MongoRiverCollector;
import com.oaksoft.logging.river.MongoRiverConfig;

@Component
public class CollectorFactory {
	
	private static String API_VERSION;
	private static ThreadPoolTaskScheduler SERVICE;
	public static Collection<DataCollector> buildFromConfig(final String configFile, SourceType sourceType) throws IOException{
		Collection<DataCollector> collectors = Lists.newArrayList();
		switch (sourceType) {
			case TIBCO: 
				Collection<TibcoXmlConfig> tibcoConfigs = TibcoXmlConfig.parseConfig(configFile);
				for (TibcoXmlConfig config : tibcoConfigs) {
					config.setApiVersion(API_VERSION);
					DataCollector collector = new TibcoCollector(config, SERVICE);
					collectors.add(collector);
				}
				break;
			case JMX:
				Collection<JmxServerConfig> jmxConfigs = JmxServerConfig.parseConfig(configFile);
				for (JmxServerConfig config : jmxConfigs) {
					config.setApiVersion(API_VERSION);
					DataCollector logger = new JmxCollector(config, SERVICE);
					collectors.add(logger);
				}
				break;
			case JDBC: 
				Collection<JdbcServerConfig> jdbcConfigs = JdbcServerConfig.parseConfig(configFile);
				for (JdbcServerConfig config : jdbcConfigs) {
					config.setApiVersion(API_VERSION);
					DataCollector logger = new JdbcCollector(config, SERVICE);
					collectors.add(logger);
				}
				break;
			case RIVER: 
				Collection<MongoRiverConfig> riverConfigs = MongoRiverConfig.parseConfig(configFile);
				for (MongoRiverConfig config : riverConfigs) {
					config.setApiVersion(API_VERSION);
					MongoRiverCollector logger = new MongoRiverCollector(config);
					collectors.add(logger);
				}
				break;
			default: 
				throw new IllegalArgumentException("SourceType is not valid: " + sourceType);		
		}		
		return collectors;
	}
	
	@Value("${app.version}")
    public void setApiVersion(String apiVersion) {
		API_VERSION = apiVersion;
    }
	
	@Autowired
	public void setScheduler(ThreadPoolTaskScheduler service){
		SERVICE = service;
	}
}
