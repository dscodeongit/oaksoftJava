package com.oaksoft.logging.jmx;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oaksoft.logging.config.DataSnap;
import com.oaksoft.logging.config.Monitor;

public class JmxMonitor implements Monitor{	
	private static Logger logger = LogManager.getLogger();
	protected static final String[] ARRAY_TEMP = new String[0];
	protected String name;
	protected String type;
	protected String mbeanType;
	
	protected String jmxBeanTypePrefix;
	protected String jmxBeanNamePrefix;

	
	protected Collection<Metric> metrics;
	
	public JmxMonitor(String name, String type, String mbeanType, Collection<Metric> metrics) {
		this.name = name;
		this.type = type;
		this.mbeanType = mbeanType;
		jmxBeanTypePrefix = mbeanType + ":type";
		jmxBeanNamePrefix = mbeanType + ":name";
		this.metrics = Lists.newArrayList(metrics);		
	}		
	
	public JmxMonitor(String name, String type, String mbeanType) {
		this.name = name;
		this.type = type;
		this.mbeanType = mbeanType;
		jmxBeanTypePrefix = mbeanType + ":type";
		jmxBeanNamePrefix = mbeanType + ":name";
		metrics = Collections.emptyList();
	}		
	
	public String getName() {
		return name;
	}


	public String getType() {
		return type;
	}

	public String getMbeanType() {
		return mbeanType;
	}

	public Collection<Metric> getMetrics() {
		return Collections.unmodifiableCollection(metrics);
	}

	private ObjectName toJmxObjectName() throws MalformedObjectNameException{
		if(!StringUtils.isBlank(name) && !StringUtils.isBlank(type)){
			return new ObjectName(jmxBeanTypePrefix + "=" + type + "," + "name=" + name);
		} else if(!StringUtils.isBlank(type)){
			return new ObjectName(jmxBeanTypePrefix + "=" + type);
		} else if (!StringUtils.isBlank(name)){
			return new ObjectName(jmxBeanNamePrefix + "=" + name);		
		}
		return null;
	}
		
	@Override
	public String toString() {
		return "Monitor [name=" + name + ", type=" + type + ", metrics=" + metrics + "]";
	}

	@Override
	public Collection<DataSnap> snap(Object connectionObject) throws Exception{
		MBeanServerConnection mbeanServer = (MBeanServerConnection)connectionObject;
		Map<String, Metric> attrToMatrics = Maps.newHashMap();
		String[] attrs = null;
		if(CollectionUtils.isNotEmpty(metrics)){
			for (Metric metric : metrics) {
				attrToMatrics.put(metric.getName(), metric);
			}
			attrs = attrToMatrics.keySet().toArray(ARRAY_TEMP);
		} else {
			MBeanInfo info = mbeanServer.getMBeanInfo(toJmxObjectName());
			MBeanAttributeInfo[] attributes = info.getAttributes();
			List<String> attrList = Lists.newArrayList();
			for (MBeanAttributeInfo mBeanAttributeInfo : attributes) {
				attrList.add(mBeanAttributeInfo.getName());
			}
			attrs = attrList.toArray(ARRAY_TEMP);			
		}
		
		AttributeList attrList = mbeanServer.getAttributes(toJmxObjectName(), attrs);
		
		if (attrList.size() == attrs.length) {
			logger.debug("All attributes were retrieved successfully for Attributes: " + Arrays.toString(attrs));
		} else {
		     List<String> missings = Lists.newArrayList(Arrays.asList(attrs));
		     for (Attribute a : attrList.asList()){
		         missings.remove(a.getName());
		     }
		     logger.warn("Metrics failed to collect for : " + missings);
		}
		
		return JmxUtils.unMarshalStats(this, attrList);						
	}
		

	public static class Metric{
		private final String name;
		private final boolean isCompositeType;
		private final Collection<String> measures;
		
		public Metric(String name, boolean isCompositeType, Collection<String> measures){
			this.name = name;
			this.isCompositeType = isCompositeType;
			this.measures = Sets.newHashSet(measures);
		}
		
		public Metric(String name){
			this.name = name;
			this.isCompositeType = false;
			this.measures = Collections.emptySet();
		}
		
		public String getName() {
			return name;
		}

		public boolean isCompositeType() {
			return isCompositeType;
		}
		public Collection<String> getMeasures() {
			return Collections.unmodifiableCollection(measures);
		}

		@Override
		public String toString() {
			return "Metric [name=" + name + ", isCompositeType=" + isCompositeType + ", measures=" + measures + "]";
		}		
	}

}
