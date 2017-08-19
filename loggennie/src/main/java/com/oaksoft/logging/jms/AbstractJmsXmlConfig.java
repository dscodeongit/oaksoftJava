package com.oaksoft.logging.jms;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.apache.commons.lang.StringUtils;

import com.oaksoft.commons.configuration.XMLConfigurationUtil;

public abstract class AbstractJmsXmlConfig extends AbstractJmsConfig
{
	protected ConfigurationNode configurationNode;

	abstract Map<String, String> getLoginInfo();

	abstract void setLoginInfo(HashMap<String, String> attributeMap);

	public void setAncestor(ConfigurationNode configurationNode)
	{
		setAncestor(StringUtils.join(XMLConfigurationUtil.getAncestors(configurationNode), "."));
	}

}



























