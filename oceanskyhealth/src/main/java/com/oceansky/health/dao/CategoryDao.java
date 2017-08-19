package com.oceansky.health.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mongodb.morphia.Datastore;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.oceansky.health.entity.Category;
import com.oceansky.health.entity.PCategory;

public class CategoryDao extends MongoDao<Category> {
	public CategoryDao(Datastore ds){
		super(ds);
	}
	
	public CategoryDao(){
		super();
	}

	@Override
	protected Class<Category> getParamType() {
		return Category.class;
	}
	
	public Collection<Category> findByParent(PCategory pcat){
		return super.findByField("parent", pcat);
	}
	
	public Collection<PCategory> getCategoryHiarchy(){
		Collection<Category> cats = findAll();
		Map<String, PCategory> catMap = Maps.newHashMap();
		
		for (Category cat : cats) {
			PCategory pcat = cat.getParent();
			cat.setParent(null);
			if(!catMap.containsKey(pcat.getName())){
				catMap.put(pcat.getName(), pcat);
			}
			
			if(!catMap.get(pcat.getName()).getCats().contains(cat)){
				catMap.get(pcat.getName()).getCats().add(cat);
			}
		}
		
		return catMap.values();
	}
}
