package com.oaksoft.logging.log4j.message;

import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.message.TimestampMessage;
import org.joda.time.DateTime;


public class LogGenieMessage extends SimpleMessage implements TimestampMessage
{
	private static final long serialVersionUID = -2283306856894571267L;

	private long timestamp;
	private String loggerName;
	private String threadName;
	private String sourceLocation;

	public LogGenieMessage(String message, DateTime dateTime, String loggerName, String threadName, String sourceLocation)
	{
		super(message);

		setTimestamp(dateTime.getMillis());
		setLoggerName(loggerName);
		setThreadName(threadName);
		setSourceLocation(sourceLocation);
	}

	@Override
	public long getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public String getLoggerName()
	{
		return loggerName;
	}

	public void setLoggerName(String loggerName)
	{
		this.loggerName = loggerName;
	}

	public String getThreadName()
	{
		return threadName;
	}

	public void setThreadName(String threadName)
	{
		this.threadName = threadName;
	}

	public String getSourceLocation()
	{
		return sourceLocation;
	}

	public void setSourceLocation(String sourceLocation)
	{
		this.sourceLocation = sourceLocation;
	}
}