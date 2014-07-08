package com.breezy.oauth;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

public class BreezyApi extends DefaultApi10a {
	
	private static final String AUTHORIZE_URL = "http://breezy-api-test.azurewebsites.net/oauth/authorize";
	private static final String REQUEST_TOKEN_RESOURCE = "breezy-api-test.azurewebsites.net/oauth/request_token";
	private static final String ACCESS_TOKEN_RESOURCE = "breezy-api-test.azurewebsites.net/oauth/access_token";
	
	@Override
	public String getAccessTokenEndpoint() {
		return "http://" + ACCESS_TOKEN_RESOURCE;
	}
	
	@Override
	public String getRequestTokenEndpoint() {
		return "http://" + REQUEST_TOKEN_RESOURCE;
	}
	
	@Override
	public String getAuthorizationUrl(Token requestToken) {
		return String.format(AUTHORIZE_URL, requestToken.getToken());
	}
	
	/**
	 * Breezy authorization endpoint for OAuth.
	 */
	public static class Authenticate extends BreezyApi {
		
		private static final String AUTHENTICATE_URL = "http://breezy-api-test.azurewebsites.net/oauth/authorize?oauth_token=";
		
		@Override
		public String getAuthorizationUrl(Token requestToken) {
			return String.format(AUTHENTICATE_URL, requestToken.getToken());
		}
	}
}
