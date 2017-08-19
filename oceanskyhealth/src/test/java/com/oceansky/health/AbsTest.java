package com.oceansky.health;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oceansky.health.config.AppConfig;
import com.oceansky.health.dao.AdministratorDao;
import com.oceansky.health.dao.CategoryDao;
import com.oceansky.health.dao.CustomerDao;
import com.oceansky.health.dao.OrderDao;
import com.oceansky.health.dao.PCategoryDao;
import com.oceansky.health.dao.ProductDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class AbsTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}

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
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}
