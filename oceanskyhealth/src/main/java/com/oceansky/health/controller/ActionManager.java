package com.oceansky.health.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.oceansky.health.dao.AdministratorDao;
import com.oceansky.health.dao.CategoryDao;
import com.oceansky.health.dao.CustomerDao;
import com.oceansky.health.dao.OrderDao;
import com.oceansky.health.dao.PCategoryDao;
import com.oceansky.health.dao.ProductDao;

public abstract class ActionManager {
	@Autowired
	protected ProductDao prodDao;

	@Autowired
	protected CategoryDao catDao;
	
	@Autowired
	protected PCategoryDao pcatDao;
	
	@Autowired
	protected CustomerDao cusDao;
	
	@Autowired
	protected AdministratorDao adminDao;
	
	@Autowired
	protected OrderDao orderDao;
	
}
