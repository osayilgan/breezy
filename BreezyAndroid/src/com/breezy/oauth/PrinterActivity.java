package com.breezy.oauth;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.breezy.oauth.PrinterAdapter.PrinterClickListener;
import com.breezy.oauth.model.Printer;
import com.breezy.oauth.model.UploadFileResponse;

public class PrinterActivity extends Activity implements OnClickListener {
	
	private ListView printerListView;
	private Button printButton;
	private ProgressBar progressBar;
	
	private PrinterAdapter printerAdapter;
	private ArrayList<Printer> printerList;
	
	private Uri fileUri;
	public static final String FILE_URI_STRING = "fileUri";
	
	private Context context;
	private int selectedPrinterPosition = -1;
	
	private OAuthService service;
	private Token accessToken;
	
	public static void startPrinterActivity(Context context, Uri fileUri) {
		Intent intent = new Intent(context, PrinterActivity.class);
		intent.putExtra(FILE_URI_STRING, fileUri);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent); 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.printer_layout);
		
		/* intialize UI */
		initUI();
		
		/* Set Click Listeners */
		setClickListeners();
		
		/* Authenticate User */
		initOAuth();
		
		/* get the list of available printers */
		listPrinters();
		
		/* Parse File Uri */
		Bundle bundle = this.getIntent().getExtras();
		if (bundle != null && bundle.containsKey(FILE_URI_STRING)) {
			fileUri = bundle.getParcelable(FILE_URI_STRING);
		}
		
		this.context = this;
	}
	
	private void initOAuth() {
		
		/** API Class holds URL and API Parameters */
		final BreezyApi api = new BreezyApi();
		
		/* Get Access Token */
		SharedPreferences settings = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, 0);
	    String accessTokenString = settings.getString(BreezyApi.ACCESS_TOKEN_STRING, "");
	    String accessTokenStringSecret = settings.getString(BreezyApi.ACCESS_TOKEN_SECRET_STRING, "");
	    
	    accessToken = new Token(accessTokenString, accessTokenStringSecret);
		
		/* Trying to get the Request Token First */
		service = new ServiceBuilder()
		.provider(api)
		.apiKey(Constants.CLIENT_KEY)
		.apiSecret(Constants.CLIENT_SECRET)
		.callback("oob")
		.build();
	}
	
	private void initUI() {
		printerListView = (ListView) findViewById(R.id.printerListView);
		printButton = (Button) findViewById(R.id.printButton);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
	}
	
	private void setClickListeners() {
		printButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == printButton.getId()) {
			
			if (selectedPrinterPosition != -1) {
				
				sendPrintRequest();
				
			} else {
				Toast.makeText(this, "Please Select a Printer First", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	/**
	 * Retrieves the list of available printers and push them into the List View with
	 * Custom Adapter.
	 */
	private void listPrinters() {
		
		final Handler handler = new Handler();
		
		/* Show Progress Bar */
		progressBar.setVisibility(View.VISIBLE);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String url = BreezyApi.API_URL + "user/printers/1?radius_meters=100&faxNumber=0";
				OAuthRequest mRequest = new OAuthRequest(Verb.GET, url);
				service.signRequest(accessToken, mRequest);
				
				Response response = mRequest.send();
				String responseBody = response.getBody();
				int responseCode = response.getCode();
				
				Log.i("Okan", "Printer Response Code : " + responseCode);
				Log.i("Okan", "Printer Response Message : " + responseBody);
				
				try {
					
					/* Parse Printers */
					printerList = Printer.parsePrinterList(responseBody);
					
					/* Create Adapter in UI Thread */
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							
							/* Hide Progress Bar */
							progressBar.setVisibility(View.GONE);
							
							printerAdapter = new PrinterAdapter(PrinterActivity.this, printerList);
							printerListView.setAdapter(printerAdapter);
							printerAdapter.setPrinterClickListener(new PrinterClickListener() {
								
								@Override
								public void onPrinterClicked(int position) {
									selectedPrinterPosition = position;
								}
							});
						}
					});
					
				} catch (JSONException e) {
					Toast.makeText(PrinterActivity.this, "Error Parsing the JSON Data", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	/**
	 * Sends print request in 3 steps.
	 * 
	 * 1- retrieve an url to upload the file to be printed.
	 * 2- Upload file.
	 * 3- Update status of the process to the server.
	 * 
	 */
	private void sendPrintRequest() {
		
		final Handler handler = new Handler();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				/* Step - 1 */
				/* Get Upload URL */
				UploadFileResponse uploadUrlResponse = getUploadFileResponse();
				if (uploadUrlResponse == null) {
					return;
				}
				
				String uploadURL = uploadUrlResponse.getUploadURL();
				int documentId = uploadUrlResponse.getDocumentId();
				
				Log.i("Okan", "Upload URL : " + uploadURL);
				Log.i("Okan", "Document ID  " + documentId);
				
				/* Step - 2 */
				/* Upload File */
				int uploadedFileResponseCode = uploadFile(fileUri, uploadURL);
				Log.i("Okan", "Upload file Request responseCode : " + uploadedFileResponseCode);
				if (uploadedFileResponseCode == 0 || uploadedFileResponseCode != 200) {
					return;
				}
				
				/* Step - 3 */
				/* Send Process FeedBack to the Server */
				
			}
		}).start();
	}
	
	/**
	 * Request Document Id and Upload URL to api.
	 * 
	 * @return	Object which contains the whole response.
	 */
	private UploadFileResponse getUploadFileResponse() {
		
		String url = BreezyApi.API_URL + "document";
		
		OAuthRequest mRequest = new OAuthRequest(Verb.POST, url);
		mRequest.addBodyParameter("endpoint_id", "" + printerList.get(selectedPrinterPosition).getEndPointId());
		mRequest.addBodyParameter("fax_number", "0");
		
		Log.i("Okan", "Request Body Content : " + mRequest.getBodyContents());
		Log.i("Okan", "Request URL : " + mRequest.getCompleteUrl());
		
		service.signRequest(accessToken, mRequest);
		
		Response response = mRequest.send();
		String responseBody = response.getBody();
		int responseCode = response.getCode();
		
		UploadFileResponse object = null;
		
		Log.i("Okan", "Response Code for upload URL request : " + responseCode);
		Log.i("Okan", "Response Body : " + responseBody);
		
		if (responseCode == 200) {
			try {
				object = UploadFileResponse.parseResponse(responseBody);
			} catch (JSONException e) {
				object = null;
				e.printStackTrace();
			}
		}
		
		if (object == null) {
			Log.i("Okan", "Response Object is Null ");
		}
		
		return object;
	}
	
	/**
	 * Uploads given document to the given URL.
	 * 
	 * @param sourceFileUri		Uri of the selected document to be printed.
	 * @param uploadURL			URL to upload the file.
	 * @return					Response Code of the request.
	 */
	private int uploadFile(Uri sourceFileUri, String uploadURL) {
		
		int serverResponseCode = 0;
		ProgressDialog dialog = ProgressDialog.show(PrinterActivity.this, "", "Uploading file...", true);
		
		Uri fileName = sourceFileUri;
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
		File sourceFile = null;
		try {
			sourceFile = new File(new URI(sourceFileUri.toString()));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		if (sourceFile == null || !sourceFile.isFile()) {
			Log.e("uploadFile", "Source File Does not exist");
			return 0;
		}
		try {
			// open a URL connection to the Servlet
			FileInputStream fileInputStream = new FileInputStream(sourceFile);
			URL url = new URL(uploadURL);
			
			// Open a HTTP connection to the URL
			conn = (HttpURLConnection) url.openConnection();
			
			conn.setDoInput(true); // Allow Inputs
			conn.setDoOutput(true); // Allow Outputs
			conn.setUseCaches(false); // Don't use a Cached Copy
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("uploaded_file", fileName.getPath());
			dos = new DataOutputStream(conn.getOutputStream());
			
			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
			dos.writeBytes(lineEnd);
			
			bytesAvailable = fileInputStream.available();
			
			// create a buffer of maximum size
			bufferSize = Math.min(bytesAvailable, maxBufferSize);
			buffer = new byte[bufferSize];
			
			// read file and write it into form...
			bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			
			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}
			
			// send multipart form data necesssary after file data...
			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
			
			// Responses from the server (code and message)
			serverResponseCode = conn.getResponseCode();
			String serverResponseMessage = conn.getResponseMessage();
			
			Log.i("Okan", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
			
			if (serverResponseCode == 200) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(PrinterActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
			// close the streams //
			fileInputStream.close();
			dos.flush();
			dos.close();
			
		} catch (MalformedURLException ex) {
			dialog.dismiss();
			ex.printStackTrace();
			Toast.makeText(PrinterActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
			Log.e("Okan", "Upload file to server, error: " + ex.getMessage(), ex);
		} catch (Exception e) {
			dialog.dismiss();
			e.printStackTrace();
			Toast.makeText(PrinterActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
			Log.e("Okan", "Upload file to server Exception, Exception : " + e.getMessage(), e);
		}
		
		dialog.dismiss();
		return serverResponseCode;
	}
}
