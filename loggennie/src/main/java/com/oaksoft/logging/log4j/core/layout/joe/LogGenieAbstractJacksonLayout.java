/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package com.oaksoft.logging.log4j.core.layout.joe;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.util.Strings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.oaksoft.logging.log4j.core.impl.LogGenieLogEvent;

abstract class LogGenieAbstractJacksonLayout extends AbstractStringLayout {

    private static final long serialVersionUID = 1L;
    protected static final String DEFAULT_EOL = "\r\n";
    protected static final String COMPACT_EOL = Strings.EMPTY;
    protected final String eol;
    protected final ObjectWriter objectWriter;
    protected final boolean compact;
    protected final boolean complete;

    protected LogGenieAbstractJacksonLayout(final ObjectWriter objectWriter, final Charset charset, final boolean compact, final boolean complete) {
        super(charset);
        this.objectWriter = objectWriter;
        this.compact = compact;
        this.complete = complete;
        this.eol = compact ? COMPACT_EOL : DEFAULT_EOL;
    }

    /**
     * Formats a {@link org.apache.logging.log4j.core.LogEvent}.
     *
     * @param event The LogEvent.
     * @return The XML representation of the LogEvent.
     */

    @Override
    public String toSerializable(final LogEvent event)
    {
    	//	Create custom LogEvent - remove MDC from new event as we are using the data in fields in LogGenieLogEvent
    	LogGenieLogEvent logGenieLogEvent = new LogGenieLogEvent(event.getLoggerName(), event.getMarker(), event.getLoggerFqcn(), event.getLevel(), event.getMessage(), event.getThrown(), null, event.getContextStack(), event.getThreadName(), event.isIncludeLocation() ? event.getSource() : null, event.getTimeMillis());

    	//	Create a map to be set in LogGenieLogEvent.content
    	Map<String, Object> contentMap = new HashMap<String, Object>();

    	//	Interate through the original MDC, processing the entries as appropriate
		for (Entry<String, String> entry : event.getContextMap().entrySet())
		{
			switch (entry.getKey())
			{
				case "apiVersion":
				case "transactionStatus":
				case "createDate":
				case "count":
				case "id":
				case "modifiedBy":
				case "modifiedDate":

				contentMap.put(entry.getKey(), entry.getValue());
			}
		}

		//	Set the content in the LogGenieLogEvent
    	logGenieLogEvent.setContent(contentMap);

    	//	set the endOfBatch for LogGenieLogEvent here since it can't be done in the constructor
    	logGenieLogEvent.setEndOfBatch(event.isEndOfBatch());

        try
        {
        	//	Will return any serializable, public objects in the LogEvent
            return this.objectWriter.writeValueAsString(logGenieLogEvent);
        }
        catch (final JsonProcessingException e)
        {
            // Should this be an ISE or IAE?
            LOGGER.error(e);
            return Strings.EMPTY;
        }
    }

}
