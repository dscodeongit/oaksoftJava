package com.oaksoft.logging.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.oaksoft.logging.AbstractDataLogger;
import com.oaksoft.logging.exception.ConnectionException;

public class JdbcCollector extends AbstractDataLogger {
	
	private static final String SQL_STATE_PREFIX_CONN_ERROR = "08";

	private volatile JdbcConnectionPool connPool;
	private volatile Connection connection;
		
	public JdbcCollector(JdbcServerConfig serverConfig, ThreadPoolTaskScheduler schedulingService) {
		super();
		this.serverConfig = serverConfig;
		this.service = schedulingService;
	}
	
	@Override
	protected void doConnect() throws ConnectionException {
		if(connPool == null){
			connPool = new  JdbcConnectionPool((JdbcServerConfig)serverConfig);
		}
		try {
			connection = connPool.getConnection();
		} catch (SQLException e) {
			throw new ConnectionException(e);
		}
	}
		
	@Override
	protected void onException(Throwable t){
		if(t instanceof SQLException && serverConfig.getReconnectLimit() > 0){			
			SQLException e = (SQLException)t;
			if(e.getSQLState().startsWith(SQL_STATE_PREFIX_CONN_ERROR)){
				this.needsRestart = true;
			}
		}		
	}
	
	@Override
	public void stop(){		
		if(connection != null){
			logger.info("Shutting down JDBC data logger for DB URL : " + this.serverConfig.getUrl());
			try {
				connection.close();
			} catch (SQLException e) {
				logger.error("Failed to close JDBC connection to :" + this.serverConfig.getUrl(), e);					
			} finally {
				connection = null;
			}
			
			if(connPool != null){
				connPool.shutdown();
			}
		}
	}
	
	@Override
	protected Object getConnectionObject(){
		return this.connection;
	}
	@Override
	public String toString() {
		return "JdbcLogger [serverURL = " + serverConfig.getUrl() + "]";
	}
	
}
