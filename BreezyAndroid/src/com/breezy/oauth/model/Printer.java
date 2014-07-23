package com.breezy.oauth.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Printer {
	
	private int endPointId;
	private String displayName;
	private boolean requiresReleaseCode;
	private int locationId;
	private int distanceInMeters;
	private int termsOfServiceId;
	
	public static final String END_POINT_ID_STRING = "endpoint_id";
	public static final String DISPLAY_NAME_STRING = "display_name";
	public static final String REQUIRES_RELEASE_CODE_STRING = "requires_release_code";
	public static final String LOCATION_ID_STRING = "location_id";
	public static final String DISTANCE_IN_METERS_STRING = "distance_meters";
	public static final String TERMS_OF_SERVICE_STRING = "terms_of_service_id";
	
	public static final String PRINTER_JSON_ARRAY_STRING = "printers";
	
	/**
	 * Parses Printer Object from given JSON Object.
	 * 
	 * @param printerJson
	 * @return
	 */
	public static Printer parsePrinter(JSONObject printerJson) {
		
		int endPointId = printerJson.optInt(Printer.END_POINT_ID_STRING);
		String displayName = printerJson.optString(Printer.DISPLAY_NAME_STRING);
		boolean requiresReleaseCode = printerJson.optBoolean(Printer.REQUIRES_RELEASE_CODE_STRING);
		int locationId = printerJson.optInt(Printer.LOCATION_ID_STRING);
		int distanceInMeters = printerJson.optInt(Printer.DISTANCE_IN_METERS_STRING);
		int termsOfServiceId = printerJson.optInt(Printer.TERMS_OF_SERVICE_STRING);
		
		return new Printer(endPointId, displayName, requiresReleaseCode, locationId, distanceInMeters, termsOfServiceId);
	}
	
	/**
	 * Parses response body of the printer list and retrieves the List of Printer Objects.
	 * 
	 * @param responseBody
	 * @return
	 * @throws JSONException
	 */
	public static ArrayList<Printer> parsePrinterList(String responseBody) throws JSONException {
		
		ArrayList<Printer> printerList = new ArrayList<Printer>();
		
		JSONObject mainJSONObject = new JSONObject(responseBody);
		
		JSONArray printerJSONArray = mainJSONObject.optJSONArray(PRINTER_JSON_ARRAY_STRING);
		
		for (int i = 0; i < printerJSONArray.length(); i++) {
			
			JSONObject printerJSONObject = printerJSONArray.getJSONObject(i);
			printerList.add(Printer.parsePrinter(printerJSONObject));
		}
		
		return printerList;
	}
	
	public Printer(int endPointId, String displayName, boolean requiresReleaseCode, 
			int locationId, int distanceInMeters, int termsOfServiceId) {
		
		this.endPointId = endPointId;
		this.displayName = displayName;
		this.requiresReleaseCode = requiresReleaseCode;
		this.locationId = locationId;
		this.distanceInMeters = distanceInMeters;
		this.termsOfServiceId = termsOfServiceId;
	}

	public int getEndPointId() {
		return endPointId;
	}
	
	public void setEndPointId(int endPointId) {
		this.endPointId = endPointId;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public boolean isRequiresReleaseCode() {
		return requiresReleaseCode;
	}
	
	public void setRequiresReleaseCode(boolean requiresReleaseCode) {
		this.requiresReleaseCode = requiresReleaseCode;
	}
	
	public int getLocationId() {
		return locationId;
	}
	
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	
	public int getDistanceInMeters() {
		return distanceInMeters;
	}
	
	public void setDistanceInMeters(int distanceInMeters) {
		this.distanceInMeters = distanceInMeters;
	}
	
	public int getTermsOfServiceId() {
		return termsOfServiceId;
	}
	
	public void setTermsOfServiceId(int termsOfServiceId) {
		this.termsOfServiceId = termsOfServiceId;
	}
}
