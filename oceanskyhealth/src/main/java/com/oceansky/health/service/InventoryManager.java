package com.oceansky.health.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oceansky.health.dao.CategoryDao;
import com.oceansky.health.dao.PCategoryDao;
import com.oceansky.health.dao.ProductDao;
import com.oceansky.health.entity.Category;
import com.oceansky.health.entity.PCategory;
import com.oceansky.health.entity.Product;
import com.oceansky.health.model.ActionMessage;
import com.oceansky.health.validation.MessageKey;

@Service("invService")
public class InventoryManager {
    private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	protected ProductDao prodDao;

	@Autowired
	protected CategoryDao catDao;
	
	@Autowired
	protected PCategoryDao pcatDao;
	
	public InventoryManager(){
		
	}
	
	public Collection<PCategory> getCategoryHiarchy(){
		Collection<PCategory> pcats = catDao.getCategoryHiarchy();
		Collection<PCategory> pcatsWithNoChild = pcatDao.findAll();
		Collection<PCategory> rsPcats = new ArrayList<>(pcats);
		
		for (PCategory pcat : pcatsWithNoChild) {
			if(!rsPcats.contains(pcat)){
				rsPcats.add(pcat);
			}
		}
		
		return rsPcats;
	}
	public void addParentCategory(PCategory pcat){
		logger.info("Adding parent category: " + pcat.getName());
		pcatDao.save(pcat);
	}

	public void addCategory(Category cat){
		PCategory pcat = pcatDao.findById(cat.getName());
		if(pcat == null){
			logger.info("Adding Parent Cat [{}] for cat [{}] ", cat.getParent().getName(), cat.getName());
			pcatDao.save(cat.getParent());
		}
		logger.info("Adding category: " + cat.getName());
		catDao.save(cat);
	}
	
	public ActionMessage deleteParentCategoryById(String id){
		PCategory pcat = pcatDao.findById(id);
		Collection<Category> cats = catDao.findByParent(pcat);
		if(cats != null && !cats.isEmpty()){
			logger.info("Failed to delete parent category: " + pcat.getName() + ". sub-cat not empty");
			return ActionMessage.error(MessageKey.ERR_SUB_CAT_NOT_EMPTY);
		}
		logger.info("Deleting parent category: " + pcat.getName());

		pcatDao.delete(pcat);
		return ActionMessage.sucess();
	}

	public ActionMessage deleteCategoryById(String id){
		long prodCount = prodDao.getProductCountForCategory(id);
		if(prodCount != 0){
			logger.info("Failed to delete category: " + id + ". Category not empty!, delete products in this Category first.");
			return ActionMessage.error(MessageKey.ERR_CAT_NOT_EMPTY);
		}
		logger.info("Deleting category: " + id);

		catDao.deleteById(id);
		return ActionMessage.sucess();
	}
	
	public ActionMessage saveProduct(Product prod){
		ActionMessage validateMsg = validateProduct(prod);
		if(!validateMsg.isSuccessful()){			
			return validateMsg;
		}
		logger.info("Saving Product: " + prod.getName());
		prodDao.save(prod);
		return ActionMessage.sucess();
	}
	
	public ActionMessage updateProduct(Product prod, Map<String, Object> fields){
		logger.info("updating Product: " + prod.getName());
		prodDao.updateFields(prod, fields);
		return ActionMessage.sucess();
	}
	
	public ActionMessage deleteProductById(String prodId){
		logger.info("Removing Product: with ID" + prodId);
		prodDao.deleteById(prodId);
		return ActionMessage.sucess();
	}
	
	public ActionMessage deleteProduct(Product prod){
		logger.info("Removing Product: " + prod.getName());
		prodDao.delete(prod);
		return ActionMessage.sucess();
	}
	
	private ActionMessage validateProduct(Product prod){
		if(prod.getCat() == null){
			return ActionMessage.error(MessageKey.ERR_PROD_CAT_MISSING);
		}
		if(StringUtils.isBlank(prod.getName())){
			return ActionMessage.error(MessageKey.ERR_PROD_NAME_MISSING);
		}
		/*
		if(prod.getPrice()==0){
			return ActionMessage.error(MessageKey.ERR_PROD_PRICE_MISSING);
		}
		
		if(StringUtils.isBlank(prod.getUnit())){
			return ActionMessage.error(MessageKey.ERR_PROD_UNIT_MISSING);
		}
		*/
		return ActionMessage.sucess();
	}

	public ProductDao getProdDao() {
		return prodDao;
	}

	public CategoryDao getCatDao() {
		return catDao;
	}

	public PCategoryDao getPcatDao() {
		return pcatDao;
	}
}
