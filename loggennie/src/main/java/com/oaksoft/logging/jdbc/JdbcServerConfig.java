package com.oaksoft.logging.jdbc;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oaksoft.commons.configuration.XMLConfigurationUtil;
import com.oaksoft.enums.SourceType;
import com.oaksoft.logging.config.SourceConfig;

public class JdbcServerConfig extends SourceConfig{
	
	private String driver;
	private int minPoolsize;
	private int maxPoolsize;
	private int connectionTimeout;
	private Map<String, Object> options;
		
	private JdbcServerConfig(){
		this.type = SourceType.JDBC;
	}	
	
	@Override
	protected void parseServerSpecificDetails(ConfigurationNode attrNode){
		String value = String.valueOf(attrNode.getValue());
		if(attrNode.getName().equals("driver")){
			setDriver(value);
		}else if(attrNode.getName().equals("min-poolsize")){
			setMinPoolsize(Integer.valueOf(value));
		}else if(attrNode.getName().equals("max-poolsize")){
			setMaxPoolsize(Integer.valueOf(value));
		}else if(attrNode.getName().equals("conn-timeout")){
			setConnectionTimeout(Integer.valueOf(value));
		}else if(attrNode.getName().equals("url")){
			setUrl(value);
			setHost(value);
		}		
	}
	
	public static Collection<JdbcServerConfig> parseConfig(String  configFile) throws FileNotFoundException{
		XMLConfiguration xmlConfiguration = XMLConfigurationUtil.getXMLConfiguration(configFile);

		HierarchicalConfiguration hierarchicalConfiguration = XMLConfigurationUtil.getSubConfiguration(xmlConfiguration, SourceType.JDBC.name());
		List<JdbcServerConfig> configs = Lists.newArrayList();
		
		if (!hierarchicalConfiguration.isEmpty())
		{
			List<ConfigurationNode> connectionNodes = XMLConfigurationUtil.getDescendentsforName(hierarchicalConfiguration, "ConnectionNode");

			for (ConfigurationNode configurationNode: connectionNodes)
			{    
				JdbcServerConfig config = new JdbcServerConfig();
			
				config.parseConnectionDetails(configurationNode);
				config.setHost(config.getAlias());
				config.setOptions(parseConnOptions(configurationNode));
				
				config.setMonitors(getMonitors(configurationNode));
				
				configs.add(config);
			}
		}			
		
		return configs;		
	}
	
	private static Map<String, Object> parseConnOptions(ConfigurationNode configurationNode){
		Map<String, Object> options = Maps.newHashMap();
		List<ConfigurationNode> optionsNodes = configurationNode.getChildren("DBOptions");
		if(optionsNodes != null && !optionsNodes.isEmpty()){
			List<ConfigurationNode> optionsEntries  = optionsNodes.get(0).getChildren("entry");
			if(optionsEntries != null && !optionsEntries.isEmpty()) {
				for (ConfigurationNode confNode : optionsEntries) {
					List<ConfigurationNode> attrNodes = confNode.getAttributes();
					if(attrNodes != null && !attrNodes.isEmpty()) {
						String key = null;
						String value = null;
						for (ConfigurationNode attrNode : attrNodes) {
							if(attrNode.getName().equals("key")){
								key = String.valueOf(attrNode.getValue());
							} else if(attrNode.getName().equals("value")){
								value = String.valueOf(attrNode.getValue());
							}
						}
						
						if(StringUtils.isNoneBlank(key) && StringUtils.isNoneBlank(value)){
							options.put(key, value);
						}
					}	
				}
			}			
		}
		return options;
	}
	
	private static Collection<JdbcMonitor> getMonitors(ConfigurationNode configurationNode) {
		
		List<ConfigurationNode> monitors = configurationNode.getChildren("Monitors");
		Collection<JdbcMonitor> allMonitors = Lists.newArrayList();
				
		if(!CollectionUtils.isEmpty(monitors)){		
			List<ConfigurationNode> monitorList = monitors.get(0).getChildren("Monitor");
			
			for (ConfigurationNode mon : monitorList) {
				List<ConfigurationNode> attrNodes = mon.getAttributes();
				String monName = null;
				String type = null;
				for (ConfigurationNode attr : attrNodes) {
					if("name".equals(attr.getName())){
						monName = String.valueOf(attr.getValue());
					} else if ("type".equals(attr.getName())) {
						type = String.valueOf(attr.getValue());
					}
				}
				List<ConfigurationNode> queryList =  mon.getChildren("query");
				
				//List<ConfigurationNode> queryList = XMLConfigurationUtil.getDescendentsforName(mon, "ConnectionNode");
				if(CollectionUtils.isNotEmpty(queryList)){
					Set<String> queries = Sets.newHashSet();
					for (ConfigurationNode confNode : queryList) {
						String query = String.valueOf(confNode.getValue());						
						queries.add(query);						
					}
					JdbcMonitor monitor = new JdbcMonitor(monName, type, queries);
					allMonitors.add(monitor);
				}				
			}
		}
		
		return allMonitors;
	}
	
	private void setDriver(String driver) {
		this.driver = driver;
	}


	private void setMinPoolsize(int minPoolsize) {
		this.minPoolsize = minPoolsize;
	}


	private void setMaxPoolsize(int maxPoolsize) {
		this.maxPoolsize = maxPoolsize;
	}

	public String getDriver() {
		return driver;
	}

	public int getMinPoolsize() {
		return minPoolsize;
	}

	public int getMaxPoolsize() {
		return maxPoolsize;
	}

	public Map<String, Object> getOptions() {
		return options;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	private void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void setOptions(Map<String, Object> options) {
		this.options = options;
	}
}
