package com.oceansky.health.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.oceansky.health.entity.MongoEntity;
import com.oceansky.health.entity.Product;

public class DaoFactory {

	@Autowired
	private ProductDao prodDao;
	@Autowired
	private CategoryDao catDao;
	@Autowired
	private PCategoryDao pcatDao;
	
	//private Map<Class<T>, MongoDao<T>> daoStore;
	
	public <T extends MongoEntity> MongoDao getDao(Class<T> t){		
		if(t.getName().equals(Product.class.getName())){
			return prodDao;
		}
		
		if(t.getName().equals(CategoryDao.class.getName())){
			return catDao;
		}
		
		if(t.getName().equals(PCategoryDao.class.getName())){
			return pcatDao;
		}
		
		return null;
	}
	
	public ProductDao getProdDao(){
		return prodDao;
	}
	
	public CategoryDao getCatDao(){
		return catDao;
	}
	
	public PCategoryDao getPcatDao(){
		return pcatDao;
	}

	public void setProdDao(ProductDao prodDao) {
		this.prodDao = prodDao;
	}

	public void setCatDao(CategoryDao catDao) {
		this.catDao = catDao;
	}

	public void setPcatDao(PCategoryDao pcatDao) {
		this.pcatDao = pcatDao;
	}
	
	public static void main(String[] args){
		DaoFactory df = new DaoFactory();
		df.getDao(Product.class);
		System.out.println("DONE");
	}
}
