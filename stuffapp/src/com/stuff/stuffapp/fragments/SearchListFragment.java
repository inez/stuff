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

public class SearchListFragment extends Fragment {

	private static String TAG = "SearchListFragment";

	private View view;

	public static SearchListFragment newInstance() {
		SearchListFragment fragment = new SearchListFragment();
		return fragment;
	}

	private HomeFeedAdapter mainAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");

		view = inflater.inflate(R.layout.fragment_home, container, false);

		if (savedInstanceState == null) {

			if (mainAdapter == null) {
				mainAdapter = new HomeFeedAdapter(getActivity());
			}

			ListView lvHome = (ListView) view.findViewById(R.id.lvHome);

			lvHome.setAdapter(mainAdapter);

		}

		return view;
	}

}
