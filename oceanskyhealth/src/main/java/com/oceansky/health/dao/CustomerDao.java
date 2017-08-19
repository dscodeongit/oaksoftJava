package com.oceansky.health.dao;

import java.util.Collection;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import com.oceansky.health.entity.Customer;
import com.oceansky.health.entity.UserStatus;

public class CustomerDao extends MongoDao<Customer> {

	public CustomerDao() {
		super();
	}

	public CustomerDao(Datastore ds) {
		super(ds);		
	}
	
	@Override
	protected Class<Customer> getParamType() {
		// TODO Auto-generated method stub
		return Customer.class;
	}
	
	public Collection<Customer> findByEmail(String email){
		return super.findByField("email", email);		
	}
	
	public Collection<Customer> findByPhone(String phone){
		return super.findByField("phone", phone);		
	}
	
	public UpdateResults marketForDelete(Customer cus){
		UpdateOperations<Customer> ops = ds.createUpdateOperations(getParamType()).set("status", UserStatus.DELETED);
		return super.update(cus, ops);
	}

	public UpdateResults block(Customer cus){
		return super.updateField(cus, "status", UserStatus.BLOCKED);
	}
	
	public UpdateResults tempPass(Customer cus){
		return super.updateField(cus, "status", UserStatus.TEMP_PASS);
	}
	
	public UpdateResults deActivate(Customer cus){
		return super.updateField(cus, "status", UserStatus.DECTIVATED);
	}
	
}
