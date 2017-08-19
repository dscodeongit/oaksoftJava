package com.oceansky.health.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import com.google.common.collect.Sets;

import com.oceansky.health.exception.InvalidInputException;
import com.oceansky.health.model.Tag;

@Entity(value="products", noClassnameStored=true)
@Indexes({
	    @Index(fields = @Field("name")),
	    @Index(fields = @Field("cat")),
		@Index(fields = @Field("creationTime")),
	    @Index(fields = @Field("tags"))
    }
)

public class Product extends MongoEntity{
	
	@Id
	private  String productNo;
	//@Reference(lazy = true)
	private Category cat;
	private String name;
	private String descripton;
	private String instructons;
	private String manufacturer;
	private String country;
	private Set<String> images = Sets.newHashSet();
	private double price;
	private double saleTaxRate;
	private String unit;
	private String size;
	private String color;	
	private String priceCurrency;
	private boolean onSale;
	private double discountRate;
	private boolean active;
	@Transient
	private boolean isNew;
	@Transient
	private boolean isHot;
	private Set<Tag> tags = Sets.newHashSet();
	
		
	private Product(Category cat, String prodNo, String name, String descripton, String instructons, 
					String manufacturer, String country, Set<String> images, Double price, String unit, 
					String size, String color, String currency, boolean onSale, double discountRate, Set<Tag> tags) {
		super();
		this.cat = cat;
		this.productNo = prodNo;
		this.name = name;
		this.descripton = descripton;
		this.instructons = instructons;
		this.manufacturer = manufacturer;
		this.country = country;
		this.images.addAll(images);
		this.price = price;
		this.unit = unit;
		this.size = size;
		this.color = color;
		this.priceCurrency = currency;
		this.onSale = onSale;
		this.discountRate = discountRate;
		this.tags.addAll(tags);
		this.active = true;
	}
		
	public Product(){
		this.creationTime = new Date();
	}
	@Override
	public Query<Product> identityQuery(Datastore ds)
    {
		return ds.createQuery(Product.class).field(Mapper.ID_KEY).equal(productNo);
    }
	public String getProductNo() {
		return productNo;
	}

	public void setProductNo(String productNo) {
		this.productNo = productNo;
	}

	public Category getCat() {
		return cat;
	}
	public void setCat(Category cat) {
		this.cat = cat;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescripton() {
		return descripton;
	}
	public void setDescripton(String descripton) {
		this.descripton = descripton;
	}
	public String getInstructons() {
		return instructons;
	}
	public void setInstructons(String instructons) {
		this.instructons = instructons;
	}
	public Set<String> getImages() {
		return images;
	}
	public void setImages(Set<String> images) {
		this.images.addAll(images);
	}
	
	public void addImage(String image){
		this.images.add(image);
	}
	
	public Double getPrice() {
		return price;
	}
	public Double calcSalePrice(){
		if(onSale){
			return price*(1-discountRate);
		}else{
			return price;
		}
	}
	public String getPriceCurrency() {
		return priceCurrency;
	}
	public void setPriceCurrency(String priceCurrency) {
		this.priceCurrency = priceCurrency;
	}
	public boolean isOnSale() {
		return onSale;
	}
	public void setOnSale(boolean onSale) {
		this.onSale = onSale;
	}
	public double getDiscountRate() {
		return discountRate;
	}
	public void setDiscountRate(double discountRate) {
		this.discountRate = discountRate;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void addTag(Tag tag){
		this.tags.add(tag);
	}
	
	public void removeTag(Tag tag){
		this.tags.remove(tag.name());
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}


	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public double getSaleTaxRate() {
		return saleTaxRate;
	}

	public void setSaleTaxRate(double saleTaxRate) {
		this.saleTaxRate = saleTaxRate;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isHot() {
		return isHot;
	}

	public void setHot(boolean isHot) {
		this.isHot = isHot;
	}

	@Override
	public String toString() {
		return "Product [productNo=" + productNo + ", cat=" + cat + ", name=" + name + ", descripton=" + descripton
				+ ", instructons=" + instructons + ", manufacturer=" + manufacturer + ", country=" + country
				+ ", images=" + images + ", price=" + price + ", unit=" + unit + ", size=" + size + ", color=" + color
				+ ", priceCurrency=" + priceCurrency + ", onSale=" + onSale + ", discountRate=" + discountRate
				+ ", creationDate=" + creationTime + ", lastUpdateDate=" + lastUpdateTime + ", tags=" + tags + "]";
	}
	

	public static class Builder{
		private  String productNo;
		private Category cat;
		private String name;
		private String descripton;
		private String instructons;
		private String manufacturer;
		private String country;
		private Set<String> images = Sets.newHashSet();
		private Double price;
		private String unit;
		private String size;
		private String color;	
		private String priceCurrency;
		private boolean onSale;
		private double discountRate;
		private Set<Tag> tags = Sets.newHashSet();

		public Builder(){
			
		}
		
		public Product build(){
			return new Product(this.cat, this.productNo, this.name, this.descripton, this.instructons, 
					this.manufacturer, this.country, this.images, this.price, this.unit, this.size, this.color, this.priceCurrency, this.onSale, this.discountRate, this.tags);
		}
		
		public Builder productNo(final String prodNo){
			this.productNo = prodNo;
			return this;
		}
		
		public Builder cat(final Category cat){
			this.cat = cat;
			return this;
		}
		
		public Builder name(final String name){
			this.name = name;
			return this;
		}
		
		public Builder descripton(final String descripton){
			this.descripton = descripton;
			return this;
		}
		
		public Builder instructons(final String instructons){
			this.instructons = instructons;
			return this;
		}
		
		public Builder manufacturer(final String manufacturer){
			this.manufacturer = manufacturer;
			return this;
		}
		
		public Builder country(final String country){
			this.country = country;
			return this;
		}
		
		public Builder images(final Set<String> images){
			this.images = images;
			return this;
		}
		
		public Builder price(final double price){
			this.price = price;
			return this;
		}
		
		public Builder unit(final String unit){
			this.unit = unit;
			return this;
		}
		
		public Builder size(final String size){
			this.size = size;
			return this;
		}
		
		public Builder color(final String color){
			this.color = color;
			return this;
		}
		
		public Builder priceCurrency(final String priceCurrency){
			this.priceCurrency = priceCurrency;
			return this;
		}
		
		public Builder onSale(final boolean onSale){
			this.onSale = onSale;
			return this;
		}
		
		public Builder tags(final Collection<Tag> tags){
			this.tags.addAll(tags);
			return this;
		}
		
		public Builder tag(final Tag tag){
			this.tags.add(tag);
			return this;
		}
			
		public Builder discountRate(final double discountRate) throws InvalidInputException{
			if(discountRate < 0 || discountRate >=1){
				throw new InvalidInputException("discountRate cannot be less than 0 and greate than 1.");
			}
			this.discountRate = discountRate;
			return this;
		}
	}
}
