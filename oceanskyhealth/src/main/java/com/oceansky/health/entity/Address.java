package com.oceansky.health.entity;

public class Address {
	
	private String streetAndNumber;
	private String city;
	private String province;
	private String country;
	private String postCode;
	
	private String countryCode;
			
	public Address(String streetAndNumber, String city, String province, String country, String postCode) {
		super();
		this.streetAndNumber = streetAndNumber;
		this.city = city;
		this.province = province;
		this.country = country;
		this.postCode = postCode;
	}
	
	public Address() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getStreetAndNumber() {
		return streetAndNumber;
	}
	public void setStreetAndNumber(String streetAndNumber) {
		this.streetAndNumber = streetAndNumber;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPostCode() {
		return postCode;
	}
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	@Override
	public String toString() {
		return "Address [streetAndNumber=" + streetAndNumber + ", city=" + city + ", province=" + province
				+ ", country=" + country + ", postCode=" + postCode + "]";
	}

}
