package com.breezy.oauth.model;

import org.json.JSONObject;

public class Address {
	
	private String line1;
	private String line2;
	private String city;
	private String state;
	private String zip;
	private String country;
	private long latitude;
	private long longitude;
	
	public static final String LINE_1_STRING = "address_line1";
	public static final String LINE_2_STRING = "address_line2";
	public static final String CITY_STRING = "address_city";
	public static final String STATE_STRING = "address_state";
	public static final String ZIP_STRING = "address_zip";
	public static final String COUNTRY_STRING = "address_country";
	public static final String LATITUDE_STRING = "address_latitude";
	public static final String LONGITUDE_STRING = "address_longitude";
	
	/**
	 * Parses Address Object from given JSON Object.
	 * 
	 * @param addressJSON
	 * @return
	 */
	public static Address parseString(JSONObject addressJSON) {
		
		String line1 = addressJSON.optString(Address.LINE_1_STRING);
		String line2 = addressJSON.optString(Address.LINE_2_STRING);
		String city = addressJSON.optString(Address.CITY_STRING);
		String state = addressJSON.optString(Address.STATE_STRING);
		String zip = addressJSON.optString(Address.ZIP_STRING);
		String country = addressJSON.optString(Address.COUNTRY_STRING);
		long latitude = addressJSON.optLong(Address.LATITUDE_STRING);
		long longitude = addressJSON.optLong(Address.LONGITUDE_STRING);
		
		return new Address(line1, line2, city, state, zip, country, latitude, longitude);
	}
	
	public Address(String line1, String line2, String city, String state, 
			String zip, String country, long latitude, long longitude) {
		
		this.line1 = line1;
		this.line2 = line2;
		this.city = city;
		this.state = state;
		this.zip = zip;
		this.country = country;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getLine1() {
		return line1;
	}
	
	public void setLine1(String line1) {
		this.line1 = line1;
	}
	
	public String getLine2() {
		return line2;
	}
	
	public void setLine2(String line2) {
		this.line2 = line2;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getZip() {
		return zip;
	}
	
	public void setZip(String zip) {
		this.zip = zip;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public long getLatitude() {
		return latitude;
	}
	
	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}
	
	public long getLongitude() {
		return longitude;
	}
	
	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}
}
