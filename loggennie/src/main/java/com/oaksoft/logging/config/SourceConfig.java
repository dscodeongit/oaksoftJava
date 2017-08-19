package com.oaksoft.logging.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.tree.ConfigurationNode;

import com.google.common.collect.Lists;
import com.oaksoft.enums.SourceType;

public abstract class SourceConfig {
	
	private static final Long DEFAULT_POLL_INTERVAL = 5000L; 
	
	protected String url;
	protected String alias;

	protected String userName;
	protected String password;
	
	protected String host;
	
	protected Integer port;
	protected String path;

	protected SourceType type;
	protected long pollInterval;	
	
	protected int reconnectLimit;
	
	protected long reconnectInterval;

	protected volatile String apiVersion;
	protected Collection<Monitor> monitors;

	
	public void parseConnectionDetails(ConfigurationNode configurationNode) {
		List<ConfigurationNode> attrNodes = configurationNode.getAttributes();
		
		for (ConfigurationNode attrNode : attrNodes) {
			String value = String.valueOf(attrNode.getValue());
			if(attrNode.getName().equals("alias")){
				setAlias(value);
			} else if(attrNode.getName().equals("user")){
				setUserName(value);
			}else if(attrNode.getName().equals("password")){
				setPassword(value);
			}else if(attrNode.getName().equals("pollInterval")){
				setPollInterval(Long.valueOf(value));
			}else if(attrNode.getName().equals("reconnectLimit")){
				setReconnectLimit(Integer.valueOf(value));
			}else if(attrNode.getName().equals("reconnectInterval")){
				setReconnectInterval(Long.valueOf(value));
			}else{
				parseServerSpecificDetails(attrNode);
			}	
		}
	}
	
	protected  void parseServerSpecificDetails(ConfigurationNode attrNode){}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public SourceType getType() {
		return type;
	}	
	
	public long getPollInterval() {
		return pollInterval;
	}
	
	public long getDefaultPollInterval(){
		return DEFAULT_POLL_INTERVAL;
	}

	public void setPollInterval(long pollInterval) {
		this.pollInterval = pollInterval;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public int getReconnectLimit() {
		return reconnectLimit;
	}

	public void setReconnectLimit(int reconnectLimit) {
		this.reconnectLimit = reconnectLimit;
	}

	public long getReconnectInterval() {
		return reconnectInterval;
	}

	public void setReconnectInterval(long reconnectInterval) {
		this.reconnectInterval = reconnectInterval;
	}

	public void setType(SourceType type) {
		this.type = type;
	}
	
	public boolean reconnectOnfailure(){
		return reconnectLimit > 0;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	protected void setMonitors(Collection<? extends Monitor> monitors) {
		this.monitors = Lists.newArrayList(monitors);
	}

	public Collection<Monitor> getMonitors() {
		return Collections.unmodifiableCollection(monitors);
	}
}
