package com.oaksoft.logging.jms;

import static com.oaksoft.logging.jms.AbstractJmsConfig.CONNECTIONINFO_KEY;
import static com.oaksoft.logging.jms.AbstractJmsConfig.CONSUMERINFO_KEY;
import static com.oaksoft.logging.jms.AbstractJmsConfig.DURABLEINFO_KEY;
import static com.oaksoft.logging.jms.AbstractJmsConfig.QUEUEINFO_KEY;
import static com.oaksoft.logging.jms.AbstractJmsConfig.TIBCO_TYPE;
import static com.oaksoft.logging.jms.AbstractJmsConfig.TOPICINFO_KEY;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.logging.log4j.ThreadContext;
import org.joda.time.DateTime;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.google.common.collect.Lists;
import com.oaksoft.logging.exception.ConnectionException;
import com.oaksoft.util.datetime.DateTimeUtils;
import com.oaksoft.util.properties.PropertiesUtil;
import com.oaksoft.util.string.OakSoftStringUtils;
import com.tibco.tibjms.admin.ConnectionInfo;
import com.tibco.tibjms.admin.ConsumerInfo;
import com.tibco.tibjms.admin.DestinationInfo;
import com.tibco.tibjms.admin.DurableInfo;
import com.tibco.tibjms.admin.QueueInfo;
import com.tibco.tibjms.admin.TibjmsAdmin;
import com.tibco.tibjms.admin.TibjmsAdminException;
import com.tibco.tibjms.admin.TibjmsAdminInvalidNameException;
import com.tibco.tibjms.admin.TopicInfo;

import groovy.json.JsonBuilder;

public class TibcoCollector extends AbstractJmsLogger
{
	private static HashMap<Integer, String> tibcoAckModes = new HashMap<>();
	private static HashMap<Integer, String> tibcoDestinationTypes = new HashMap<>();

	private volatile TibjmsAdmin tibjmsAdmin;
		
	public TibcoCollector(TibcoXmlConfig serverConfig, ThreadPoolTaskScheduler schedulingService)
	{
		tibcoAckModes.putAll(getConstantFieldNames(TibjmsAdmin.class, "^SESSION_(.*)_ACKNOWLEDGE$"));
		tibcoDestinationTypes.putAll(getConstantFieldNames(DestinationInfo.class, "^(.*)_TYPE$"));
		this.serverConfig = serverConfig;
		this.service = schedulingService;
		intializeMaps();
	}
	
	private HashMap<Integer, String> getConstantFieldNames(Class<?> c, String patternString)
	{
		Field[] fieldArray = c.getFields();
		HashMap<Integer, String> ackModes = new HashMap<>();

		Pattern pattern = Pattern.compile(patternString);

		for (int i=0; i < fieldArray.length; i++)
		{
			Matcher matcher = pattern.matcher(fieldArray[i].getName());

			if (matcher.find())
			{
				try
				{
					ackModes.put(fieldArray[i].getInt(null), OakSoftStringUtils.upperUnderscoreToUpperCamelCase(matcher.group(1)));
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					logger.error("Error occured: ", e);
					throw new RuntimeException(e);
				}
			}
		}

		return ackModes;
	}

	private void intializeMaps()
	{
		this.serverStatisticsMap = new TreeMap<>();
		this.queueStatisticsMap = new TreeMap<>();
		this.topicStatisticsMap = new TreeMap<>();
		this.connectionStatisticsMap = new TreeMap<>();
	}

		
	@Override
	public void stop(){		
		if(this.tibjmsAdmin !=null ){
			logger.info("Shutting down data logger for tibco JMS server: " + this.serverConfig.getHost());
			try {
				this.tibjmsAdmin.close();
			} catch (TibjmsAdminException e) {
				logger.error("Failed to close tibco admin connection to :" + this.serverConfig.getHost(), e);
				//throw new RuntimeException("Failed to close tibco admin connection to :" + this.serverConfig.getHost(), e);
			}finally {
				this.tibjmsAdmin = null;
			}
		}
	}
	
	@Override
	protected void poll() {
		final String tibHost = serverConfig.getAlias();
		//Thread.currentThread().setName(tibHost);		
		try {
			getTibcoStats();
			logger.info("Statistics data successfully collected for : " + tibHost);

		} catch (TibjmsAdminException e) {
			onException(e);
			logger.error("Failed to collect statistics data for : " + tibHost + ". Caused by ", e);
			throw new RuntimeException(e);
		} catch (Throwable t) {
			logger.error("Failed to collect statistics data for : " + tibHost + ". Caused by ", t);
			throw t;
		}						
	}


