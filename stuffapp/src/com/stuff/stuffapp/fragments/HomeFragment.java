package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.HomeFeedAdapter;

public class HomeFragment extends Fragment {

	private static String TAG = "HomeFragment";
	
	private View view;

	public static HomeFragment newInstance() {
		HomeFragment fragment = new HomeFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);

        HomeFeedAdapter mainAdapter = new HomeFeedAdapter(getActivity());

        ListView lv_home = (ListView) view.findViewById(R.id.lv_home);
        lv_home.setAdapter(mainAdapter);

		return view;
    }

}
