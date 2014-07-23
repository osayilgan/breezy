package com.breezy.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DocumentActivity extends Activity implements OnClickListener {
	
	/** UI Elements */
	private Button document, image, webSite, selectPrinter;
	
	/** Constant Integer used to filter onActivityResult state */
	private static final int READ_REQUEST_CODE = 42;
	
	/** Uri of the file to be printed */
	private Uri fileUri;
	
	public static void startDocumentActivity(Context context) {
		Intent intent = new Intent(context, DocumentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent); 
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.document_chooser);
		
		initUI();
		setClickListeners();
	}
	
	/**
	 * Initializes UI elements.
	 */
	private void initUI() {
		
		document = (Button) findViewById(R.id.chooseDocument);
		image = (Button) findViewById(R.id.chooseImage);
		webSite = (Button) findViewById(R.id.chooseWebsite);
		selectPrinter = (Button) findViewById(R.id.choosePrinter);
	}
	
	/**
	 * Sets click listeners for the Document, Image and WebSite Buttons.
	 */
	private void setClickListeners() {
		
		document.setOnClickListener(this);
		image.setOnClickListener(this);
		webSite.setOnClickListener(this);
		selectPrinter.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		/* Login Button is Clicked */
		if (id == document.getId()) {
			
			// TODO
			// Use Document Provider with Intent ACTION_OPEN_DOCUMENT
			openDocument(false);
			
		} else if(id == image.getId()) {
			
			// TODO
			// Use Document Provider with Intent ACTION_OPEN_DOCUMENT
			// Add filter as "image" MIME type.
			openDocument(true);
			
		} else if(id == webSite.getId()) {
			
			// TODO
			// Open up a WebView
			
		} else if(id == selectPrinter.getId()) {
			
			/* If a file is chosen to be printed */
			if (fileUri !=null) {
				
				PrinterActivity.startPrinterActivity(this, fileUri);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// TODO
		// Handle chosen document.
		
		if (resultCode == RESULT_OK) {
			
			if (requestCode == READ_REQUEST_CODE) {
				
				fileUri = data.getData();
			}
			
		}
	}
	
	/**
	 * 
	 * Triggers the intent to list the files.
	 * 
	 */
	private void openDocument(boolean isImage) {
		
		// ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
	    // browser.
	    Intent intent;
	    
	    if (android.os.Build.VERSION.SDK_INT < 19) {
	    	intent = new Intent(Intent.ACTION_GET_CONTENT);
		} else {
			intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		}
	    
	    // Filter to only show results that can be "opened", such as a
	    // file (as opposed to a list of contacts or timezones)
	    intent.addCategory(Intent.CATEGORY_OPENABLE);
	    
	    // Filter to show only images, using the image MIME data type.
	    // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
	    // To search for all documents available via installed storage providers,
	    // it would be "*/*".
	    if (isImage) {
	    	intent.setType("image/*");
		} else {
			intent.setType("*/*");
		}
	    
	    if (intent.resolveActivity(getPackageManager()) != null) {
	    	startActivityForResult(intent, READ_REQUEST_CODE);
	    } else {
	    	Toast.makeText(this, "No Activity Found", Toast.LENGTH_SHORT).show();
	    }
	}
}
