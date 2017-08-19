package com.oceansky.health.dao;

import java.util.Collection;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateResults;

import com.google.common.collect.Maps;

import com.oceansky.health.entity.Customer;
import com.oceansky.health.entity.Order;
import com.oceansky.health.entity.OrderStatus;

public class OrderDao extends MongoDao<Order> {

	public OrderDao() {
		super();
	}

	public OrderDao(Datastore ds) {
		super(ds);
	}

	@Override
	protected Class<Order> getParamType() {
		return Order.class;
	}
	
	public Collection<Order> findByCustomer(Customer cus){
		return super.findByField("customer", cus);		
	}
	
	public Collection<Order> findUNPaidOrdersforCustomer(Customer cus){		
		return findOrdersforCustomerWithStatus(cus, OrderStatus.NEW);	
	}
	
	public Collection<Order> findOrdersToDeliverforCustomer(Customer cus){
		return findOrdersforCustomerWithStatus(cus, OrderStatus.PAID);
	}
	
	public Collection<Order> findOrdersInDeliveryforCustomer(Customer cus){
		return findOrdersforCustomerWithStatus(cus, OrderStatus.DELIVERING);
	}
	
	public Collection<Order> findDeliveredOrdersforCustomer(Customer cus){
		return findOrdersforCustomerWithStatus(cus, OrderStatus.DELIVERED);
	}
	
	public Collection<Order> findOrdersforCustomerWithStatus(Customer cus, OrderStatus status){
		return ds.find(getParamType(), "customer", cus).field("status").equal(status).asList();	
	}
	
	public Collection<Order> findActiveOrdersforCustomer(Customer cus){
		return ds.find(getParamType(), "customer", cus).field("status").notEqual(OrderStatus.DELIVERED).asList();	
	}
	
	public UpdateResults markOrderForDeletion(Order order){
		return super.updateField(order, "status", OrderStatus.DELETED);	
	}
}
