package com.oceansky.health.controller;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;

import com.google.common.collect.Sets;
import com.oceansky.health.entity.Category;
import com.oceansky.health.entity.PCategory;
import com.oceansky.health.entity.Product;
import com.oceansky.health.model.ActionMessage;
import com.oceansky.health.model.Tag;
import com.oceansky.health.service.InventoryManager;

@PropertySource("classpath:app.properties")
@Controller
@RequestMapping("admin/products")
public class ProductController {
    private Logger logger = LogManager.getLogger(this.getClass());
    private DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyyMMddHHmmss");

	@Autowired
	InventoryManager invService;
	@Autowired
	private ServletContext servletContext;

	@Value("${image.dir}")
    private String imageDir;
	
    @RequestMapping("/fetchAll")
    public @ResponseBody Collection<Product> getProducts() {
    	Collection<Product> prods = invService.getProdDao().findAll();
    	for (Product prod : prods) {
    		prepareForView(prod);
		}
        return prods;
    }

    
    @RequestMapping("/fetchByType/{parentCat}/{cat}")
    public @ResponseBody Collection<Product> getProductsByType(@PathVariable("cat") String cat, @PathVariable("parentCat") String pcat) {
    	Collection<Product> prods = invService.getProdDao().findByCategoryId(cat, pcat);
    	for (Product prod : prods) {
    		prepareForView(prod);
		}
        return prods;
    }
    
    
    @RequestMapping(value = "/saveProduct", method = RequestMethod.POST)
    public @ResponseBody Product saveProduct(@RequestBody Product prod) {    	
    	prepareForMongo(prod);
    	invService.saveProduct(prod);
    	cleanImagesForProd(prod);
    	return prod;
    }
    
    private void cleanImagesForProd(final Product prod){
    	Set<File> existingImages = getAllImagesforProd(prod.getProductNo());
    	for (File file : existingImages) {
			String fName = file.getName();
			if(!prod.getImages().contains(fName)){
				logger.info("deleting image : {} for prodNo: {}", fName, prod.getProductNo());
				FileUtils.deleteQuietly(file);
			}
		}
    }

	@RequestMapping(value = "/saveProductWithImage", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public @ResponseBody Product saveProductWithImage(@RequestPart(value = "prod") Product prod, @RequestPart("file") MultipartFile imageFile) {
    	    	
    	if (!imageFile.isEmpty()) {
    		String imageNo = dtf.print(DateTime.now());
        	String imageFileName = prod.getProductNo()+"_" + imageNo + "."+ StringUtils.substringAfterLast(imageFile.getOriginalFilename(), ".");
        	prod.addImage(imageFileName);
			try {				
				Files.copy(imageFile.getInputStream(), Paths.get(imageDir, imageFileName));
				logger.info("Successfully uploaded image file {} for {}",  imageFileName, prod.getProductNo());
			} catch (IOException|RuntimeException e) {
				logger.info("Error uploading image file {} for {}",  imageFileName, prod.getProductNo());
			}
		} else {
			logger.info("No image file is added for prod : {} " + prod.getProductNo());
		}
		return saveProduct(prod);
    }

    @RequestMapping(value = "/removeProduct/{id}", method = RequestMethod.DELETE)
    public @ResponseBody ActionMessage removeProduct(@PathVariable("id") String id) {
    	return invService.deleteProductById(id);
    }
       
    @RequestMapping(value = "/images/", method = RequestMethod.POST)
    public  @ResponseBody ActionMessage handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable("id") String id, RedirectAttributes redirectAttributes) {
    	
		if (!file.isEmpty()) {
			try {				
				Files.copy(file.getInputStream(), Paths.get(imageDir, file.getOriginalFilename()));
				redirectAttributes.addFlashAttribute("message",
						"You successfully uploaded " + file.getOriginalFilename() + "!");
			} catch (IOException|RuntimeException e) {
				redirectAttributes.addFlashAttribute("message", "Failued to upload " + file.getOriginalFilename() + " => " + e.getMessage());
				return ActionMessage.error("file.upload.failure!");
			}
		} else {
			redirectAttributes.addFlashAttribute("message", "Failed to upload " + file.getOriginalFilename() + " because it was empty");
			return ActionMessage.error("file.upload.failure.file.empty!");

		}
		
		return ActionMessage.sucess();
	}
    

    @RequestMapping("/layout")
    public String getProductPartialPage() {
        return "products/layout";
    }
    
    @RequestMapping("/prodlist")
    public String getProductListPage() {
        return "admin/products/prodlist";
    }
    
    @RequestMapping("/proddetails")
    public String getProductDetailsPage() {
        return "admin/products/proddetails";
    }

    @RequestMapping("/newprod")
    public String getNewProductPage() {
        return "admin/products/newprod";
    }
    
    @RequestMapping("/editprod")
	public String getEditProductPage() {
        return "admin/products/editprod";
    }
    
    private Set<File> getAllImagesforProd(final String productNo) {
    	File dir = new File(imageDir);

        // list the files using our FileFilter
        File[] files = dir.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				return name.startsWith(productNo);
			}
		});
		return Sets.newHashSet(files);
	}
        
	private void prepareForView(Product prod) {
		prod.setCreationTime(null);
		prod.setLastUpdateTime(null);
		if(prod.getTags() != null){
			if(prod.getTags().contains(Tag.NEW)){
				prod.setNew(true);
			}
			if(prod.getTags().contains(Tag.HOT)){
				prod.setHot(true);
			}
		}
	}
    
	private void prepareForMongo(Product prod) {
    	if(StringUtils.isBlank(prod.getProductNo())){
    		prod.setProductNo(createProdNo(prod));
    	}
    	
		Set<Tag> tags = Sets.newHashSet();
		if(prod.isHot()){
			tags.add(Tag.HOT);			
		}
		if(prod.isNew()){
			tags.add(Tag.NEW);			
		}
		prod.setTags(tags);
	}
	
    private String createProdNo(Product prod) {
    	String prodNo = (prod.getName()+"_"+ dtf.print(DateTime.now())).toLowerCase();
		return StringUtils.replace(prodNo, " ", "_");
	}
    
}
