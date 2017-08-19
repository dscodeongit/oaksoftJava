package com.oaksoft.logging.jms;

import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.oaksoft.enums.SourceType;
import com.oaksoft.logging.AbstractDataLogger;
import com.oaksoft.logging.config.Constants;

public abstract class AbstractJmsLogger extends AbstractDataLogger
{
	public String configType;

	protected TreeMap<String, Object> serverStatisticsMap;
	protected TreeMap<String, Object> queueStatisticsMap;
	protected TreeMap<String, Object> topicStatisticsMap;
	protected TreeMap<String, Object> connectionStatisticsMap;
	
	public void setConfigType(String configType)
	{
		this.configType = configType;
	}


}

