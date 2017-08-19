package com.oceansky.health.dao;

import org.mongodb.morphia.Datastore;

import com.oceansky.health.entity.PCategory;

public class PCategoryDao extends MongoDao<PCategory> {

	public PCategoryDao() {
		super();
	}

	public PCategoryDao(Datastore ds) {
		super(ds);
	}

	@Override
	protected Class<PCategory> getParamType() {
		return PCategory.class;
	}

}
