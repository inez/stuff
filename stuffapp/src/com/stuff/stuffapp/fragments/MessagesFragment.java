package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.ConversationListAdapter;
import com.stuff.stuffapp.fragments.HomeFragment.OnItemClickedListener;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.Item;

public class MessagesFragment extends Fragment {

	private static String TAG = "MessagesFragment";
	
	private View view;
	
	private  ConversationListAdapter conversationListAdapter;
	
	private ListView lvConversations;

	public static MessagesFragment newInstance() {
		MessagesFragment fragment = new MessagesFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_messages, container, false);
        
        if(savedInstanceState == null) {
        	if(conversationListAdapter == null) {
        		conversationListAdapter = new ConversationListAdapter(getActivity());
        	}
        	lvConversations = (ListView) view.findViewById(R.id.lvConversations);
        	lvConversations.setAdapter(conversationListAdapter);
        }

        ListView lvConversations = (ListView) view.findViewById(R.id.lvConversations);
        lvConversations.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Conversation conversation = (Conversation) arg0.getItemAtPosition(arg2);
				((OnItemClickedListener) getActivity()).onMessageCompose(conversation.getItem());
			}
		});
        return view;
    }


}
