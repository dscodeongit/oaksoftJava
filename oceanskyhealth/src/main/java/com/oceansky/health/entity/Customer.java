package com.oceansky.health.entity;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import com.oceansky.health.model.Cart;

@Entity (value="customers", noClassnameStored = true)
@Indexes(
	{
	    @Index(fields = @Field("userId")),
	    @Index(fields = @Field("email")),
		@Index(fields = @Field("phone"))
	}
)

public class Customer extends User{

	private static final int TEMP_PASSWORD_VALID_DAYS = 1; 
	
	@Transient
	private Cart cart;
	
	private String firstName;
	private String lastName;
	private Date lastTempPassSentOutTime;
	
	@Embedded
	private Address address;
	
	private UserStatus status;	
		
	public Customer(){		
	}
	
	private Customer(String userId, String password, String firstName, String lastName, String email, String phone, Address address){		
		super();
		this.userId = userId;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.phone = phone;
		this.address = address;
		this.status = UserStatus.PENDING;
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public UserStatus getStatus() {
		return status;
	}
	public void setStatus(UserStatus status) {
		this.status = status;
	}
		
	public boolean isNormal(){
		return getStatus() == UserStatus.NORMAL;
	}
			
	public boolean isBlocked(){
		return getStatus() == UserStatus.BLOCKED;
	}
	
	public boolean isInTempPass(){
		return getStatus() == UserStatus.TEMP_PASS;
	}
	
	public boolean isDeActivated(){
		return getStatus() == UserStatus.DECTIVATED;
	}
	
	public boolean loginEligible(){
		return isNormal() || !isTempPassExpired();
	}
	
	private int getTempPassRemainingMinutes(){
		DateTime  now = new DateTime();
		int passed = Minutes.minutesBetween(new DateTime(lastTempPassSentOutTime), now).getMinutes();
		int remianing = TEMP_PASSWORD_VALID_DAYS*24*60 - passed;
		return remianing;
	}
	
	private boolean isTempPassExpired() {
		return isInTempPass() && getTempPassRemainingMinutes() < 0;
	}
	@Override
	public Query<Customer> identityQuery(Datastore ds) {
		return ds.createQuery(Customer.class).field(Mapper.ID_KEY).equal(userId);
	}
	public Date getLastTempPassSentOutTime() {
		return lastTempPassSentOutTime;
	}
	public void setLastTempPassSentOutTime(Date lastTempPassSentOutTime) {
		this.lastTempPassSentOutTime = lastTempPassSentOutTime;
	}
	public Cart getCart() {
		return cart;
	}
	public void setCart(Cart cart) {
		this.cart = cart;
	}
	@Override
	public String toString() {
		return "Customer [firstName=" + firstName + ", lastName=" + lastName + ", lastTempPassSentOutTime="
				+ lastTempPassSentOutTime + ", address=" + address + ", status=" + status + ", userId=" + userId
				+ ", password=" + password + ", email=" + email + ", phone=" + phone + ", lastLoginTime="
				+ lastLoginTime + "]";
	}
	
	public static class Builder{
		private String userId; 
		private String pasword;
		private String firstName;
		private String lastName;
		private String email;
		private String phone;
		private Address address;
		
		public Customer build(){
			return new Customer(this.userId, this.pasword, this.firstName, this.lastName, this.email, 
					this.phone, this.address);
		}
		
		public Builder userId(String userId){
			this.userId = userId;
			return this;
		}
		
		public Builder pasword(String pasword){
			this.pasword = pasword;
			return this;
		}
		
		public Builder firstName(String firstName){
			this.firstName = firstName;
			return this;
		}
		
		public Builder lastName(String lastName){
			this.lastName = lastName;
			return this;
		}
		
		public Builder email(String email){
			this.email = email;
			return this;
		}
		
		public Builder phone(String phone){
			this.phone = phone;
			return this;
		}
	}

	public static void main(String[] args) {
		DateTime sentTime = DateTime.parse("2016-06-30 16:46:52", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		Customer cus = new Customer();
		cus.setStatus(UserStatus.TEMP_PASS);
		cus.setLastTempPassSentOutTime(new Date(sentTime.getMillis()));
		
		System.out.println(cus.loginEligible());
	}

}
