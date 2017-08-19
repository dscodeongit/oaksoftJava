package com.oceansky.health.model;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.Lists;

import com.oceansky.health.entity.Customer;
import com.oceansky.health.entity.Order;

public class Cart {
	private final Customer cus;
	private Collection<Order> orders = Lists.newArrayList();

	public Cart(Customer cus) {
		this.cus = cus;
	}
	
	public void addToCart(Order order){
		orders.add(order);
	}
	
	public void addAllToCart(Collection<Order> orders){
		orders.addAll(orders);
	}
	
	public void removeFromCart(Order order){
		orders.remove(order);
	}
	
	public void removeAllFromCart(Collection<Order> orders){
		orders.removeAll(orders);
	}
	
	public Customer getCustomer() {
		return cus;
	}

	public Collection<Order> getOrders() {
		return Collections.unmodifiableCollection(orders);
	}

	public double getOderAfterTaxAmount(){
		Double amount = 0d;
		for (Order order : orders) {
			amount += order.getOderAmount();
		}
		return amount;
	}

	public double getPreTaxAmount(){
		Double amount = 0d;
		for (Order order : orders) {
			amount += order.getValueAmount();
		}
		return amount;
	}

	public double getSaleTaxAmount(){
		Double amount = 0d;
		for (Order order : orders) {
			amount += order.getSaleTaxAmount();
		}
		return amount;
	}
	
	public double getSavingAmount(){
		Double amount = 0d;
		for (Order order : orders) {
			amount += order.getSavingAmount();
		}
		return amount;
	}

}
