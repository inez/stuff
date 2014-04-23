package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.HomeFeedAdapter;
import com.stuff.stuffapp.models.Item;

public class MessagesFragment extends Fragment {

	private static String TAG = "MessagesFragment";
	
	private View view;

	public static MessagesFragment newInstance() {
		MessagesFragment fragment = new MessagesFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_messages, container, false);

		return view;
    }

}
