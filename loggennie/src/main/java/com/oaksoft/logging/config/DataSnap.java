package com.oaksoft.logging.config;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.google.common.collect.Maps;

public class DataSnap {

	private String name;
	private String type;
	private Map<String, Object> data;
		
	public String toJson() throws JsonGenerationException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);		
		mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);		
		
		return mapper.writeValueAsString(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, Object> getData() {
		return Collections.unmodifiableMap(data);
	}

	public void setData(Map<String, Object> data) {
		this.data = Maps.newHashMap(data);
	}
}
