package com.oceansky.health.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.UpdateResults;

import com.oceansky.health.entity.Category;
import com.oceansky.health.entity.PCategory;
import com.oceansky.health.entity.Product;
import com.oceansky.health.model.Tag;

public class ProductDao extends MongoDao<Product> {

	public ProductDao(){
		super();		
	}
	
	public ProductDao(Datastore ds){
		super(ds);		
	}
	
	@Override
	public Key<Product> save(Product prod){
		if(prod.getCat().isNew()){
			PCategory pcat = prod.getCat().getParent();			
			if(pcat.isNew()){
				ds.save(pcat);
			}			
			ds.save(prod.getCat());
		}
		
		return ds.save(prod);
	}
	
	public Collection<Product> findByName(String value){
		return super.findByField("name", value);		
	}
	
	public Collection<Product> findByCategory(Category cat){
		return super.findByField("cat", cat);		
	}
	

	public Collection<Product> findByCategoryId(String catId, String pcatId){
		Map<String, Object> fields = new HashMap<>();
		fields.put("cat._id", catId);
		fields.put("cat.parent._id", pcatId);

		return super.findByField("cat._id", catId);		
	}
		
	public Collection<Product> findNewArrivals(){
		return findByTag(Tag.NEW);
	}
	
	public Collection<Product> findHotProducts(){
		return findByTag(Tag.HOT);
	}
	
	public Collection<Product> findSpecials(){
		return findByTag(Tag.SPECIAL);
	}
	
	public Collection<Product> findByTag(Tag tag){
		return super.findByArrayElement("tags",tag);
	}
		
	public UpdateResults addTag(Product prod, Tag tagToAdd){
		return super.addToArray(prod, "tags", tagToAdd);
	}
	
	public UpdateResults removeTag(Product prod, Tag tagToRemove){
		return super.removeFromArray(prod, "tags", tagToRemove);
	}
		
	public UpdateResults deActivate(Product prod){
		return super.updateField(prod, "active", false);
	}
	
	public UpdateResults marketProductForDeletion(Product prod){
		return deActivate(prod);
	}
	
	public long getProductCountForCategory(String catName){
		return ds.getCount(eqQuery("cat._id", catName));		
	}
	
	@Override
	protected Class<Product> getParamType() {
		return Product.class;
	}	
}
