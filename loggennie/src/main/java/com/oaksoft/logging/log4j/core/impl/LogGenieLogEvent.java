package com.oaksoft.logging.log4j.core.impl;

import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext.ContextStack;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;

public class LogGenieLogEvent extends Log4jLogEvent
{
	private static final long serialVersionUID = 5514941058546560338L;

	private Map<String, Object> content;

	public LogGenieLogEvent(String loggerName, Marker marker, String loggerFQCN, Level level, Message message, Throwable t, Map<String, String> mdc, ContextStack ndc, String threadName, StackTraceElement location, long timestamp)
	{
		super(loggerName, marker, loggerFQCN, level, message, t, mdc, ndc, threadName, location, timestamp);
	}

	public Map<String, Object> getContent()
	{
		return content;
	}

	public void setContent(Map<String, Object> content)
	{
		this.content = content;
	}
}
