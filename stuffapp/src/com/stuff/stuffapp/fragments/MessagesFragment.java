package com.stuff.stuffapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Conversation;

public class MessagesFragment extends Fragment {

	private static String TAG = "MessagesFragment";
	
	private View view;
	
	private ParseQueryAdapter<Conversation> adapter;

	public static MessagesFragment newInstance() {
		MessagesFragment fragment = new MessagesFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        if(adapter==null) {
        	createAdapter();
        }
        return view;
    }
	
	private void createAdapter() {
		adapter = new ParseQueryAdapter<Conversation>(getActivity(), new ParseQueryAdapter.QueryFactory<Conversation>() {
        	public ParseQuery<Conversation> create() {
                ParseUser user = ParseUser.getCurrentUser();
                
                ParseQuery<Conversation> queryUserOne = new ParseQuery<Conversation>(Conversation.class);
                queryUserOne.whereEqualTo(Conversation.ATTR_USER_ONE, user);

                ParseQuery<Conversation> queryUserTwo = new ParseQuery<Conversation>(Conversation.class);
                queryUserTwo.whereEqualTo(Conversation.ATTR_USER_TWO, user);
                
                List<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();
                queries.add(queryUserOne);
                queries.add(queryUserTwo);
                
                return ParseQuery.or(queries);
        	}
        });
	}

}
