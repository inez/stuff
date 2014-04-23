package com.stuff.stuffapp.fragments;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.MessageListAdapter;
import com.stuff.stuffapp.fragments.HomeFragment.OnItemClickedListener;
import com.stuff.stuffapp.models.Item;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MessageListFragment extends Fragment {
	
	private static final String TAG = "MessageListFragment";
	
	private  MessageListAdapter messageListAdapter;

	private View view;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_home, container, false);
        
        if(savedInstanceState == null) {
        	

        	if ( messageListAdapter == null ) {
        		messageListAdapter = new MessageListAdapter(getActivity());
        	}

        	
	        
        
        }

		return view;
    }

}