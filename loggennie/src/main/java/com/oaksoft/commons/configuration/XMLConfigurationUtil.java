package com.oaksoft.commons.configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class XMLConfigurationUtil
{
	private static final ArrayList<ConfigurationNode> configurationNodesArrayList = new ArrayList<>();
	private static final ArrayList<String> ancestorsList = new ArrayList<>();

	public static XMLConfiguration getXMLConfiguration(String configFileName) throws FileNotFoundException
	{
    	XMLConfiguration xmlConfiguration = null;
    	Path path = null;

    	try
    	{
			path = Paths.get(ClassLoader.getSystemResource(configFileName).toURI());

			xmlConfiguration = getXMLConfiguration(path.toFile());

			if (xmlConfiguration == null)
			{
				throw new FileNotFoundException(path.toAbsolutePath().toString());
			}

		}
    	catch (URISyntaxException e)
    	{
			e.printStackTrace();
		}

    	return xmlConfiguration;
	}

	public static XMLConfiguration getXMLConfiguration(File configFile)
	{
		XMLConfiguration xmlConfiguration = new XMLConfiguration();
		try
		{			
			xmlConfiguration.setDelimiterParsingDisabled(true);
			xmlConfiguration.load(configFile);
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
			return null;
		}

		return xmlConfiguration;
	}

	public static HierarchicalConfiguration getSubConfiguration(XMLConfiguration xmlConfiguration, String configurationPath)
	{
		
		HierarchicalConfiguration subConfig = xmlConfiguration.configurationAt(configurationPath);

		return subConfig;
	}
	
	public static List<ConfigurationNode> getDescendentsforName(HierarchicalConfiguration hierarchicalConfiguration, String nodeToMatch)
	{
		List<ConfigurationNode> nodes = Lists.newArrayList();
		for (ConfigurationNode configurationNode : hierarchicalConfiguration.getRoot().getChildren())
		{
			nodes.addAll(getNodesforName(configurationNode, nodeToMatch));		
		}
		return nodes;
	}
	
	private static List<ConfigurationNode> getNodesforName(ConfigurationNode parent, String nodeToMatch) {
		List<ConfigurationNode> nodes = Lists.newArrayList();
		if(parent.getName().matches(nodeToMatch)){
			nodes.add(parent);
		} else if (parent.getChildrenCount() != 0) {
			for (ConfigurationNode child : parent.getChildren()) {
				nodes.addAll(getNodesforName(child, nodeToMatch));
			}
		}
		
		return nodes;
	}
		
	public static boolean isConfigPathExist(XMLConfiguration xmlConfiguration, String configurationPath) {
		
		for(Iterator<String> keysIt = xmlConfiguration.getKeys(); keysIt.hasNext(); ) {
			String curPath = keysIt.next();
			if(StringUtils.startsWith(curPath, configurationPath)){
				return true;
			}
		}
		
		return false;
	}

	public static ArrayList<ConfigurationNode> parseHierarchicalConfiguration(HierarchicalConfiguration hierarchicalConfiguration, String nodeToMatch)
	{
		for (ConfigurationNode configurationNode : hierarchicalConfiguration.getRoot().getChildren())
		{
			if (!configurationNode.getName().matches(nodeToMatch))
			{
				getConfigurationNodes(configurationNode, nodeToMatch);
			}
			else
			{
				getMatchedNode(configurationNode);
			}
		}

		return configurationNodesArrayList;
	}

	public static void getConfigurationNodes(ConfigurationNode parentConfigurationNode, String nodeToMatch)
	{
		for (ConfigurationNode configurationNode : parentConfigurationNode.getChildren())
		{
			if (!configurationNode.getName().matches(nodeToMatch))
			{
				getConfigurationNodes(configurationNode, nodeToMatch);
			}
			else
			{
				getMatchedNode(configurationNode);
			}
		}
	}

	private static void getMatchedNode(ConfigurationNode configurationNode)
	{
//		System.err.println("\n" + configurationNode.getName());

		configurationNodesArrayList.add(configurationNode);

//		List<ConfigurationNode> attributes = configurationNode.getAttributes();
//
//		for (ConfigurationNode attribute: attributes)
//		{
//			System.err.println(attribute.getName() + ": " + attribute.getValue());
//		}
	}

	public static ArrayList<String> getAncestors(ConfigurationNode configurationNode)
	{
		if (configurationNode != null)
		{
			ConfigurationNode parentConfigurationNode = configurationNode.getParentNode();

			ancestorsList.add(parentConfigurationNode.getName());

			getAncestorPath(parentConfigurationNode);
		}

		Collections.reverse(ancestorsList);

		ArrayList<String> returnAncestorsList = new ArrayList<>(ancestorsList);

		ancestorsList.clear();

		return returnAncestorsList;
	}

	private static void getAncestorPath(ConfigurationNode configurationNode)
	{
		if (configurationNode != null)
		{
			ConfigurationNode parentConfigurationNode = configurationNode.getParentNode();

			if (parentConfigurationNode != null)
			{
				ancestorsList.add(parentConfigurationNode.getName());

				getAncestorPath(parentConfigurationNode);
			}
		}
	}
}
