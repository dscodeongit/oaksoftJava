package com.oceansky.health.entity;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

@Entity(value="administrator", noClassnameStored=true)
public class Administrator extends User{
	private String lastLoginIp;
	
	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}
	@Override
	public Query<Administrator> identityQuery(Datastore ds) {
		return ds.createQuery(Administrator.class).field(Mapper.ID_KEY).equal(userId);
	}
}
