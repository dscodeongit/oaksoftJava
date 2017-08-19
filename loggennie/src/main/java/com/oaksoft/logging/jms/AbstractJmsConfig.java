package com.oaksoft.logging.jms;

import java.util.Arrays;
import java.util.Collection;

import com.oaksoft.logging.config.SourceConfig;

public abstract class AbstractJmsConfig extends SourceConfig
{
	public static final String JMS_QUEUE_NODE = "Queues";
	public static final String JMS_TOPIC_NODE = "Topics";
	public static final String JMS_DURABLE_NODE = "Durables";
	public static final String JMS_CONNECTION_NODE = "Connections";
	public static final String JMS_MONITOR_NODE = "Monitors";
	public static final String JMS_CONSUMER_NODE = "Consumers";
	public static final String CONFIG_TYPE_XML = "xml";
	public static final String CONFIG_TYPE_JSON = "json";
	public static final String TIBCO_TYPE = "Tibco";
	public static final String ACTIVE_MQ_TYPE = "ActiveMq";

	public static final String QUEUEINFO_KEY = "queueInfo";
	public static final String TOPICINFO_KEY = "topicInfo";
	public static final String DURABLEINFO_KEY = "durableInfo";
	public static final String CONNECTIONINFO_KEY = "connectionInfo";
	public static final String CONSUMERINFO_KEY = "consumerInfo";


	protected static String ancestor;

	public String getAncestor()
	{
		return ancestor;
	}

	public void setAncestor(String ancestor)
	{
		AbstractJmsConfig.ancestor = ancestor;
	}
	
	public static Collection<String> getJMSTypes(){
		return Arrays.asList(TIBCO_TYPE, ACTIVE_MQ_TYPE);
	}
}