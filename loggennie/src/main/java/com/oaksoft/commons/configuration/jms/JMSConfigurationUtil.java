package com.oaksoft.commons.configuration.jms;

import static com.oaksoft.logging.jms.AbstractJmsConfig.JMS_CONNECTION_NODE;
import static com.oaksoft.logging.jms.AbstractJmsConfig.JMS_CONSUMER_NODE;
import static com.oaksoft.logging.jms.AbstractJmsConfig.JMS_DURABLE_NODE;
import static com.oaksoft.logging.jms.AbstractJmsConfig.JMS_MONITOR_NODE;
import static com.oaksoft.logging.jms.AbstractJmsConfig.JMS_QUEUE_NODE;
import static com.oaksoft.logging.jms.AbstractJmsConfig.JMS_TOPIC_NODE;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.configuration.tree.ConfigurationNode;

public class JMSConfigurationUtil
{
	private static final HashMap<String, ArrayList<ConfigurationNode>> configurationNodeMap = new HashMap<>();

	public static HashMap<String, ArrayList<ConfigurationNode>> getDestinations(ConfigurationNode configurationNode)
	{
		getJmsQueues(configurationNode);
		getJmsTopics(configurationNode);
		getJmsDurables(configurationNode);
		getJmsConnections(configurationNode);
		getJmsConsumers(configurationNode);

		HashMap<String, ArrayList<ConfigurationNode>> returnConfigurationNodeMap = new HashMap<>(configurationNodeMap);

		configurationNodeMap.clear();

		return returnConfigurationNodeMap;
	}

	public static HashMap<String, ArrayList<ConfigurationNode>> getMonitors(ConfigurationNode configurationNode)
	{
		getJmsMonitors(configurationNode);

		HashMap<String, ArrayList<ConfigurationNode>> returnConfigurationNodeMap = new HashMap<>(configurationNodeMap);

		configurationNodeMap.clear();

		return returnConfigurationNodeMap;
	}

	private static void getJmsMonitors(ConfigurationNode configurationNode)
	{
		getConfigurationNodes(configurationNode, JMS_MONITOR_NODE);
	}

	private static void getJmsDurables(ConfigurationNode configurationNode)
	{
		getConfigurationNodes(configurationNode, JMS_DURABLE_NODE);
	}

	private static void getJmsQueues(ConfigurationNode configurationNode)
	{
		getConfigurationNodes(configurationNode, JMS_QUEUE_NODE);
	}

	private static void getJmsConnections(ConfigurationNode configurationNode)
	{
		getConfigurationNodes(configurationNode, JMS_CONNECTION_NODE);
	}

	private static void getJmsConsumers(ConfigurationNode configurationNode)
	{
		getConfigurationNodes(configurationNode, JMS_CONSUMER_NODE);
	}

	private static void getJmsTopics(ConfigurationNode configurationNode)
	{
		getConfigurationNodes(configurationNode, JMS_TOPIC_NODE);
	}

	private static void getConfigurationNodes(ConfigurationNode parentConfigurationNode, String nodeToMatch)
	{
		ArrayList<ConfigurationNode> configurationNodeArrayList = new ArrayList<>();

		for (ConfigurationNode configurationNode : parentConfigurationNode.getChildren())
		{
			if (!configurationNode.getName().matches(nodeToMatch))
			{
				getConfigurationNodes(configurationNode, nodeToMatch);
			}
			else
			{
				configurationNodeArrayList.add(configurationNode);
			}

			configurationNodeMap.put(nodeToMatch, configurationNodeArrayList);
		}
	}
}

