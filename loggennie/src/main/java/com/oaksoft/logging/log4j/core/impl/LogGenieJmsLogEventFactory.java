package com.oaksoft.logging.log4j.core.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;

public class LogGenieJmsLogEventFactory extends LogGenieLogEventFactory
{
	private static final String JMS_SERVER_NAME = "jmsServer";
	private static final String JMS_SERVER_POLL_RATE = "jmsServerPollRate";

	public LogGenieJmsLogEventFactory()
	{
		super();

		startJmsListener(getConfiguration());
	}

	private static StrLookup getStrLookup(Configuration configuration)
	{
		StrSubstitutor strSubstitutor = configuration.getStrSubstitutor();

		return strSubstitutor.getVariableResolver();
	}

	private static String getJmsServerName(Configuration configuration)
	{
		return getStrLookup(configuration).lookup(JMS_SERVER_NAME);
	}

	private static Integer getJmsServerPollRate(Configuration configuration)
	{
		return Integer.parseInt(getStrLookup(configuration).lookup(JMS_SERVER_POLL_RATE));
	}

	private static void startJmsListener(final Configuration configuration)
	{
		class JmsServerPoller implements Runnable
		{
			@Override
			public void run()
			{
				System.out.println(JMS_SERVER_POLL_RATE + " milliseconds passed (" + getJmsServerName(configuration) + ")");
			}
		}

		Thread jmsServerPoller = new Thread(new JmsServerPoller());
		jmsServerPoller.setDaemon(true);

		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

		service.scheduleAtFixedRate(jmsServerPoller, 0, getJmsServerPollRate(configuration), TimeUnit.MILLISECONDS);
	}

	private static Configuration getConfiguration()
	{
		LoggerContext loggerContext = (LoggerContext) LogManager.getContext();

		Configuration configuration = loggerContext.getConfiguration();

		return configuration;
	}
}