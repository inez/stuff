package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stuff.stuffapp.R;

public class AddFragment extends Fragment {

	private static String TAG = "AddFragment";
	
	private View view;

	public static AddFragment newInstance() {
		AddFragment fragment = new AddFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_add, container, false);
        
		return view;
    }

	/*
	To display keyboard
	@Override
	public void onResume() {
		super.onResume();
        EditText someEditText = (EditText) view.findViewById(R.id.textView2);
        someEditText.requestFocus(); 
        InputMethodManager mgr =      (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(someEditText, InputMethodManager.SHOW_IMPLICIT);
	}
	*/

}
