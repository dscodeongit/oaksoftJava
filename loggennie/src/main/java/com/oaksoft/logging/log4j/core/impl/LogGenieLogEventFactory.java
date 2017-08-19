package com.oaksoft.logging.log4j.core.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.DefaultLogEventFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;

import com.oaksoft.logging.log4j.message.LogGenieMessage;

public class LogGenieLogEventFactory extends DefaultLogEventFactory
{

    @Override
    public LogEvent createEvent(String loggerName, final Marker marker, final String fqcn, final Level level, final Message data, final List<Property> properties, final Throwable t)
    {
    	String threadName = null;
    	
    	if (data instanceof LogGenieMessage)
    	{
    		loggerName = ((LogGenieMessage) data).getLoggerName();
    		threadName = ((LogGenieMessage) data).getThreadName();
    	}
    	
        return new Log4jLogEvent(loggerName, marker, "fqcn", level, data, t, createMap(properties), ThreadContext.getDepth() == 0 ? null : ThreadContext.cloneStack(), threadName, null, new Date().getTime());
        		
    }
	
    private static Map<String, String> createMap(final List<Property> properties) {
        final Map<String, String> contextMap = ThreadContext.getImmutableContext();
        if (contextMap == null && (properties == null || properties.isEmpty())) {
            return null;
        }
        if (properties == null || properties.isEmpty()) {
            return contextMap; // contextMap is not null
        }
        final Map<String, String> map = new HashMap<String, String>(contextMap);

        for (final Property prop : properties) {
            if (!map.containsKey(prop.getName())) {
                map.put(prop.getName(), prop.getValue());
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
