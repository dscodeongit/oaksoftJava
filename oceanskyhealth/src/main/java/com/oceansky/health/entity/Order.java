package com.oceansky.health.entity;

import java.util.Date;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

@Entity(value="orders", noClassnameStored = true)
@Indexes(
	{
	    @Index(fields = @Field("orderId")),
	    @Index(fields = @Field("customer")),
	    @Index(fields = @Field("status"))

	}
)
public class Order extends MongoEntity {
	
	@Id
	private String orderId;
	
	@Reference
	private Customer customer;
	
	@Reference
	private Product product;
	
	private OrderStatus status;
	
    private int quantity;
    
    private double price;
    
    private double salePrice;
    
    private double saleDiscount;
    
    private double saleTaxRate;
    
    private Date deliveryDate;
    
    private Date deliveredDate;
    
    private String deliveryReceipt;
            	
	public Order() {		
	}
	
	public static Order createNew(String orderId, Customer customer, Product product, int quantity){
		return new Order(orderId, customer, product,quantity);
	}
	
	private Order(String orderId, Customer customer, Product product, int quantity){
		super();
		this.orderId = orderId;
		this.customer = customer;
		this.product = product;
		this.quantity = quantity;
		this.status = OrderStatus.NEW;
		this.price = product.getPrice();
		this.salePrice = product.calcSalePrice();
		this.saleDiscount = product.getDiscountRate();
		this.saleTaxRate = product.getSaleTaxRate();
	}
	
	public double getRevenueBeforeTax(){
		if(isOrderDelivered()){
			return salePrice*quantity*(1+saleTaxRate);
		}		
		return 0;
	}
	
	public double getRevenueAfterTax(){
		if(isOrderDelivered()){
			return salePrice*quantity;
		}		
		return 0;
	}
	
	public double getOderAmount(){
		return getValueAmount() + getSaleTaxAmount();
	}

	public double getValueAmount(){
		return product.calcSalePrice()*quantity;
	}

	public double getSaleTaxAmount(){
		return product.calcSalePrice()*quantity*product.getSaleTaxRate();
	}
	
	public double getSavingAmount(){
		if(product.isOnSale()){
			return (product.getPrice()-product.calcSalePrice())*quantity*(1+product.getSaleTaxRate());
		}
		return 0;
	}
	
	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(double salePrice) {
		this.salePrice = salePrice;
	}

	public double getSaleDiscount() {
		return saleDiscount;
	}

	public void setSaleDiscount(double saleDiscount) {
		this.saleDiscount = saleDiscount;
	}

	public double getSaleTaxRate() {
		return saleTaxRate;
	}

	public void setSaleTaxRate(double saleTaxRate) {
		this.saleTaxRate = saleTaxRate;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}
	
	public boolean isOrderDelivered(){
		return status == OrderStatus.DELIVERED;
	}
	
	public boolean isOrderInDelivery(){
		return status == OrderStatus.DELIVERING;
	}
	
	public boolean isOrderPaid(){
		return status == OrderStatus.PAID;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Date getDeliveredDate() {
		return deliveredDate;
	}

	public void setDeliveredDate(Date deliveredDate) {
		this.deliveredDate = deliveredDate;
	}

	public String getDeliveryReceipt() {
		return deliveryReceipt;
	}

	public void setDeliveryReceipt(String deliveryReceipt) {
		this.deliveryReceipt = deliveryReceipt;
	}

	@Override
	public Query<? extends MongoEntity> identityQuery(Datastore ds) {
	      return ds.createQuery(Order.class).field(Mapper.ID_KEY).equal(orderId);
	}

	@Override
	public String toString() {
		return "Order [orderId=" + orderId + ", customer=" + customer + ", product=" + product + ", status=" + status
				+ ", quantity=" + quantity + ", salePrice=" + salePrice + ", saleDiscount=" + saleDiscount
				+ ", saleTaxRate=" + saleTaxRate + ", creationTime=" + creationTime + ", lastUpdateTime="
				+ lastUpdateTime + "]";
	}
}
