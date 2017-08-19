package com.oaksoft.logging.river;

import org.elasticsearch.river.mongodb.MongoDBRiver;

import com.oaksoft.logging.AbstractDataLogger;
import com.oaksoft.logging.config.SourceConfig;
import com.oaksoft.logging.exception.ConnectionException;
public class MongoRiverCollector extends AbstractDataLogger {
	private final MongoDBRiver river; 
	
	private int remainingRestartLimit; 
	private final int maxRestart;
	public MongoRiverCollector(SourceConfig config){
		this.serverConfig = config;
		MongoRiverConfig mrConfig = (MongoRiverConfig)serverConfig;
		river = new MongoDBRiver(mrConfig.getRiverSettings());
		this.maxRestart = river.getRestatLimit();
		this.remainingRestartLimit = this.maxRestart;
	}
	
	@Override
	
	public void start() {
		try {
			logger.info("Starting MongoDB River ...");		
			river.start();
		} catch (Throwable t) {
			logger.error("Mongo River Failed with Error", t);
			throw new RuntimeException(t);
		}		
	}
			
	@Override
	protected void onException(Throwable t){
		
	}
	
	@Override
	public void stop(){
		river.stop();
	}
	

	@Override
	protected void doConnect() throws ConnectionException {	
		
	}

	@Override
	protected Object getConnectionObject() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public boolean needsRestart() {
		return river.needsRestart() && this.remainingRestartLimit > 0;
	}	
		
	@Override
	public void reStart() {
		stop();
		logger.info("Restart Mongo River. Remaining attempts : " + this.remainingRestartLimit);
		this.remainingRestartLimit --;
		start();		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!needsRestart()){
			this.remainingRestartLimit = this.maxRestart;
		}
	}
	
	@Override
	public String toString() {
		return river.getMongoElasticInfo();
	}	
}
