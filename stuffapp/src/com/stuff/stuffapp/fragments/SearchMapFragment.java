package com.stuff.stuffapp.fragments;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.SupportMapFragment;
import com.stuff.stuffapp.R;

public class SearchMapFragment extends Fragment {

	private static String TAG = "SearchMapFragment";

	// BEGIN BUG FIX for fragment within fragment
	// http://stackoverflow.com/questions/14929907/causing-a-java-illegalstateexception-error-no-activity-only-when-navigating-to
	// https://code.google.com/p/android/issues/detail?id=42601
	private static final Field sChildFragmentManagerField;
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "Error getting mChildFragmentManager field", e);
		}
		sChildFragmentManagerField = f;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} catch (Exception e) {
				Log.e(TAG, "Error setting mChildFragmentManager field", e);
			}
		}
	}

	// END BUG FIX
	
	private View view;

	public static SearchMapFragment newInstance() {
		SearchMapFragment fragment = new SearchMapFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");

		view = inflater.inflate(R.layout.fragment_map_search, container, false);
		
		FragmentManager fm = getChildFragmentManager();
		SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.flMap);
		if (fragment == null) {
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.flMap, fragment).commit();
		}

		return view;
	}
	
}