	private Map<String, Method> getMethodMap(Map<String, String> monitorsMap, String keyType, Class<?> baseClass)
	{
		String propertiesFileName = WordUtils.uncapitalize(TIBCO_TYPE) + WordUtils.capitalize(keyType) + "." + monitorsMap.get(keyType);

		Properties properties = null;
		

		HashMap<String, Method> methodMap = new HashMap<>();

		try
		{
			properties = PropertiesUtil.getProperties(propertiesFileName);

		    for (Map.Entry<?, ?> entry: properties.entrySet())
		    {
		        String key = (String) entry.getKey();
		        String value = (String) entry.getValue();

		        if (!key.contains("."))
		        {
		        	Method method = baseClass.getMethod(key);
		        	methodMap.put(value, method);
		        }
		        else
		        {
		        	String methodName = key.split("\\.")[0];

					Method method = baseClass.getMethod(variableToGetter(methodName));
					methodMap.put(key, method);
		        }
		    }

		}
		catch (NoSuchMethodException e)
		{
			logger.error("Error occured: ", e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (SecurityException e)
		{
			logger.error("Error occured: ", e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return methodMap;
	}
	
	private void getTibcoStats() throws TibjmsAdminException
	{
		TibcoXmlConfig config = (TibcoXmlConfig)serverConfig;
		Map<String, String> monitorsMap = config.getMonitorsMap();
		Collection<String> queueList = config.getQueues().isEmpty() ? Lists.newArrayList() : config.getQueues();
		Collection<String> topicList = config.getTopics().isEmpty() ? Lists.newArrayList() : config.getTopics();
		Collection<String> durableList = config.getDurables().isEmpty() ? Lists.newArrayList() : config.getDurables();
		//TibjmsAdmin tibjmsAdmin = doXmlConnect();
		
		if (monitorsMap.containsKey(QUEUEINFO_KEY))
		{
			if (queueList.isEmpty())
			{
				QueueInfo[] allQueueInfos;
			
				allQueueInfos = tibjmsAdmin.getQueues();

				for (int i=0; i < allQueueInfos.length; i++)
				{
					QueueInfo queueInfo = allQueueInfos[i];

					queueList.add(queueInfo.getName());
				}				
			}

			Map<String, Method> methodMap = getMethodMap(monitorsMap, QUEUEINFO_KEY, QueueInfo.class);
			getQueueStatistics(queueList, tibjmsAdmin, methodMap);
		}

		if (monitorsMap.containsKey(TOPICINFO_KEY))
		{
			if (topicList.isEmpty())
			{
				TopicInfo[] allTopicInfos;
				
				allTopicInfos = tibjmsAdmin.getTopics();

				for (int i=0; i < allTopicInfos.length; i++)
				{
					TopicInfo topicInfo = allTopicInfos[i];

					topicList.add(topicInfo.getName());
				}				
			}

			Map<String, Method> methodMap = getMethodMap(monitorsMap, TOPICINFO_KEY, TopicInfo.class);
			getTopicStatistics(topicList, tibjmsAdmin, methodMap);
		}

		if (monitorsMap.containsKey(DURABLEINFO_KEY))
		{
			if (durableList.isEmpty())
			{
				DurableInfo[] allDurableInfos;
				
				allDurableInfos = tibjmsAdmin.getDurables();

				for (int i=0; i < allDurableInfos.length; i++)
				{
					DurableInfo durableInfo = allDurableInfos[i];

					durableList.add(durableInfo.getDurableName());
				}				
			}

			Map<String, Method> methodMap = getMethodMap(monitorsMap, DURABLEINFO_KEY, DurableInfo.class);
			getDurableStatistics(durableList, tibjmsAdmin, methodMap);
		}

		if (monitorsMap.containsKey(CONNECTIONINFO_KEY))
		{
			Map<String, Method> methodMap = getMethodMap(monitorsMap, CONNECTIONINFO_KEY, ConnectionInfo.class);

			getConnectionStatistics(tibjmsAdmin, methodMap);
		}

		if (monitorsMap.containsKey(CONSUMERINFO_KEY))
		{
			Map<String, Method> methodMap = getMethodMap(monitorsMap, CONSUMERINFO_KEY, ConsumerInfo.class);

			getConsumerStatistics(tibjmsAdmin, methodMap);
		}


		JsonBuilder jsonBuilder = new JsonBuilder();
		jsonBuilder.call(serverStatisticsMap);
		
		String batchId = get_id();
		DateTime batchTimeStmp = DateTimeUtils.getJodaDateTimeUTC();
		Map<String, Object> contentMap = buildConentMap(serverConfig, jsonBuilder.toString(), batchId, batchTimeStmp);

		for (Entry<String, Object> entry : contentMap.entrySet())
		{
			ThreadContext.put(entry.getKey(), String.valueOf(entry.getValue().toString()));
		}

		logger.info("Tibco JMS stats collected for " + serverConfig.getHost());

		ThreadContext.clearMap();

		serverStatisticsMap.clear();
		
	}

	private String methodToVariable(String word, String prefix)
	{
		String findString = "^" + prefix;

		return WordUtils.uncapitalize(word.replaceFirst(findString, ""));
	}

	private String variableToGetter(String word)
	{
		return "get".concat(WordUtils.capitalize(word));
	}

	@SuppressWarnings("unused")
	private void getClassInfo(Class<?> className, String parentClassName)
	{
		Method[] methods = className.getMethods();

		TreeMap<String, String> methodMap = new TreeMap<>();

		for (int i=0; i < methods.length; i++)
		{
			String methodName = methods[i].getName();
			String returnType = methods[i].getReturnType().getCanonicalName();

			if (methodName.matches("get.*"))
			{
				String methodVariable = methodToVariable(methodName, "get");

				try
				{
					Class<?> c = ClassUtils.getClass(returnType);
					Package p = c.getPackage();

					if (p != null)
					{
						if (!p.getName().matches("java.lang"))
						{
							getClassInfo(c, methodVariable);
						}
						else
						{
							processMethod(methodName, methodVariable, methodMap, parentClassName);
						}
					}
					else
					{
						if (c.isPrimitive())
						{
							processMethod(methodName, methodVariable, methodMap, parentClassName);
						}

						if (c.isArray())
						{
							Class<?> componentClass = c.getComponentType();

							processMethod(methodName, methodVariable + "[" + componentClass.getCanonicalName() + "]", methodMap, parentClassName);
						}
					}
				}
				catch (ClassNotFoundException e)
				{
					System.err.println("MethodName: " + methodName + " ReturnType: " + returnType);
					logger.error("Error occured MethodName: " + methodName + " ReturnType: " + returnType + ". Caused by ", e);
					throw new RuntimeException(e);

				}
			}
			if (methodName.matches("is.*"))
			{
				methodMap.put(methodName, methodToVariable(methodName, "is"));
			}
		}

		for (Entry<String, String> entry: methodMap.entrySet())
		{
			System.err.println(entry.getKey() + " " + entry.getValue());
		}
	}

	private void processMethod(String methodName, String methodVariable, TreeMap<String, String> methodMap, String parentClassName)
	{
		String key = methodName;
		String value = methodVariable;

		if (parentClassName != null)
		{
			key = parentClassName.concat(".").concat(methodName);
			value = parentClassName.concat(WordUtils.capitalize(value));
		}
				
		methodMap.put(key, value);
	}

	private void getStatisticsDetailList(Map<String, Method> methodMap, Object itemInfo, TreeMap<String, Object> statisticsDetailMap)
	{
		try
		{
			for (Entry<String, Method> entry: methodMap.entrySet())
			{
				Method method = entry.getValue();
				String methodKey = entry.getKey();
				

				Object statisticsInfoDetail = method.invoke(itemInfo);
				
				if(methodKey.toLowerCase().contains("host") || methodKey.toLowerCase().contains("uptime") ){
					logger.debug("==>> "+ methodKey + ": " + statisticsInfoDetail);

				}
				if (!methodKey.contains("."))
				{
					statisticsDetailMap.put(entry.getKey(), statisticsInfoDetail);
				}
				else
				{
					if (!methodKey.contains("["))
					{
						String[] methodInfo = entry.getKey().split("\\.");

						String getterName = variableToGetter(methodInfo[0]);
						String childGetterName = methodInfo[1];

						Method childMethod = statisticsInfoDetail.getClass().getMethod(childGetterName);
							
						Object childItemInfoDetail = childMethod.invoke(statisticsInfoDetail);

						if(childGetterName.contains("getTotalMessages")){
							logger.debug("==>> Total Messages: " + childItemInfoDetail);
						}						
						
						statisticsDetailMap.put(methodToVariable(getterName, "get") + WordUtils.capitalize(methodToVariable(childGetterName, "get")), childItemInfoDetail);
					}
					else
					{
						String[] methodInfo = entry.getKey().split("\\[");

						String getterKey = methodInfo[0];

						statisticsDetailMap.put(getterKey, statisticsInfoDetail);						
					}					
				}
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e)
		{
			throw new RuntimeException(e);
		}		
	}

	private void getQueueStatistics(Collection<String> queueArrayList, TibjmsAdmin tibjmsAdmin, Map<String, Method> methodMap) throws TibjmsAdminException
	{
		ArrayList<TreeMap<String, Object>> queueStatisticsDetailList = new ArrayList<>();

		serverStatisticsMap.put("queueStatistics", queueStatisticsDetailList);

		for (String queue: queueArrayList)
		{
			QueueInfo queueInfo;

			TreeMap<String, Object> queueStatisticsDetailMap = new TreeMap<>();

			try
			{
				queueInfo = tibjmsAdmin.getQueue(queue);
				
				if(queueInfo != null) {

					queueStatisticsDetailMap.put("queueName", queueInfo.getName());
	
					queueStatisticsDetailList.add(queueStatisticsDetailMap);
	
					getStatisticsDetailList(methodMap, queueInfo, queueStatisticsDetailMap);
				} else {
					logger.error(String.format("No Queue with name [%s] found on server [%s]", queue, tibjmsAdmin.getInfo().getServerName()));
				}
			}
			catch (TibjmsAdminInvalidNameException e)
			{
				throw new RuntimeException(e);
			}			
		}
	}

	private void getConsumerStatistics(TibjmsAdmin tibjmsAdmin, Map<String, Method> methodMap) throws TibjmsAdminException
	{
		ArrayList<TreeMap<String, Object>> consumerStatisticsDetailList = new ArrayList<>();

		ConsumerInfo[] allConsumerInfos = null;
		
		allConsumerInfos = tibjmsAdmin.getConsumers(null, null, null, true, TibjmsAdmin.GET_STAT + TibjmsAdmin.GET_SELECTOR);		

		serverStatisticsMap.put("consumerStatistics", consumerStatisticsDetailList);

		for (ConsumerInfo consumerInfo : allConsumerInfos)
		{
			TreeMap<String, Object> consumerStatisticsDetailMap = new TreeMap<>();

			consumerStatisticsDetailList.add(consumerStatisticsDetailMap);

			getStatisticsDetailList(methodMap, consumerInfo, consumerStatisticsDetailMap);

			if (consumerStatisticsDetailMap.containsKey("detailsSessionAcknowledgeMode"))
			{
				consumerStatisticsDetailMap.put("detailsSessionAcknowledgeMode", tibcoAckModes.get(consumerStatisticsDetailMap.remove("detailsSessionAcknowledgeMode")));
			}

			if (consumerStatisticsDetailMap.containsKey("destinationType"))
			{
				consumerStatisticsDetailMap.put("destinationType", tibcoDestinationTypes.get(consumerStatisticsDetailMap.remove("destinationType")));
			}

			handleDateTime(consumerStatisticsDetailMap, "createTime");

//			if (consumerStatisticsDetailMap.containsKey("createTime"))
//			{
//				Map<String, Long> dateMap = getBSONDateMap(Long.valueOf(consumerStatisticsDetailMap.get("createTime").toString()));
//
//				consumerStatisticsDetailMap.put("createTime", dateMap);
//			}
		}
	}

	private void handleDateTime(TreeMap<String, Object> treeMap, String mapKey)
	{
		if (treeMap.containsKey(mapKey))
		{
			Map<String, Long> dateMap = getBSONDateMap(Long.valueOf(treeMap.get(mapKey).toString()));

			treeMap.put(mapKey, dateMap);
		}
	}

	private void getConnectionStatistics(TibjmsAdmin tibjmsAdmin, Map<String, Method> methodMap) throws TibjmsAdminException
	{
		ArrayList<TreeMap<String, Object>> connectionStatisticsDetailList = new ArrayList<>();

		ConnectionInfo[] allConnectionInfos = null;		
		allConnectionInfos = tibjmsAdmin.getConnections();

		serverStatisticsMap.put("connectionStatistics", connectionStatisticsDetailList);

		for (ConnectionInfo connectionInfo : allConnectionInfos)
		{
			TreeMap<String, Object> connectionStatisticsDetailMap = new TreeMap<>();

			connectionStatisticsDetailList.add(connectionStatisticsDetailMap);

			getStatisticsDetailList(methodMap, connectionInfo, connectionStatisticsDetailMap);

			String clientVersion = connectionStatisticsDetailMap.remove("versionInfoVersionMajor") + "." + connectionStatisticsDetailMap.remove("versionInfoVersionMinor") + "." + connectionStatisticsDetailMap.remove("versionInfoVersionUpdate") + "." + connectionStatisticsDetailMap.remove("versionInfoVersionBuild");

			connectionStatisticsDetailMap.put("clientVersion", clientVersion);

			handleDateTime(connectionStatisticsDetailMap, "startTime");
		}	
		
	}

	private void getDurableStatistics(Collection<String> durableArrayList, TibjmsAdmin tibjmsAdmin, Map<String, Method> methodMap) throws TibjmsAdminException
	{
		ArrayList<TreeMap<String, Object>> durableStatisticsDetailList = new ArrayList<>();

		serverStatisticsMap.put("durableStatistics", durableStatisticsDetailList);

		for (String durable: durableArrayList)
		{
			DurableInfo durableInfo;

			TreeMap<String, Object> durableStatisticsDetailMap = new TreeMap<>();

			try
			{
				durableInfo = tibjmsAdmin.getDurable(durable, null);
				
				if(durableInfo !=null ) {
					durableStatisticsDetailMap.put("durableName", durableInfo.getDurableName());

					durableStatisticsDetailList.add(durableStatisticsDetailMap);

					getStatisticsDetailList(methodMap, durableInfo, durableStatisticsDetailMap);
				} else {
					logger.error(String.format("No Durable with name [%s] found on server [%s]", durable, tibjmsAdmin.getInfo().getServerName()));
				}
			}
			catch (TibjmsAdminInvalidNameException e)
			{
				throw new RuntimeException(e);
			}			
		}
	}

	private void getTopicStatistics(Collection<String> topicArrayList, TibjmsAdmin tibjmsAdmin, Map<String, Method> methodMap) throws TibjmsAdminException
	{
		ArrayList<TreeMap<String, Object>> topicStatisticsDetailList = new ArrayList<>();

		serverStatisticsMap.put("topicStatistics", topicStatisticsDetailList);

		for (String topic: topicArrayList)
		{
			TopicInfo topicInfo;

			TreeMap<String, Object> topicStatisticsDetailMap = new TreeMap<>();

			try
			{
				topicInfo = tibjmsAdmin.getTopic(topic);
				
				if(topicInfo != null) {

					topicStatisticsDetailMap.put("topicName", topicInfo.getName());
	
					topicStatisticsDetailList.add(topicStatisticsDetailMap);
	
					getStatisticsDetailList(methodMap, topicInfo, topicStatisticsDetailMap);
				} else {
					logger.error(String.format("No Topic with name [%s] found on server [%s]", topic, tibjmsAdmin.getInfo().getServerName()));
				}
			}
			catch (TibjmsAdminInvalidNameException e)
			{
				throw new RuntimeException(e);
			}			
		}
	}


	private Map<String, Long> getBSONDateMap(Long date)
	{
		HashMap<String, Long> bsonDateMap = new HashMap<String, Long>();

		bsonDateMap.put("$date", date);

		return bsonDateMap;
	}

	@Override
	protected void doConnect() throws ConnectionException {			
		try {
			tibjmsAdmin = new TibjmsAdmin(serverConfig.getUrl(), serverConfig.getUserName(), serverConfig.getPassword());
		} catch (TibjmsAdminException e) {
			throw new ConnectionException(e);
		}
	}
	
	@Override
	protected Object getConnectionObject() {
		return this.tibjmsAdmin;
	}

	@Override
	public String toString() {
		return "TibcoLogger [serverURL = " + serverConfig.getHost() + "]";
	}

	@Override
	protected void onException(Throwable t) {
		if(t instanceof TibjmsAdminException && serverConfig.getReconnectLimit() > 0){
			this.needsRestart = true;
		}	
		
	}
}