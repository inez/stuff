package com.stuff.stuffapp;

import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends FragmentActivity {
	
	public final static int HOME = 1;
	public final static int SEARCH = 2;
	public final static int ADD = 3;
	public final static int MESSAGE = 4;
	public final static int PROFILE = 5;

	private static String LOG_TAG = "MainActivity";
	
	private HashMap<Integer, Fragment> fragments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		fragments = new HashMap<Integer, Fragment>();
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
				displayFragment(HOME);
				break;
			case R.id.iv_search:
				Log.d(LOG_TAG, "Search clicked");
				displayFragment(SEARCH);
				break;
			case R.id.iv_add:
				Log.d(LOG_TAG, "Add clicked");
				displayFragment(ADD);
				break;
			case R.id.iv_message:
				Log.d(LOG_TAG, "Message clicked");
				displayFragment(MESSAGE);
				break;
			case R.id.iv_profile:
				Log.d(LOG_TAG, "Profile clicked");
				displayFragment(PROFILE);
				break;
		}
		
	}
	
	private void displayFragment(int fragmentId) {
		Fragment fragment;
		if(!fragments.containsKey(fragmentId)) {
			switch(fragmentId) {
				case PROFILE:
					fragments.put(fragmentId, ProfileFragment.newInstance());
					break;
				default:
					fragments.put(fragmentId, MainFragment.newInstance(String.valueOf(fragmentId)));
					break;
			}
		}
		fragment = fragments.get(fragmentId);
		
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_container, fragment);
		ft.addToBackStack(String.valueOf(fragmentId));
		ft.commit();
		
	}

}
