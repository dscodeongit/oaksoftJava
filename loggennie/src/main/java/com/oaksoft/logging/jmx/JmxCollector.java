package com.oaksoft.logging.jmx;

import java.io.IOException;

import javax.management.MBeanServerConnection;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.oaksoft.logging.AbstractDataLogger;
import com.oaksoft.logging.exception.ConnectionException;

public class JmxCollector extends AbstractDataLogger {
	
	private volatile Connector connector;
	private volatile MBeanServerConnection connection;
	
	public JmxCollector(JmxServerConfig serverConfig, ThreadPoolTaskScheduler schedulingService) {
		super();
		this.serverConfig = serverConfig;
		this.service = schedulingService;
	}
		
	@Override
	protected void doConnect() throws ConnectionException{
		if(connector == null){
			connector = new Connector(serverConfig.getHost(), serverConfig.getPort(), serverConfig.getPath(), serverConfig.getUserName(),  serverConfig.getPassword());		
		}
		try {
			connector.connect();
			connection = connector.getMbeanServer();
		} catch (IOException e) {
			throw new ConnectionException(e);
		}
	}
	
	@Override
	protected void onException(Throwable t) {
		if((t instanceof IOException || t instanceof java.rmi.ConnectException) && serverConfig.getReconnectLimit() > 0){ 
			this.needsRestart = true;
		}		
	}
	
	@Override
	public void stop(){
		if(connector != null) {		
			logger.info("Shutting down JMX data logger for server: " + this.serverConfig.getHost());
			try {
				connector.close();
			} catch (IOException e) {
				logger.error("Failed to close JMX connection to :" + this.serverConfig.getHost(), e);
				//throw new RuntimeException("Failed to close Jmx connection to :" + this.serverConfig.getHost(), e);
			}finally{
				connector = null;
			}
		}
	}

	@Override
	public String toString() {
		return "JmxLogger [serverURL = " + serverConfig.getHost() + "]";
	}

	@Override
	protected Object getConnectionObject() {
		return this.connection;
	}
	
}
