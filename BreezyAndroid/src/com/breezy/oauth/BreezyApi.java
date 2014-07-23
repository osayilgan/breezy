package com.breezy.oauth;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class BreezyApi extends DefaultApi10a {
	
	private static final String AUTHORIZE_URL = "oauth/authorize";
	private static final String REQUEST_TOKEN_RESOURCE = "oauth/request_token";
	private static final String ACCESS_TOKEN_RESOURCE = "oauth/access_token";
	
	public static final String API_URL = "http://breezy-api-test.azurewebsites.net/";
//	public static final String API_URL = "http://office.breezy.com:9312/";
	
	public static final String ACCESS_TOKEN_STRING = "ACCESS_TOKEN";
	public static final String ACCESS_TOKEN_SECRET_STRING = "ACCESS_TOKEN_SECRET";
	
	@Override
	public String getAccessTokenEndpoint() {
		return API_URL + ACCESS_TOKEN_RESOURCE;
	}
	
	@Override
	public String getRequestTokenEndpoint() {
		return API_URL + REQUEST_TOKEN_RESOURCE;
	}
	
	@Override
	public String getAuthorizationUrl(Token requestToken) {
		return String.format(API_URL + AUTHORIZE_URL, requestToken.getToken());
	}
	
	/**
	 * Breezy authorization endpoint for OAuth.
	 */
	public static class Authenticate extends BreezyApi {
		
		private static final String AUTHENTICATE_URL = API_URL + "oauth/authorize?oauth_token=";
		
		@Override
		public String getAuthorizationUrl(Token requestToken) {
			return String.format(AUTHENTICATE_URL, requestToken.getToken());
		}
	}
}
