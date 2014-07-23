package com.breezy.oauth.model;

import org.json.JSONObject;

public class Location {
	
	private int locationId;
	private String name;
	private String phone;
	private String availability;
	private Address address;

	public static final String LOCATION_ID_STRING = "location_id";
	public static final String NAME_STRING = "name";
	public static final String PHONE_STRING = "phone";
	public static final String AVAILABILITY_STRING = "availability";
	
	/**
	 * Parses Location Object from given JSON Object.
	 * 
	 * @param locationJSON
	 * @return
	 */
	public static Location parseLocation(JSONObject locationJSON) {
		
		int locationId = locationJSON.optInt(Location.LOCATION_ID_STRING);
		String name = locationJSON.optString(Location.NAME_STRING);
		String phone = locationJSON.optString(Location.PHONE_STRING);
		String availability = locationJSON.optString(Location.AVAILABILITY_STRING);
		
		Address address = Address.parseString(locationJSON);
		
		return new Location(locationId, name, phone, availability, address);
	}
	
	public Location(int locationId, String name, String phone, String availability, Address address) {
		
		this.locationId = locationId;
		this.name = name;
		this.phone = phone;
		this.availability = availability;
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
	
	public void setAddress(Address address) {
		this.address = address;
	}
	
	public int getLocationId() {
		return locationId;
	}
	
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getAvailability() {
		return availability;
	}
	
	public void setAvailability(String availability) {
		this.availability = availability;
	}
}
