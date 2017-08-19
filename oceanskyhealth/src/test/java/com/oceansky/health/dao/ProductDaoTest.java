package com.oceansky.health.dao;

import java.util.Collection;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.oceansky.health.AbsTest;

import com.oceansky.health.entity.Category;
import com.oceansky.health.entity.PCategory;
import com.oceansky.health.entity.Product;
import com.oceansky.health.model.Tag;

public class ProductDaoTest extends AbsTest {


	@Test
	public void test() {
		//testFindAll();
		//testSave();
		//testRemove();
		//testUpdateFields();
		//testQuery();
		//testGetbyTag();
		System.out.println("Done");
	}
	
	private void testFindAll(){
		Collection<Product> prods = prodDao.findAll();
		for (Product product : prods) {
			System.out.println(product);
		}
	}
	
	private void testUpdateField(){
		Collection<Product> prods = prodDao.findByName("shoe");
		
		for (Product product : prods) {
			System.out.println(product);
			//pd.updateField(product, "descripton", "Addidas Sport Shoe for men");
			prodDao.updateField(product, "active", false);
		}
	}
	
	private void testUpdateFields(){
		Collection<Product> prods = prodDao.findByName("Materna");
		
		for (Product product : prods) {
			System.out.println(product);
			//pd.updateField(product, "descripton", "Addidas Sport Shoe for men");
			
			Map<String, Object> fields = Maps.newHashMap();
			fields.put("active", false);
			fields.put("price", 32.88);
			fields.put("descripton", "materna 150 caps");
			//fields.put("color", "White");
			Collection tags = Lists.newArrayList();
			//tags.add(Tag.HOT);
			tags.add(Tag.SPECIAL);
			
			fields.put("tags", tags);
			
			prodDao.updateFields(product, fields);
		}
	}

	private void testAddTag(){
		Collection<Product> prods = prodDao.findByTag(Tag.NEW);
		
		for (Product product : prods) {
			System.out.println(product);
			prodDao.addTag(product, Tag.HOT);
		}
	}
	
	private void testRemoveTag(){
		Collection<Product> prods = prodDao.findByTag(Tag.NEW);
		
		for (Product product : prods) {
			System.out.println(product);
			prodDao.removeTag(product, Tag.NEW);
		}
	}
	
	private void testGetbyTag(){
		Collection<Product> prods = prodDao.findByTag(Tag.NEW);
		
		for (Product product : prods) {
			System.out.println(product);
		}
	}
	private void testQuery(){
		PCategory pc = new PCategory();
		pc.setName("HEALTH");

		Category cat = new Category();		
		cat.setName("ADULT");
		cat.setParent(pc);
		Collection<Product> prods = prodDao.findByCategory(cat); //ds.find(Product.class).field("cat").equal(cat).asList();//.filter("foo >", 12);
	
		for (Product product : prods) {
			System.out.println(product);
		}
	}
	
	private void testSave(){
		createProduct();
		
	}
	
	private void testRemove(){		
		Product p= prodDao.findById("ht_sy00001");
		prodDao.delete(p);
		
	}
	
	private void createProduct(){
		PCategory pc1 = PCategory.createNew("FASHION");
		PCategory pc2 = PCategory.createNew("HEALTH");

		//pcatDao.save(pc);

		Category cat = Category.createNew(pc1, "MEN");
		Category cat2 = Category.createNew(pc1, "WOMEN");
		Category cat3 = Category.createNew(pc2, "WOMEN");
		Category cat4 = Category.createNew(pc2, "MEN");
		Category cat5 = Category.createNew(pc2, "CHILDREN");

		
		//ds.save(cat);
		//catDao.save(cat);		
		//Product pp = new Product(cat, 1238, "oats", "oats", "ÿ�����ͽԿ�ʳ", "image/oats_001.jepg", 2.01);
		
		Product p6 = new Product.Builder()
				.cat(cat5)
				.productNo("ht_fmt00006")
				.country("US")
				.descripton("calcium 475 ml")
				.name("liquid calcium")
				//.imageId("ht_fmt00006.jepg")
				.priceCurrency("USD")
				.size("476 ml")
				.color("")
				.manufacturer("Healthy Food")
				.price(28.99)
				.unit("bottle")
				.tag(Tag.NEW)
				.build();
		
		Product p1 = new Product.Builder()
				.cat(cat3)
				.productNo("ht_fmt00001")
				.country("USD")
				.descripton("Materna 100 capsule")
				.name("Materna")
				//.imageId("ht_fmt00001.jepg")
				.priceCurrency("USD")
				.size("100 caps")
				.color("")
				.manufacturer("Healthy Food")
				.price(28.99)
				.unit("bottle")
				.tag(Tag.NEW).tag(Tag.HOT)
				.build();
		
		
		Product p2 = new Product.Builder()
				.cat(cat4)
				.productNo("ht_fmt00002")
				.country("US")
				.descripton("Faterna 100 capsule")
				.name("Faterna")
				//.imageId("ht_fmt00002.jepg")
				.priceCurrency("USD")
				.size("100 caps")
				.color("")
				.manufacturer("Healthy Food")
				.price(28.99)
				.unit("bottle")
				.tag(Tag.NEW)
				.build();
		Product p3 = new Product.Builder()
				.cat(cat4)
				.productNo("ht_fmt00003")
				.country("US")
				.descripton("Faterna 100 capsule")
				.name("liquid calcium")
				//.imageId("ht_fmt00003.jepg")
				.priceCurrency("USD")
				.size("476 ml")
				.color("")
				.manufacturer("Healthy Food")
				.price(28.99)
				.unit("bottle")
				.tag(Tag.NEW)
				.build();
		
		Product p4 = new Product.Builder()
				.cat(cat2)
				.productNo("f_shoe00001")
				.country("US")
				.descripton("Men Sport shoe")
				.name("Shoe")
				//.imageId("f_shoe00001.jepg")
				.priceCurrency("USD")
				.size("10")
				.color("White")
				.manufacturer("Nike")
				.price(88.99)
				.unit("pair")
				.tag(Tag.NEW).tag(Tag.SPECIAL)
				.build();
		
		Product p5 = new Product.Builder()
				.cat(cat)
				.productNo("f_shoe0002")
				.country("US")
				.descripton("Women Sport shoe")
				.name("Shoe")
				//.imageId("f_shoe00021.jepg")
				.priceCurrency("USD")
				.size("6")
				.color("Red")
				.manufacturer("Nike")
				.price(98.99)
				.unit("pair")
				.tag(Tag.NEW).tag(Tag.SPECIAL)
				.build();
			
		prodDao.save(p1);
		prodDao.save(p2);
		prodDao.save(p3);
		prodDao.save(p4);
		prodDao.save(p5);		
		prodDao.save(p6);

	}


}
