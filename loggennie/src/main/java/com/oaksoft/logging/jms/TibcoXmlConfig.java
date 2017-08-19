package com.oaksoft.logging.jms;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oaksoft.commons.configuration.XMLConfigurationUtil;
import com.oaksoft.commons.configuration.jms.JMSConfigurationUtil;
import com.oaksoft.enums.SourceType;

public class TibcoXmlConfig extends AbstractJmsXmlConfig
{
	private Collection<String> queues;
	private Collection<String> topics;
	private Collection<String> durables;

	private Map<String, String> monitorsMap;
	
	@Override
	protected void parseServerSpecificDetails(ConfigurationNode attrNode){
		String value = String.valueOf(attrNode.getValue());
		if(attrNode.getName().equals("url")){
			setUrl(value);
			setHost(StringUtils.substringBetween(value, "//", ":"));
			setPort(Integer.valueOf(StringUtils.substringAfterLast(value, ":")));
		}
	}
	
	public static Collection<TibcoXmlConfig> parseConfig(String configFile) throws FileNotFoundException{
		XMLConfiguration xmlConfiguration = XMLConfigurationUtil.getXMLConfiguration(configFile);

		HierarchicalConfiguration hierarchicalConfiguration = XMLConfigurationUtil.getSubConfiguration(xmlConfiguration, SourceType.TIBCO.name());
		List<TibcoXmlConfig> configs = Lists.newArrayList();
		
		if (!hierarchicalConfiguration.isEmpty())
		{
			List<ConfigurationNode> connectionNodes = XMLConfigurationUtil.getDescendentsforName(hierarchicalConfiguration, "ConnectionNode");

			for (ConfigurationNode configurationNode: connectionNodes)
			{    
				TibcoXmlConfig config = new TibcoXmlConfig();
				
				config.parseConnectionDetails(configurationNode);				
				
				config.setMonitors(getXmlMonitors(configurationNode));
				
				config.setQueues(getXmlDestinations(configurationNode, JMS_QUEUE_NODE));
				config.setTopics(getXmlDestinations(configurationNode, JMS_TOPIC_NODE));
				config.setDurables(getXmlDestinations(configurationNode, JMS_DURABLE_NODE));
				
				configs.add(config);
			}
		}			
		
		return configs;		
	}

	private static Collection<String> getXmlDestinations(ConfigurationNode configurationNode, String nodeType)
	{
		Collection<String> destinations = Lists.newArrayList();
		HashMap<String, ArrayList<ConfigurationNode>> configurationNodeMap = JMSConfigurationUtil.getDestinations(configurationNode);

		if (!configurationNodeMap.get(nodeType).isEmpty())
		{
			if (configurationNodeMap.get(nodeType).get(0).getChildrenCount() > 0)
			{

				if (configurationNodeMap.get(nodeType) != null)
				{
					ArrayList<ConfigurationNode> destinationList = configurationNodeMap.get(nodeType);

					for (ConfigurationNode destinationNode: destinationList)
					{
						for (ConfigurationNode destList : destinationNode.getChildren())
						{
							for (ConfigurationNode destination : destList.getAttributes())
							{
								destinations.add(destination.getValue().toString());
							}
						}
					}
				}
			}
		}
		
		return destinations;
	}

	
	private static Map<String, String> getXmlMonitors(ConfigurationNode configurationNode)
	{
		Map<String, String> monitorsMap = Maps.newHashMap();
		HashMap<String, ArrayList<ConfigurationNode>> configurationNodeMap = JMSConfigurationUtil.getMonitors(configurationNode);

		if (configurationNodeMap.get(JMS_MONITOR_NODE) != null)
		{
			ArrayList<ConfigurationNode> monitorList = configurationNodeMap.get(JMS_MONITOR_NODE);

			for (ConfigurationNode monitorNode: monitorList)
			{
				for (ConfigurationNode monitors : monitorNode.getChildren())
				{
					HashMap<String, String> monitorMap = new HashMap<>();

					for (ConfigurationNode monitor : monitors.getAttributes())
					{
						monitorMap.put(monitor.getName(), monitor.getValue().toString());
					}

					monitorsMap.put(monitorMap.get("name"), monitorMap.get("type"));
				}
			}
		}
		return monitorsMap;
	}

	
	public TibcoXmlConfig(ConfigurationNode configurationNode)
	{
		this.configurationNode = configurationNode;
		setAncestor(configurationNode);
		//getLoginInfo();
		this.type = SourceType.TIBCO;
	}
	
	public TibcoXmlConfig(){
		type =  SourceType.TIBCO;
	}

	@Override
	public Map<String, String> getLoginInfo()
	{
		HashMap<String, String> attributeMap = new HashMap<>();

		setLoginInfo(attributeMap);

		return attributeMap;
	}

	@Override
	void setLoginInfo(HashMap<String, String> attributeMap)
	{
		List<ConfigurationNode> attributes = configurationNode.getAttributes();

		for (ConfigurationNode attribute: attributes)
		{
			String attributeName = attribute.getName();
			String attributeValue = attribute.getValue().toString();

			attributeMap.put(attributeName, attributeValue);

			switch (attributeName)
			{
				case "url":
					setUrl(attributeValue);
					break;

				case "user":
					setUserName(attributeValue);
					break;

				case "password":
					setPassword(attributeValue);
					break;
			}
		}
	}	

	public Collection<String> getQueues() {
		return Collections.unmodifiableCollection(queues);
	}

	public void setQueues(Collection<String> queues) {
		this.queues =  Lists.newArrayList(queues);
	}

	public Collection<String> getTopics() {
		return Collections.unmodifiableCollection(topics);
	}

	public void setTopics(Collection<String> topics) {
		this.topics =  Lists.newArrayList(topics);
	}

	public Collection<String> getDurables() {
		return Collections.unmodifiableCollection(durables);
	}

	public void setDurables(Collection<String> durables) {
		this.durables = Lists.newArrayList(durables);
	}

	public Map<String, String> getMonitorsMap() {
		return Collections.unmodifiableMap(monitorsMap);
	}

	public void setMonitors(Map<String, String> monitors) {
		this.monitorsMap = Maps.newHashMap(monitors);
	}
}
