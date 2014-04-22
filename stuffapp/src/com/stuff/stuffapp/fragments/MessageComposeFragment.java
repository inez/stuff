package com.stuff.stuffapp.fragments;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.helpers.Helper;
import com.stuff.stuffapp.models.Item;
import com.stuff.stuffapp.models.Message;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MessageComposeFragment extends Fragment{
	
	private static final String TAG = "MessageComposeFragment";
	private static final String KEY_ITEM = "item";
	private static final String KEY_MESSAGE="message";
	
	private EditText etCompose = null;
	private TextView tvRecepient = null;
	private Button btSend = null;
	private Item mForItem = null; 
	
	
	public static MessageComposeFragment newInstance(Item item) {
		MessageComposeFragment fragment = new MessageComposeFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_ITEM, item);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		mForItem = (Item) args.getSerializable(KEY_ITEM);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
		
		View view = inf.inflate(R.layout.fragment_message_compose,parent,false);
		etCompose =  (EditText) view.findViewById(R.id.etCompose);
		btSend = (Button) view.findViewById(R.id.btSend);
		tvRecepient = (TextView) view.findViewById(R.id.tvRecepient);
		tvRecepient.setText(Helper.getUserName(mForItem.getOwner()));
		
		//Handle send button click inside the fragment 
		btSend.setOnClickListener(new OnClickListener(){			
			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity(), "Sending", Toast.LENGTH_LONG).show();

	/* This does NOT work			
				Message message = new Message();
				message.setText(etCompose.getText().toString());
				message.setFromUser(getLoggedInUser());
				message.setToUser(mForItem.getOwner());
			    message.setItem(mForItem);
				Log.d(TAG,getLoggedInUser().getObjectId());
				message.saveInBackground(new SaveCallback(){

					@Override
					public void done(ParseException ex) {
						// TODO Auto-generated method stub
						if(ex != null) {
							ex.printStackTrace();
						}
					}
					
				}); */
				
	
// This works! Read the item (for the owner) first (even though you already have the item and the user	) 	
		 ParseQuery<Item> query = ParseQuery.getQuery(Item.class);
		 query.getInBackground(mForItem.getObjectId(), new GetCallback<Item>() {
		     public void done(Item item, ParseException ex) {
		         if (ex == null) {
		        	 
		        	 Log.d(TAG, item.getObjectId());
						com.stuff.stuffapp.models.Message m = new com.stuff.stuffapp.models.Message();
						m.setText(etCompose.getText().toString());
						m.setFromUser(ParseUser.getCurrentUser());
						m.setToUser(item.getOwner());
						m.setItem(item);
						m.saveInBackground();
		             
		         } else {
		             
						Log.d(TAG, "Error retrieving object : " + ex.toString());
						ex.printStackTrace();
		         }
		     }
		 });
		 
		

				
			}
			
		});
        
		return view;
	}
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
	}
		
	private ParseUser getLoggedInUser() { 

		ParseUser user = ParseUser.getCurrentUser();
		Log.d(TAG,"Current User is :" + user);
		return user; 
	}
	
	private void saveMessage (Message message) {
		final HashMap<String,String> map = new HashMap<String,String>();
		
		map.put("fromUserId", message.getFromUser().getObjectId());
		map.put("toUserId", message.getToUser().getObjectId());
		map.put("item",message.getItem().getObjectId());
		map.put("text",message.getText());
		try {
			com.parse.ParseCloud.callFunction("saveMessage", map);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		ParseCloud.callFunctionInBackground("sendMessage", map, new FunctionCallback<Object>() {
		      public void done(Object object, ParseException e) {
		        if (e == null) {
		          Log.d("DEBUG","Success " + object.toString());
		        } else {
		        	Log.d("DEBUG","Error " + e);
		        }
		      }
		 });*/
		
	}



}
