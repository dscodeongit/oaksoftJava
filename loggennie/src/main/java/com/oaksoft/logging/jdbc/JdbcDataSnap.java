package com.oaksoft.logging.jdbc;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.common.collect.Maps;
import com.oaksoft.logging.config.DataSnap;

public class JdbcDataSnap extends DataSnap{	

	public JdbcDataSnap(String name, String type, Map<String, Object> data) {
		super();
		setName(name);
		setType(type);
		setData(data);
	}
	
	
	private String toJson1() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);		
		
		Map<String, Object> statsMap = Maps.newLinkedHashMap();
		if(getName() != null) {
			statsMap.put("name",getName());
		}
		if(getType() != null){
			statsMap.put("type", getType());
		}		
		return mapper.writeValueAsString(statsMap);
	}

	public static void main(String[] agrs) {
		Map<String, Object> stats = Maps.newHashMap();
		
		stats.put("committed", 121213321);
		stats.put("init", 2555904);
		stats.put("used", 71363824);

	/*	JdbcDataSnap snp = new JdbcDataSnap("NonHeapMemoryUsage", null, "java.lang", "USAGE",  stats);
		
		try {
			System.out.println(snp.toJson());
			System.out.println(snp.toJson1());

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
		*/
		
	}
}
