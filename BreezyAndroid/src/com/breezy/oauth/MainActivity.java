package com.breezy.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	
	/** UI Elements */
	private EditText userName, password;
	private Button loginRegisterButton;
	private Button toggleLoginRegisterButton;
	
	private ProgressBar progressBar;
	private RelativeLayout mainViewHolder;
	
	/** API Class holds URL and API Parameters */
	final BreezyApi api = new BreezyApi();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* initialize UI Elements */
		initUI();
		
		/* Click Listeners */
		setClickListeners();
	}
	
	/**
	 * Set Login Button's Click Action.
	 */
	public void setClickListeners() {
		loginRegisterButton.setOnClickListener(this);
	}
	
	/**
	 * Initializes UI Elements.
	 */
	public void initUI() {
		
		userName = (EditText) findViewById(R.id.userName);
		password = (EditText) findViewById(R.id.password);
		loginRegisterButton = (Button) findViewById(R.id.loginRegisterButtonSendRequest);
		toggleLoginRegisterButton = (Button) findViewById(R.id.loginRegisterToggleButton);
		
		progressBar = (ProgressBar) findViewById(R.id.loginProgressBar);
		mainViewHolder = (RelativeLayout) findViewById(R.id.mainViewHolder);
		
		/* Set Constant Texts here */
		userName.setText(Constants.USER_EMAIL);
		password.setText(Constants.PASSWORD);
	}
	
	private void fadeOut(View view) {
		
		AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
		anim.setDuration(1000);
		anim.setRepeatCount(0);
		anim.setFillAfter(true);
		view.startAnimation(anim);
	}
	
	private void fadeIn(View view) {
		
		AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(1000);
		anim.setRepeatCount(0);
		anim.setFillAfter(true);
		view.startAnimation(anim);
	}
	
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		/* Login Button is Clicked */
		if (id == loginRegisterButton.getId()) {
			
			// TODO
			// check if the action is Login or Register.
			
			/* Login */
			authoriseUser();
			
		} else if(id == toggleLoginRegisterButton.getId()) {
			
			// TODO
			// Change the Texts of the Buttons, from Login to Register and from register to Login.
			// Use a boolean variable to indicate the Button click either it will register or login.
			
		}
	}
	
	/**
	 * Uses OAuth 1.0 to get Access Token within 3 steps.
	 * 
	 * Retrieve;
	 * 1- Request Token
	 * 2- Verifier
	 * 3- Access Token.
	 */
	public void authoriseUser() {
		
		/* Show Progress Bar */
		progressBar.setVisibility(View.VISIBLE);
		fadeIn(progressBar);
		
		fadeOut(mainViewHolder);
		mainViewHolder.setVisibility(View.GONE);
		
		final Handler handler = new Handler();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				Looper.prepare();
				
				/* Trying to get the Request Token First */
				final OAuthService service = new ServiceBuilder()
				.provider(api)
				.apiKey(Constants.CLIENT_KEY)
				.apiSecret(Constants.CLIENT_SECRET)
				.callback("oob")
				.build();
				
				/* Step 1 */
				/* Get Request Token */
				final Token requestToken = service.getRequestToken();
				Log.i("Okan", "(1)Request Token : " + requestToken.getToken());
				Log.i("Okan", "(2)Request Token Secret : " + requestToken.getSecret());
				
				String authUrl = service.getAuthorizationUrl(requestToken);
				Log.i("Okan", "OAuth URL : " + authUrl);
				
				/* Step 2 */
				/* Get Verification Code, make custom call to API with request token and user credentials */
				final String verifierValue = getOAuthVerifier(requestToken.getToken());
				Log.i("Okan", "Verifier String : " + verifierValue);
				
				if (verifierValue.equalsIgnoreCase("")) {
					
					/* Error, No Verifier Granted */
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							/* Show Progress Bar */
							progressBar.setVisibility(View.GONE);
							mainViewHolder.setVisibility(View.VISIBLE);
							
							fadeIn(mainViewHolder);
							fadeOut(progressBar);
							
							Toast.makeText(MainActivity.this, "Verifier Couldn't Granted", Toast.LENGTH_SHORT).show();
						}
					});
					
				} else {
					
					/* Success, Verifier Granted */
					
					/* Step 3 */
					/* Get Access token with Request Token and Verification Code */
					
					Verifier verifier = new Verifier(verifierValue);
					Token accessToken = service.getAccessToken(requestToken, verifier);
					Log.i("Okan", "Access Token : " + accessToken.toString());
					
					/* Save Access Token */
					saveAccessToken(accessToken);
					
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							/* Go To Document Activity */
							DocumentActivity.startDocumentActivity(MainActivity.this);
						}
					});
				}
				
				Looper.loop();
			}
		}).start();
	}
	
	/**
	 * Sends POST Request to API with Request Token and User Credentials.
	 * 
	 * @param requestToken	Request Token retrieved at Step 1.
	 * @return				Returns oauth_verifier String from API Response.
	 */
	public String getOAuthVerifier(String requestToken) {
		
		/* URL with Request Token */
		String url = BreezyApi.API_URL + "/oauth/authorize?oauth_token=" + requestToken;
		String oauthVerifier = "";
		
		/* Create JSON Object with User Credentials */
		JSONObject postMessage = new JSONObject();
		try {
			postMessage.put("password", Constants.PASSWORD);
			postMessage.put("email", Constants.USER_EMAIL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		/* HTTP Client and it's parameters */
		int TIMEOUT_MILLISEC = 20000;  // = 20 seconds
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
		HttpClient client = new DefaultHttpClient(httpParams);
		
		HttpPost request = new HttpPost(url);
		
		/* Entity of Request */
		StringEntity se;
		try {
			
			String body = postMessage.toString();
			se = new StringEntity(body);
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            request.setEntity(se);
            
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpResponse response = null;
		
		/* Execute Request */
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*Checking response */
        if(response != null){
            try {
            	
            	BufferedReader inputStream = null;
				
				inputStream = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder stringBuilder = new StringBuilder();
				String line = null;
				
				while ((line = inputStream.readLine()) != null) {
					stringBuilder.append(line);
				}
				
				/* LOG full Response */
				Log.i("Okan", "Full Response : " + stringBuilder.toString());
				
				/* Get Verifier */
				oauthVerifier = parseOAuthVerifier(stringBuilder.toString());
				
				Log.i("Okan", "Verifier : " + oauthVerifier);
				
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return oauthVerifier;
	}
	
	/**
	 * Parses Access Token and it's Secret.
	 * Saves them to the Shared Preferences.
	 * 
	 * @param accessToken	Access Token Received from 3rd step in OAuth.
	 */
	private void saveAccessToken(Token accessToken) {
		
		String token = accessToken.getToken();
		String tokenSecret = accessToken.getSecret();
		
		SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
	    editor.putString(BreezyApi.ACCESS_TOKEN_STRING, token);
	    editor.putString(BreezyApi.ACCESS_TOKEN_SECRET_STRING, tokenSecret);
	    
		// Commit the edits!
		editor.commit();
	}
	
	/**
	 * Parses API Response which has oauth_token and oauth_verifier Strings with Regex.
	 * 
	 * @param input	API Response
	 * @return		Returns oauth_verifier value,
	 */
	public String parseOAuthVerifier(String input) {
		
		String oauth_verifier = "";
		
		Pattern p = Pattern.compile("(?:&([^=]*)=([^&]*))");
		Matcher m = p.matcher(input);
		while (m.find()) {
			
			if (m.group(1).equals("oauth_verifier")) {
				oauth_verifier = m.group(2);
			}
		}
		
		return oauth_verifier;
	}
}
