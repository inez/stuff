package com.stuff.stuffapp.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SendCallback;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.ConversationAdapter;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.ConversationReply;
import com.stuff.stuffapp.models.Item;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater; 
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConversationsFragment extends ListFragment {

	private static final String TAG = "MessageComposeFragment";
	private static final String KEY_ITEM = "item";

	
	private static final String  KEY_DEVICE_TYPE = "deviceType";
		private static final String  VALUE_ANDROID = "android";
	
	private static final String KEY_ALERT = "alert";


	private Button btSendMessage = null;
	private Item mForItem = null;
	
	ConversationAdapter adapter;
	EditText text;	
	static String sender;

	public static ConversationsFragment newInstance(Item item) {
		ConversationsFragment fragment = new ConversationsFragment();
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
	public View onCreateView(LayoutInflater inf, ViewGroup parent,
			Bundle savedInstanceState) {

		return myCreateView1(inf, parent, savedInstanceState);
		
	}
	
	private View myCreateView1(LayoutInflater inf, ViewGroup parent,
			Bundle savedInstanceState) {
		View view = inf.inflate(R.layout.converesation, parent,
				false);

		
		text = (EditText) view.findViewById(R.id.text);
		btSendMessage = (Button) view.findViewById(R.id.btSendMessage);

		//TODO: Set title on the activity bar. 
		
		//Register button handling in fragment. 
		
		btSendMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(getActivity(), "Sending", Toast.LENGTH_LONG)
						.show();

				sendMessage(v);

                
								              				                
			}
		});
		
	
		adapter = new ConversationAdapter(getActivity(),mForItem);
		setListAdapter(adapter);	

		return view;
	}

	public void sendMessageAsData()
	{
	    ParsePush push = new ParsePush();

	    push.setChannel(mForItem.getOwner().getUsername());

		ParseQuery query = ParseInstallation.getQuery();
		// Notification for Android users
		query.whereEqualTo(KEY_DEVICE_TYPE, VALUE_ANDROID);		
		JSONObject data = null;
		try {
			data = new JSONObject("{'"+KEY_ALERT+"': '"+text.getText().toString()+"'}");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		push.setData(data);
	    push.sendInBackground(new SendCallback() {
			
			@Override
			public void done(ParseException ex) {
				
				if(ex != null) {
					Log.d(TAG,"Error:" + ex);
					ex.printStackTrace();
				}
				Log.d(TAG,"Push notification sent");
				
			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}


	public void sendMessage(View v) {

		Toast.makeText(getActivity(), "Sending", Toast.LENGTH_LONG)
				.show();


        ConversationReply reply = createReply();
        
		reply.saveInBackground(new SaveCallback(){

			@Override
			public void done(ParseException ex) {
				if(ex !=null) {
					ex.printStackTrace();
				}
				
			}
			
		});
				
		sendMessageAsData();
		text.setText("");	
		//Add for local display
		adapter.loadObjects();
	}
	
	private ConversationReply createReply() {
		
		Conversation thisConversation = ConversationAdapter.findConversation(mForItem);
		if(thisConversation == null) {
			thisConversation = new Conversation();
			thisConversation.setItem(ParseObject.createWithoutData(Item.class, mForItem.getObjectId()));
			thisConversation.setUserOne(ParseUser.getCurrentUser());
			thisConversation.setUserTwo(ParseObject.createWithoutData(ParseUser.class, mForItem.getOwner().getObjectId()));
			
		}							
		//Create a new conversation Reply - 
		ConversationReply reply = new ConversationReply();
		reply.setConversation(thisConversation);
		reply.setText(text.getText().toString());
		reply.setUser(ParseUser.getCurrentUser());
		
		return reply;
	}
	
	

	void addNewConversationReply(ConversationReply m)
	{

	}


}
