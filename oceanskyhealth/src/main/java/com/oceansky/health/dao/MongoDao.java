package com.oceansky.health.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.mongodb.WriteResult;

import com.oceansky.health.entity.MongoEntity;

public abstract class MongoDao<T extends MongoEntity> {
    protected Logger logger = LogManager.getLogger(this.getClass());

	protected Datastore ds;
		
	public MongoDao(){
		
	}
	
	public MongoDao(Datastore ds){
		this.ds = ds;
	}
	
	/** CRUD Operations**/
	
	//C 		
	public Key<T> save(T entity){
		logger.info("Saving enitity: " + entity);
		return ds.save(entity);
	}
	
	public Iterable<Key<T>> save(Collection<T> entities){
		logger.info("Saving enitity: " + entities);
		return ds.save(entities);
	}
	
	//R
	public Collection<T> findAll(){		
		return ds.find(getParamType()).asList();		
	}
	
	public T findById(Object id){		
		return ds.find(getParamType(), Mapper.ID_KEY, id).limit(1).get();		
	}
	
	public Collection<T> findByField(String field, Object value){
		return ds.find(getParamType()).disableValidation().field(field).equal(value).asList();
	}
		
	public Collection<T> findByArrayElement(String arrayField, Object value){
		return ds.createQuery(getParamType()).field(arrayField).equal(value).asList();
	}
		
	//U 
	public UpdateResults updateFields(T entiry, Map<String, Object> fields){
		UpdateOperations<T> ops = ds.createUpdateOperations(getParamType());
		for (String field : fields.keySet()) {				
			ops.set(field, fields.get(field));
		}
		return update(entiry, ops);
	}			
	
	public UpdateResults updateField(T entiry, String field, Object value){
		UpdateOperations<T> ops = ds.createUpdateOperations(getParamType());
		ops.set(field, value);
		return update(entiry, ops);
	}	
	
	public UpdateResults addToArray(T entiry, String arrayName, Object element){
		UpdateOperations<T> ops = ds.createUpdateOperations(getParamType()).add(arrayName, element);
		return update(entiry, ops);
	}
	
	public UpdateResults removeFromArray(T entiry, String arrayName, Object element){
		UpdateOperations<T> ops = ds.createUpdateOperations(getParamType()).removeAll(arrayName, element);
		return update(entiry, ops);
	}
	
	public UpdateResults update(T entity, UpdateOperations<T> operations){
		operations.set("lastUpdateTime", new Date());
		return ds.update(entity, operations);
	}
				
	//D
	public WriteResult delete(T entity){
		return ds.delete(entity);
	}	
	
	public WriteResult delete(Collection<T> entities){
		return ds.delete(entities);
	}	
	
	public WriteResult deleteById(Object id){
		return ds.delete(identityQuery(id));		
	}
	
	public Query<T> identityQuery(Object id) {
		return ds.createQuery(getParamType()).field(Mapper.ID_KEY).equal(id);
    } 
	
	public Query<T> eqQuery(String field, Object value) {
		return ds.createQuery(getParamType()).field(field).equal(value);
	}
	
	protected abstract Class<T> getParamType();	
	
}
