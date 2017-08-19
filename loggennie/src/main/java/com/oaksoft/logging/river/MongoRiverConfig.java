package com.oaksoft.logging.river;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.configuration.tree.ConfigurationNode;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentHelper;
import org.elasticsearch.river.mongodb.RiverSettings;

import com.oaksoft.enums.SourceType;
import com.oaksoft.logging.config.SourceConfig;

public class MongoRiverConfig extends SourceConfig{
	private RiverSettings riverSettings;
		
	private MongoRiverConfig(){
		this.type = SourceType.RIVER;
	}	
	
	public MongoRiverConfig(RiverSettings riverSettings) {
		this();
		this.riverSettings = riverSettings;
	}

	@Override
	protected void parseServerSpecificDetails(ConfigurationNode attrNode){
		
	}
	
	public static Collection<MongoRiverConfig> parseConfig(String  configFile) throws IOException{		
		
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(configFile);

        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();            
        Streams.copy(in, byteArr);
        RiverSettings riverSettings = new RiverSettings(Settings.builder().build(), XContentHelper.convertToMap(
        		new BytesArray(byteArr.toByteArray()), false).v2());	  
	    
        MongoRiverConfig config = new MongoRiverConfig(riverSettings);
		return Collections.singleton(config);		
	}
		
	public RiverSettings getRiverSettings() {
		return riverSettings;
	}
}
