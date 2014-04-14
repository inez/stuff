package com.stuff.stuffapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	public static String LOG_TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//ImageView iv_home = (ImageView) findViewById(R.id.iv_home);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onMenuItemClick(View view) {
		switch ( view.getId() ) {
			case R.id.iv_home:
				Log.d(LOG_TAG, "Home clicked");
				break;
			case R.id.iv_search:
				Log.d(LOG_TAG, "Search clicked");
				break;
			case R.id.iv_add:
				Log.d(LOG_TAG, "Add clicked");
				break;
			case R.id.iv_message:
				Log.d(LOG_TAG, "Message clicked");
				break;
			case R.id.iv_profile:
				Log.d(LOG_TAG, "Profile clicked");
				break;
		}
		
	}

}
