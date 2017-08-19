package com.oceansky.health.controller;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.oceansky.health.entity.Category;
import com.oceansky.health.entity.PCategory;
import com.oceansky.health.model.ActionMessage;
import com.oceansky.health.service.InventoryManager;

@Controller
@RequestMapping("admin/categories")
public class CatController {
    private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	InventoryManager invService;
            
	@RequestMapping("/fetchAll")
    public @ResponseBody Collection<PCategory> getCategories() {
    	Collection<PCategory> pcats = invService.getCategoryHiarchy();    	
        return pcats;
    }
	
	@RequestMapping(value = "/saveCat", method = RequestMethod.POST)
    public @ResponseBody void saveCategory(@RequestBody Category cat) {
    	invService.getCatDao().save(cat);
    }
	

	@RequestMapping(value = "/savePcat", method = RequestMethod.POST)
    public @ResponseBody void savePCategory(@RequestBody PCategory pcat) {
    	invService.getPcatDao().save(pcat);
    }
	
	@RequestMapping(value = "/removeCat/{id}", method = RequestMethod.DELETE)
    public @ResponseBody ActionMessage removeCategoryById(@PathVariable("id") String id) {
    	ActionMessage result = invService.deleteCategoryById(id);
    	return result;
    }
      
	@RequestMapping(value = "/removePcat/{id}", method = RequestMethod.DELETE)
    public @ResponseBody ActionMessage removePCategoryById(@PathVariable("id") String id) {
    	return invService.deleteParentCategoryById(id);
    }
	    
    @RequestMapping("/catlist")
    public String getCatList() {
        return "admin/categories/layout";
    }
}
