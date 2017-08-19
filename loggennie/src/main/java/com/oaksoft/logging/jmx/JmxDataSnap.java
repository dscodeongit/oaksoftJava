package com.oaksoft.logging.jmx;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import com.google.common.collect.Maps;
import com.oaksoft.logging.config.DataSnap;

public class JmxDataSnap extends DataSnap{	
	private String mbeanType;
	private String attribute;

	public JmxDataSnap(String name, String type, String mbeanType, String attribute, Map<String, Object> data) {
		super();
		setName(name);
		setType(type);
		setData(data);
		this.mbeanType = mbeanType;
		this.attribute = attribute;
	}
	
	public String getAttribute() {
		return attribute;
	}
	public String getMbeanType() {
		return mbeanType;
	}

	public static void main(String[] agrs) {
		Map<String, Object> stats = Maps.newHashMap();
		
		stats.put("committed", 121213321);
		stats.put("init", 2555904);
		stats.put("used", 71363824);

		JmxDataSnap snp = new JmxDataSnap("NonHeapMemoryUsage", null, "java.lang", "USAGE",  stats);
		
		try {
			System.out.println(snp.toJson());

		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
