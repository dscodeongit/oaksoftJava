package com.oceansky.health.dao;

import org.mongodb.morphia.Datastore;

import com.oceansky.health.entity.Administrator;

public class AdministratorDao extends MongoDao<Administrator> {

	public AdministratorDao() {
		super();
	}

	public AdministratorDao(Datastore ds) {
		super(ds);
	}
		@Override
	protected Class<Administrator> getParamType() {
		// TODO Auto-generated method stub
		return Administrator.class;
	}

}
