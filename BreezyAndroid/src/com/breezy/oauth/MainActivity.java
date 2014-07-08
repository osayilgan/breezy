package com.breezy.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {
	
	/** UI Elements */
	private EditText userName, password;
	private Button loginButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		/* initialize UI Elements */
		initUI();
		
		/* Click Listeners */
		setClickListeners();
	}
	
	/**
	 * Set Login Button's Click Action.
	 */
	public void setClickListeners() {
		loginButton.setOnClickListener(this);
	}
	
	/**
	 * Initializes UI Elements.
	 */
	public void initUI() {
		
		userName = (EditText) findViewById(R.id.userName);
		password = (EditText) findViewById(R.id.password);
		loginButton = (Button) findViewById(R.id.loginButton);
		
		/* Set Constant Texts here */
		userName.setText(Constants.USER_EMAIL);
		password.setText(Constants.PASSWORD);
	}
	
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		/* Login Button is Clicked */
		if (id == loginButton.getId()) {
			
			/* Login */
			testOAuth();
		}
	}
	
	public void testOAuth() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Looper.prepare();
				
				/* Trying to get the Request Token First */
				OAuthService service = new ServiceBuilder()
				.provider(BreezyApi.class)
				.apiKey(Constants.CLIENT_KEY)
				.apiSecret(Constants.CLIENT_SECRET)
				.callback("oob")
				.build();
				
				Scanner in = new Scanner(System.in);
				
				Token requestToken = service.getRequestToken();
				Log.i("Okan", "Request Token : " + requestToken.toString());
				
				String authUrl = service.getAuthorizationUrl(requestToken);
				Log.i("Okan", "OAuth URL : " + authUrl);
				
				/* Fails here, NoSuchElementException */
				String verifierString = in.nextLine();
				Log.i("Okan", "Verifier : " + verifierString);
				
				Verifier verifier = new Verifier(verifierString);
				Token accessToken = service.getAccessToken(requestToken, verifier);
				Log.i("Okan", "Access Token : " + accessToken.toString());
				
				Looper.loop();
				
			}
		}).start();
	}
	
	public void authenticateUser() {
		
		final String url = "http://breezy-api-test.azurewebsites.net/oauth/request_token";
		
		// Insert Loading indicator inside the button.
		
		// Authenticate inside a Thread.
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Looper.prepare();
				
				JSONObject postMessage = new JSONObject();
				try {
					postMessage.put("password", Constants.PASSWORD);
					postMessage.put("client_id", 1);
					postMessage.put("cluster_id", 1);
					postMessage.put("email", Constants.USER_EMAIL);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
				HttpClient client = new DefaultHttpClient(httpParams);
				
				HttpPost request = new HttpPost(url);
				
				StringEntity se;
				try {
					
					se = new StringEntity(postMessage.toString());
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
	                request.setEntity(se);
	                
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				HttpResponse response = null;
				
				try {
					response = client.execute(request);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				/*Checking response */
                if(response!=null){
                    try {
                    	
                    	BufferedReader inputStream = null;
						
						inputStream = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
						StringBuilder stringBuilder = new StringBuilder();
						String line = null;
						
						while ((line = inputStream.readLine()) != null) {
							stringBuilder.append(line + "\n");
						}
						
						Log.i("Okan", "Response : " + stringBuilder.toString());
						
						
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
				
                Looper.loop();
			}
		}).start();
	}
}
