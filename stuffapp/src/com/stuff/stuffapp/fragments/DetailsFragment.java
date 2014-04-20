package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stuff.stuffapp.R;

public class DetailsFragment extends Fragment {

	private static String TAG = "DetailsFragment";

	private View view;

	public static DetailsFragment newInstance() {
		DetailsFragment fragment = new DetailsFragment();
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_details, container, false);
        return view;
	}

}
