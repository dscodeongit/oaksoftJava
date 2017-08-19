package com.oceansky.health.config;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.mongodb.MongoClient;

import com.oceansky.health.dao.AdministratorDao;
import com.oceansky.health.dao.CategoryDao;
import com.oceansky.health.dao.CustomerDao;
import com.oceansky.health.dao.OrderDao;
import com.oceansky.health.dao.PCategoryDao;
import com.oceansky.health.dao.ProductDao;
import com.oceansky.health.service.InventoryManager;

@Configuration
@PropertySource("classpath:app.properties")
public class AppConfig {
	@Value("${mongodb.host}")
	private String mongoHost;
	@Value("${mongodb.port}")
	private int port;
	@Value("${mongodb.dbname}")
	private String dbName;
	
	@Bean
	public Datastore getDataStore(){
		final Morphia morphia = new Morphia();
		morphia.mapPackage("mongo.lab.shop.entity");
		Datastore ds = morphia.createDatastore(new MongoClient(mongoHost, port), dbName);
		
		ds.ensureIndexes();		
		return ds;
	}
	
    @Bean
    public CategoryDao getCategryDao() {
        return new CategoryDao(getDataStore());
    }
    
    @Bean
    public PCategoryDao getPCategryDao() {
        return new PCategoryDao(getDataStore());
    }
    
    @Bean
    public ProductDao getProductDao() {
        return new ProductDao(getDataStore());
    }
    
    @Bean
    public CustomerDao getCustomerDao() {
        return new CustomerDao(getDataStore());
    }
    
    @Bean
    public AdministratorDao getAdminDao() {
        return new AdministratorDao(getDataStore());
    }
    
    @Bean
    public OrderDao getOrderDao(){
    	return new OrderDao(getDataStore());
    }
    
    @Bean
    public InventoryManager getInvManager(){
    	return new InventoryManager();
    }
    
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfig() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
