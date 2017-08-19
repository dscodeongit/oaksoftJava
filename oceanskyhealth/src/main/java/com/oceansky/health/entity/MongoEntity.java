package com.oceansky.health.entity;

import java.util.Date;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;

public abstract class MongoEntity {
	protected Date creationTime;
	protected Date lastUpdateTime;
	
	public abstract Query<? extends MongoEntity> identityQuery(Datastore ds);
	
	public MongoEntity(){
		this.creationTime = new Date();
		this.lastUpdateTime = new Date();
	}
			
	public Date getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}	

}
