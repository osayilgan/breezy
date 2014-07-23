package com.breezy.oauth.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadFileResponse {
	
	private String publicKeyModulus;
	private String publicKeyExponent;
	private String uploadURL;
	private int documentId;
	private String releaseCode;
	
	public static final String PUBLIC_KEY_MODULUS_STRING = "public_key_modulus";
	public static final String PUBLIC_KEY_EXPONENT_STRING = "public_key_exponent";
	public static final String UPLOAD_URL_STRING = "upload_url";
	public static final String DOCUMENT_ID_STRING = "document_id";
	public static final String RELEASE_CODE_STRING = "release_code";
	
	/**
	 * Parses Upload File Response Object from given Response Body String.
	 * 
	 * @param responseBody
	 * @return
	 * @throws JSONException
	 */
	public static UploadFileResponse parseResponse(String responseBody) throws JSONException {
		
		JSONObject responseJSON = new JSONObject(responseBody);
		
		String publicKeyModulus = responseJSON.optString(PUBLIC_KEY_MODULUS_STRING);
		String publicKeyExponent = responseJSON.optString(PUBLIC_KEY_EXPONENT_STRING);
		String uploadURL = responseJSON.optString(UPLOAD_URL_STRING);
		int documentId = responseJSON.optInt(DOCUMENT_ID_STRING);
		String releaseCode = responseJSON.optString(RELEASE_CODE_STRING);
		
		return new UploadFileResponse(publicKeyModulus, publicKeyExponent, uploadURL, documentId, releaseCode);
	}
	
	public UploadFileResponse(String publicKeyModulus, String publicKeyExponent, 
			String uploadURL, int documentId, String releaseCode) {
		
		this.publicKeyModulus = publicKeyModulus;
		this.publicKeyExponent = publicKeyExponent;
		this.uploadURL = uploadURL;
		this.documentId = documentId;
		this.releaseCode = releaseCode;
	}

	public String getPublicKeyModules() {
		return publicKeyModulus;
	}
	
	public void setPublicKeyModules(String publicKeyModules) {
		this.publicKeyModulus = publicKeyModules;
	}
	
	public String getPublicKeyExponent() {
		return publicKeyExponent;
	}
	
	public void setPublicKeyExponent(String publicKeyExponent) {
		this.publicKeyExponent = publicKeyExponent;
	}
	
	public String getUploadURL() {
		return uploadURL;
	}
	
	public void setUploadURL(String uploadURL) {
		this.uploadURL = uploadURL;
	}
	
	public int getDocumentId() {
		return documentId;
	}
	
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	
	public String getReleaseCode() {
		return releaseCode;
	}
	
	public void setReleaseCode(String releaseCode) {
		this.releaseCode = releaseCode;
	}
}
