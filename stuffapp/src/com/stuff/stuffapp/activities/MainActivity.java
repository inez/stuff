package com.stuff.stuffapp.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.fragments.MainFragment;
import com.stuff.stuffapp.fragments.ProfileFragment;
import com.stuff.stuffapp.helpers.Ids;

public class MainActivity extends FragmentActivity {

	private SparseArray<Fragment> fragments;
	
	private int currentFragmentId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragments = new SparseArray<Fragment>();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onMenuItemClick(View view) {
		int fragmentId = 0;

		switch ( view.getId() ) {
			case R.id.iv_home:
				fragmentId = Ids.HOME;
				break;
			case R.id.iv_search:
				fragmentId = Ids.SEARCH;
				break;
			case R.id.iv_add:
				fragmentId = Ids.ADD;
				break;
			case R.id.iv_message:
				fragmentId = Ids.MESSAGE;
				break;
			case R.id.iv_profile:
				fragmentId = Ids.PROFILE;
				break;
		}
		
		if ( fragmentId == currentFragmentId ) {
			return;
		}

		if(fragmentId != 0) {
			displayFragment(fragmentId);
			currentFragmentId = fragmentId;
		}
	}
	
	private void displayFragment(int fragmentId) {
		Fragment fragment = fragments.get(fragmentId);

		if(fragment==null){
			switch(fragmentId) {
				case Ids.PROFILE:
					fragment = ProfileFragment.newInstance();
					break;
				default:
					fragment = MainFragment.newInstance(String.valueOf(fragmentId));
					break;
			}
			fragments.append(fragmentId, fragment);
		}

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_container, fragment);
		ft.addToBackStack(null);
		ft.commit();
	}

}
