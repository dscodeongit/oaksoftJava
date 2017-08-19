package com.oaksoft.logging.jmx;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.TabularDataSupport;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oaksoft.logging.config.DataSnap;
import com.oaksoft.logging.jmx.JmxMonitor.Metric;

public class JmxUtils {
	
	public static final String ESCAP_QUOTE = "&quot;";

	
	public static Collection<DataSnap> unMarshalStats(JmxMonitor monitor, AttributeList attrList) {
		Collection<DataSnap> stats = Lists.newArrayList();
		
		Map<String, Metric> attrToMatrics = Maps.newHashMap();
		for (Metric metric : monitor.getMetrics()) {
			attrToMatrics.put(metric.getName(), metric);
		}
		
		for (Attribute attr : attrList.asList()) {
			String attrName = attr.getName();
			Object attrValue = attr.getValue();
			Metric metric = attrToMatrics.get(attrName);
			Map<String, Object> statsMap = Maps.newHashMap();
			
			//Ignore Meta data
			if(attrName.equalsIgnoreCase("Type") || attrName.equalsIgnoreCase("Name") || attrName.equalsIgnoreCase("ObjectName")) {
				continue;
			}
			
			if(attrValue instanceof CompositeDataSupport){
				CompositeDataSupport compositeData = (CompositeDataSupport)attrValue;
				
				if(metric != null && CollectionUtils.isNotEmpty(metric.getMeasures())){					
					statsMap = mapCompositeData(compositeData, Sets.newLinkedHashSet(metric.getMeasures()));
				} else {
					statsMap = mapCompositeData(compositeData);
				}
			} else if(attrValue instanceof String) {
				statsMap.put(attrName, StringEscapeUtils.escapeJson((String) attrValue));
			}else if(attrValue instanceof TabularDataSupport) {  
				mapTabularData((TabularDataSupport)attrValue, statsMap);

			}else if(attrValue instanceof ObjectName) {
				//Ignore this type for now
				//ObjectName objn = (ObjectName) attrValue;
				//System.out.println("Object Name: " + objn.getCanonicalName());
				//statsMap.put(attrName, StringEscapeUtils.escapeJson(objn.getCanonicalName()));

			}else {
				statsMap.put(attrName, attrValue);
			}
			
			JmxDataSnap snap = new JmxDataSnap(monitor.getName(), monitor.getType(), monitor.getMbeanType(), attrName, statsMap);
			stats.add(snap);
		}
		return stats;
	}

	public static Map<String, Object> mapCompositeData(CompositeData cData) {
		Set<String> keys = cData.getCompositeType().keySet();
		return mapCompositeData(cData, keys);
	}

	public static Map<String, Object> mapCompositeData(CompositeData cData, Set<String> includeKeys) {
		Map<String, Object> rsMap = Maps.newLinkedHashMap();
		for (String key : includeKeys) {
			Object value = cData.get(key);
			if (value instanceof CompositeDataSupport) {
				rsMap.put(key, mapCompositeData((CompositeDataSupport) value));
			} else if(value instanceof TabularDataSupport){ 				
				TabularDataSupport data = (TabularDataSupport)value;
				mapTabularData(data, rsMap);				
			}else{
				rsMap.put(key, value);
			}
		}
		return rsMap;
	}
	
	public static void mapTabularData(TabularDataSupport tabularData, Map<String, Object> rsMap){
		 Set<Object> keys = tabularData.keySet();
         for ( Iterator<Object> it = keys.iterator(); it.hasNext(); ) {
             Object dkey = it.next();
             for ( Iterator<Object> ki = ((List<Object>) dkey).iterator(); ki.hasNext(); ) {
                 Object key2 = ki.next();
                 CompositeData cd = tabularData.get(new Object[] {key2});
                 rsMap.put(key2.toString(), mapCompositeData(cd));
             }
         }
	}

	public static Set<ObjectName> getAllBeanNamesForPattern(Connector connector, ObjectName objectNamePattern) throws IOException {
		connector.connect();
		MBeanServerConnection connection = connector.getMbeanServer();		
		return connection.queryNames(objectNamePattern, null);
	}
	
	public static Set<ObjectInstance> getAllBeanInstanceForPattern(Connector connector, String mbeanNamePattern) throws IOException {
		connector.connect();
		MBeanServerConnection connection = connector.getMbeanServer();
		ObjectName queryObjectName = objectName(mbeanNamePattern);
		
		return connection.queryMBeans(queryObjectName, null);
	}
	
	public static ObjectName objectName(CharSequence on) { 
		try {
			return new ObjectName(on.toString().trim());
		} catch (Exception e) {
			throw new RuntimeException("Failed to create Object Name", e);
		}
	}
	
	public static ObjectName toJmxObjectName(String jmxBeanType, String type, String name) throws MalformedObjectNameException{
		//testGroovy();
		if(!StringUtils.isBlank(name) && !StringUtils.isBlank(type)){
			return new ObjectName(jmxBeanType + ":type" + "=" + type + "," + "name=" + name);
		} else if(!StringUtils.isBlank(type)){
			return new ObjectName(jmxBeanType + ":type" + "=" + type);
		} else if (!StringUtils.isBlank(name)){
			return new ObjectName(jmxBeanType + ":name" + "=" + name);		
		}
		return null;
	}
	/*
	private static  void testGroovy(){
		GroovyUtils.hello_world();
	}
	*/
}
