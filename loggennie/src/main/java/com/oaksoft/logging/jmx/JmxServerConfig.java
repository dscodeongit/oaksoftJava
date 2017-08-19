package com.oaksoft.logging.jmx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oaksoft.commons.configuration.XMLConfigurationUtil;
import com.oaksoft.enums.SourceType;
import com.oaksoft.logging.config.SourceConfig;
import com.oaksoft.logging.jmx.JmxMonitor.Metric;

public class JmxServerConfig extends SourceConfig{
	protected static Logger logger = LogManager.getLogger(JmxServerConfig.class);
	private static final SourceType JMX_CONFIG_TAG = SourceType.JMX;
	private static final String MEASURE_SEPARATOR = "|";
	private static final String MBEAN_TYPE_DEFAULT = "java.lang";
	
	private JmxServerConfig(){
		this.type = SourceType.JMX;
	}	
	
	@Override
	protected void parseServerSpecificDetails(ConfigurationNode attrNode){
		String value = String.valueOf(attrNode.getValue());
		if(attrNode.getName().equals("path")){
			setPath(value);
		}else if(attrNode.getName().equals("url")){
			setUrl(value);
			setHost(StringUtils.substringBefore(value, ":"));
			setPort(Integer.valueOf(StringUtils.substringAfterLast(value, ":")));
		}
	}
	
	public static Collection<JmxServerConfig> parseConfig(String  configFile) throws FileNotFoundException{
		XMLConfiguration xmlConfiguration = XMLConfigurationUtil.getXMLConfiguration(configFile);

		HierarchicalConfiguration hierarchicalConfiguration = XMLConfigurationUtil.getSubConfiguration(xmlConfiguration, JMX_CONFIG_TAG.name());
		List<JmxServerConfig> configs = Lists.newArrayList();
		
		if (!hierarchicalConfiguration.isEmpty())
		{
			List<ConfigurationNode> connectionNodes = XMLConfigurationUtil.getDescendentsforName(hierarchicalConfiguration, "ConnectionNode");

			for (ConfigurationNode configurationNode: connectionNodes)
			{    
				JmxServerConfig config = new JmxServerConfig();
				config.type = JMX_CONFIG_TAG;
				
				config.parseConnectionDetails(configurationNode);
				
				config.setMonitors(getMonitors(configurationNode, config));
				
				configs.add(config);
			}
		}			
		
		return configs;		
	}
	
	private static Collection<JmxMonitor> getMonitors(ConfigurationNode configurationNode, JmxServerConfig config) {
		
		List<ConfigurationNode> monitors = configurationNode.getChildren("Monitors");
		Collection<JmxMonitor> allMonitors = Lists.newArrayList();
				
		if(!CollectionUtils.isEmpty(monitors)){		
			List<ConfigurationNode> monitorList = monitors.get(0).getChildren("Monitor");
			
			for (ConfigurationNode mon : monitorList) {
				List<ConfigurationNode> attrNodes = mon.getAttributes();
				String monName = null;
				String type = null;
				String mbeanType = MBEAN_TYPE_DEFAULT;
				for (ConfigurationNode attr : attrNodes) {
					if("name".equals(attr.getName())){
						monName = String.valueOf(attr.getValue());
					} else if ("type".equals(attr.getName())) {
						type = String.valueOf(attr.getValue());
					} else if ("mbeanType".equals(attr.getName())){
						mbeanType = String.valueOf(attr.getValue());
					}
				}
				
				List<ConfigurationNode> metrics =  mon.getChildren("Metrics");
				Collection<Metric> allMetrics = Lists.newArrayList();

				if(CollectionUtils.isNotEmpty(metrics)){
					List<ConfigurationNode> metricList = metrics.get(0).getChildren("Metric");
					
					for (ConfigurationNode confNode : metricList) {
						List<ConfigurationNode> aNodes = confNode.getAttributes();
						String metricName = null;
						Boolean isCompType = false;
						Collection<String> measures = Collections.emptyList();
						for (ConfigurationNode attr : aNodes) {
							if("name".equals(attr.getName())){
								metricName = String.valueOf(attr.getValue());
							} else if ("compositeType".equals(attr.getName())) {
								isCompType = Boolean.valueOf(String.valueOf(attr.getValue()));
							} else if ("measures".equals(attr.getName())) {
								String value = String.valueOf(attr.getValue());
								if(StringUtils.isNotBlank(value)) {
									measures = Arrays.asList(StringUtils.split(value, MEASURE_SEPARATOR));
								}							
							}
						}
						
						Metric met = new Metric(metricName, isCompType, measures);
						allMetrics.add(met);
					}
				}
			
				
				if(useRegularExpr(mbeanType) || useRegularExpr(type) || useRegularExpr(monName)){
					try {						
						
						ObjectName bojNameExpr = JmxUtils.toJmxObjectName(mbeanType, type, monName);
						
						Connector connector = new Connector(config.getHost(), config.getPort(), config.getPath(), config.getUserName(),  config.getPassword());	

						Collection<ObjectName> beanNames = JmxUtils.getAllBeanNamesForPattern(connector, bojNameExpr);
						//Collection<ObjectInstance> beanInstances = JmxUtils.getAllBeanInstanceForPattern(connector, mbeanType+":"+"*");

						//Set<String> prefixSet = new HashSet<>();
						for (ObjectName objectName : beanNames) {
							String name = objectName.getCanonicalName();
							String[] tokens = StringUtils.split(name, ",");
							Map<String, String> attrMap = Maps.newHashMap();
							for (String token : tokens) {
								String[] keyvalue = StringUtils.split(token, "=");
								attrMap.put(keyvalue[0], keyvalue[1]);
							}						
							
							String beanName = null;
							String beanPrefix = null;
							String beanType = null;
							for (String arrtName : attrMap.keySet()) {
								if(StringUtils.endsWith(arrtName.toLowerCase(), ":name")){
									beanName = attrMap.get(arrtName);
									beanPrefix = StringUtils.substringBefore(arrtName, ":");
								}else if(arrtName.equalsIgnoreCase("type")){
									beanType = attrMap.get(arrtName);
								}
							}
							
							if(beanName != null && beanPrefix != null && beanType != null){
								JmxMonitor monitor = new JmxMonitor(beanName, beanType, beanPrefix, allMetrics);							
								allMonitors.add(monitor);
							} else {
								logger.error("Error parsing bean name: " + name);
							}						
						}
						
					} catch (Throwable t) {
						logger.error("Error fetching MBean Names from server [{}], please vrify configuration.  Error: {} ", config.getHost(), ExceptionUtils.getFullStackTrace(t));
					}
				}else{				
					JmxMonitor monitor = new JmxMonitor(monName, type, mbeanType, allMetrics);
				
					allMonitors.add(monitor);
				}
			}
		}
		return allMonitors;
	}	
	
	private static boolean useRegularExpr(String token){
		return StringUtils.containsAny(token, "*", "|", "[", "?", "+");
	}
}
