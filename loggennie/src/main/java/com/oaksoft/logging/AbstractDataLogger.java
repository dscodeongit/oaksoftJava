package com.oaksoft.logging;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.joda.time.DateTime;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.google.common.collect.Maps;
import com.google.common.net.MediaType;
import com.oaksoft.enums.MessageStatusEnum;
import com.oaksoft.logging.config.Constants;
import com.oaksoft.logging.config.DataSnap;
import com.oaksoft.logging.config.Monitor;
import com.oaksoft.logging.config.SourceConfig;
import com.oaksoft.logging.exception.ConnectionException;
import com.oaksoft.util.datetime.DateTimeUtils;

public abstract class AbstractDataLogger implements DataCollector {

	private static String modifiedBy;
	protected volatile boolean needsRestart;
	protected ThreadPoolTaskScheduler service;
	protected SourceConfig serverConfig;
	protected Logger logger = LogManager.getLogger(this.getClass());
    private volatile ScheduledFuture<?> scheduledTask;


	@Override
	public void start() {		
		connect();
		if(getConnectionObject() != null) {
			Long pollInterval = serverConfig.getPollInterval() == 0? serverConfig.getDefaultPollInterval() : serverConfig.getPollInterval();
			scheduledTask = service.scheduleAtFixedRate(() -> poll(), pollInterval);
		}		
	}
	
	protected void connect() {
		logger.info(new ParameterizedMessage("Connecting to {} server: {}", serverConfig.getType().name(), serverConfig.getUrl()));
		
		int numTrial = 0;
		int connectLimit = serverConfig.getReconnectLimit() > 0 ? (serverConfig.getReconnectLimit() +1) : 1;
		Throwable t = null;
		while (numTrial < connectLimit) {
			try
			{	
				doConnect();
				break;
			}
			catch (ConnectionException e)	{
				logger.error(new ParameterizedMessage("Failed to connect to {} Server : {}", serverConfig.getType().name(), serverConfig.getUrl()), e);
				t = e;
			}
			numTrial++ ;

			if(numTrial < connectLimit){
				try {
					Thread.sleep(serverConfig.getReconnectInterval());
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				logger.info(new ParameterizedMessage("Re-trying to connect to {} Server {} [attempt {} ]: ", serverConfig.getType().name(), serverConfig.getUrl(), numTrial));
			}
			
		}
		if(getConnectionObject() == null) {
			logger.error(new ParameterizedMessage("Failed to connect to {} Server : {}. Will not retry. ", serverConfig.getType().name(), serverConfig.getUrl()), t);
		}
	}
	
	protected void poll(){
		try {		
			String batchId = get_id();
			DateTime batchTimeStmp = DateTimeUtils.getJodaDateTimeUTC();

			for (Monitor mon : serverConfig.getMonitors()) {
				Collection<DataSnap> data = mon.snap(getConnectionObject());
				
				for (DataSnap snap : data) {
					StringBuilder builder = new StringBuilder();
					String payload = snap.toJson();
					builder.append(payload + "\n");
					
					Map<String, Object> attributesMap = buildConentMap(serverConfig, payload, batchId, batchTimeStmp);
					
					
					for (Entry<String, Object> entry : attributesMap.entrySet())
					{
						ThreadContext.put(entry.getKey(), String.valueOf(entry.getValue()));
					}
					
					logger.info(new ParameterizedMessage("Data collected for {} server: {} ", serverConfig.getType().name(),  serverConfig.getHost()));
					
					ThreadContext.clearMap();
				}		
			}
		} catch (Throwable t) {
			logger.error(new ParameterizedMessage("Error occured polling data from {} server: {} ",  serverConfig.getType().name() + serverConfig.getUrl()), t);
			onException(t);
			scheduledTask.cancel(false);
			throw new RuntimeException(t);
		}
	}
		
	public static Map<String, Object> buildConentMap(SourceConfig serverConfig,  String payload, String batchId, DateTime batchTimeStmp){
		Map<String, Object> contentMap = buildConentHeaderMap(serverConfig, batchId, batchTimeStmp);
		
		String entryID = UUID.randomUUID().toString();

		contentMap.put("content.entry.sourceURI", serverConfig.getUrl());
		contentMap.put("content.entry.sourceLocation", serverConfig.getAlias());
		contentMap.put("content.entry.correlationId",  entryID);
		contentMap.put("content.entry.id",  entryID);
		contentMap.put("content.entry.contentType", MediaType.JSON_UTF_8.toString());		
		contentMap.put("content.entry.payload", payload);
		
		return contentMap;
	}
	
	//Populate the content header
	public static Map<String, Object> buildConentHeaderMap(SourceConfig serverConfig, String batchId, DateTime batchTimeStmp){
		Map<String, Object> contentMap = Maps.newConcurrentMap();
		
		contentMap.put("source.type", serverConfig.getType().name());

		contentMap.put("content.modifiedBy", getModifiedBy());
		contentMap.put("content.apiVersion", serverConfig.getApiVersion());
		contentMap.put("content.transactionStatus", MessageStatusEnum.STATUS_ACTIVE.getMessageStatusEnum());

		contentMap.put("_id", batchId);

		contentMap.put("content.id", batchId);

		contentMap.put("content.createDate", batchTimeStmp.toString());
		contentMap.put("content.modifiedDate", batchTimeStmp.toString());

		contentMap.put("serverName", serverConfig.getAlias());
		
		return contentMap;
	}
	
	/* 
	 * Add content entry for data collected from server as defined in {@code serverConfig}. this method should be called after the header has been populated by calling buildConentHeaderMap
	 *
	*/
	public static void addBatchEntry(SourceConfig serverConfig, String payload, Map<String, Object> contentMap, int index){
				
		String contentEntryPrefix = "content.entry[" + index + "]";

		String entryID = UUID.randomUUID().toString();

		contentMap.put(contentEntryPrefix + ".sourceURI", serverConfig.getUrl());
		contentMap.put(contentEntryPrefix + ".sourceLocation", serverConfig.getAlias());
		contentMap.put(contentEntryPrefix + ".correlationId",  entryID);
		contentMap.put(contentEntryPrefix + ".id",  entryID);
		contentMap.put(contentEntryPrefix + ".contentType", MediaType.JSON_UTF_8.toString());
		contentMap.put(contentEntryPrefix + ".sequence", String.valueOf(index));
	}
	
	public static String getModifiedBy()
	{
		modifiedBy = Constants.APP_FRIENDLY_NAME;

		return modifiedBy;
	}

	public static void setModifiedBy(String modBy)
	{
		modifiedBy = modBy;
	}

	public static String get_id()
	{
		return UUID.randomUUID().toString();	
	}
	
	@Override
	public boolean needsRestart() {
		return needsRestart;
	}	
	
	protected void cleanCheck(){
		if(getConnectionObject() != null){			
			stop();
		}
	}
	
	@Override
	public void reStart() {
		this.needsRestart = false;
		cleanCheck();
		start();		
	}
	
	protected abstract void doConnect() throws ConnectionException;	
	protected abstract Object getConnectionObject();
	protected abstract void onException(Throwable t);
}
