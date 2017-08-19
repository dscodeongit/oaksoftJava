package com.oceansky.health.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oceansky.health.dao.OrderDao;
import com.oceansky.health.entity.Customer;
import com.oceansky.health.entity.Order;
import com.oceansky.health.model.Cart;

@Service("cartService")
public class CartManager {
	@Autowired
	protected OrderDao orderDao;
	
	public Cart createCart(Customer cus){
		Cart cart = new Cart(cus);
		Collection<Order> orders = orderDao.findUNPaidOrdersforCustomer(cus);
		cart.addAllToCart(orders);
		return cart;
	}
	
	public void addOrderToCart(Cart cart, Order order){
		cart.addToCart(order);
		orderDao.save(order);
	}
	
	public void addOrdersToCart(Cart cart, Collection<Order> orders){
		cart.addAllToCart(orders);
		orderDao.save(orders);
	}
	
	public void removeOrderFromCart(Cart cart, Order order){
		cart.removeFromCart(order);
		orderDao.delete(order);
	}
	
	public void removeOrdersFromCart(Cart cart, Collection<Order> orders){
		cart.removeAllFromCart(orders);
		orderDao.delete(orders);
	}
	
	public double getCartAfterTaxAmount(Cart cart){
		return cart.getOderAfterTaxAmount();
	}
	
	public double getCartSaleTax(Cart cart){
		return cart.getSaleTaxAmount();
	}
	
	public double getCartPreTaxAmount(Cart cart){
		return cart.getPreTaxAmount();
	}
	
	public double getCartSavingAmount(Cart cart){
		return cart.getSavingAmount();
	}

	public OrderDao getOrderDao() {
		return orderDao;
	}
}
