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
import com.stuff.stuffapp.helpers.ConversationListener;
import com.stuff.stuffapp.helpers.Helper;
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

public class ConversationsFragment extends ListFragment implements ConversationListener {

	private static final String TAG = "MessageComposeFragment";
	private static final String KEY_CONVERSATION = "conversation";

	
	private static final String  KEY_DEVICE_TYPE = "deviceType";
		private static final String  VALUE_ANDROID = "android";
	
	private static final String KEY_ALERT = "alert";
	private static final String KEY_CONVERSATION_ID ="conversation_id";
	


	private Button btSendMessage = null;
	private Conversation conversation = null;
	
	ConversationAdapter adapter;
	EditText text;	
	static String sender;

	public static ConversationsFragment newInstance(Conversation conversation) {
		ConversationsFragment fragment = new ConversationsFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(KEY_CONVERSATION, conversation);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		conversation =  (Conversation)args.getSerializable(KEY_CONVERSATION);
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
		
	
		adapter = new ConversationAdapter(getActivity(),conversation);
		setListAdapter(adapter);	

		return view;
	}

	public void sendMessageAsData()
	{
	    ParsePush push = new ParsePush();

	    Item item = null;
		try {
			item = (Item)conversation.getItem().fetchIfNeeded();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ParseUser user = null;
		try {
			user = (ParseUser)item.getOwner().fetchIfNeeded();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    push.setChannel(user.getUsername());

		ParseQuery query = ParseInstallation.getQuery();
		// Notification for Android users
		query.whereEqualTo(KEY_DEVICE_TYPE, VALUE_ANDROID);		
		JSONObject data = null;
		try {
			data = new JSONObject("{'"+KEY_ALERT+"': '"+text.getText().toString()+"','"+KEY_CONVERSATION_ID+"':'"+conversation.getObjectId()+"'}");
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
		
		//Conversation thisConversation = Helper.findConversation(mForItem,this);
		Conversation thisConversation = conversation;
						
		//Create a new conversation Reply - 
		ConversationReply reply = new ConversationReply();
		reply.setConversation(thisConversation);
		reply.setText(text.getText().toString());
		reply.setUser(ParseUser.getCurrentUser());

		thisConversation.setRecentReply(reply);
		
		return reply;
	}
	
	

	void addNewConversationReply(ConversationReply m)
	{

	}

	@Override
	public void conversationAvailable(Conversation conversation) {
		
		
	}


}
