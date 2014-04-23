package com.stuff.stuffapp.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.helpers.Helper;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.ConversationReply;
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

public class MessageComposeFragment extends Fragment {

	private static final String TAG = "MessageComposeFragment";
	private static final String KEY_ITEM = "item";
	private static final String KEY_MESSAGE = "message";

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
	public View onCreateView(LayoutInflater inf, ViewGroup parent,
			Bundle savedInstanceState) {

		View view = inf.inflate(R.layout.fragment_message_compose, parent,
				false);
		etCompose = (EditText) view.findViewById(R.id.etCompose);
		btSend = (Button) view.findViewById(R.id.btSend);
		tvRecepient = (TextView) view.findViewById(R.id.tvRecepient);
		tvRecepient.setText(Helper.getUserName(mForItem.getOwner()));

		// Handle send button click inside the fragment
		btSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

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
								              				                
			}
			
			private ConversationReply createReply() {
				
				Conversation thisConversation = findConversation(mForItem);
				if(thisConversation == null) {
					thisConversation = new Conversation();
					thisConversation.setItem(ParseObject.createWithoutData(Item.class, mForItem.getObjectId()));
					thisConversation.setUserOne(ParseUser.getCurrentUser());
					thisConversation.setUserTwo(ParseObject.createWithoutData(ParseUser.class, mForItem.getOwner().getObjectId()));
					
				}							
				//Create a new conversation Reply - 
				ConversationReply reply = new ConversationReply();
				reply.setConversation(thisConversation);
				reply.setText(etCompose.getText().toString());
				reply.setUser(ParseUser.getCurrentUser());
				
				return reply;
			}
			
			private Conversation findConversation(Item item) {
				
				Conversation thisConversation = null;
				ParseQuery<Conversation> itemQuery = ParseQuery.getQuery(Conversation.class); 
                itemQuery.whereEqualTo("item",mForItem);
                				                
                ArrayList<ParseQuery<Conversation>> userQueries = new ArrayList<ParseQuery<Conversation>>();
                ParseQuery<Conversation> user1Query = new ParseQuery<Conversation>(Conversation.class);
                user1Query.whereEqualTo("userOne", ParseUser.getCurrentUser());
                ParseQuery<Conversation> user2Query = new ParseQuery<Conversation>(Conversation.class);
                user2Query.whereEqualTo("userTwo", ParseUser.getCurrentUser());
                
                userQueries.add(user1Query);
                userQueries.add(user2Query);
               
                ParseQuery<Conversation> orUserQueries = ParseQuery.or(userQueries);                
                orUserQueries.whereMatchesKeyInQuery("item", "item", itemQuery);     
                try {
					List<Conversation>conversationList = orUserQueries.find();
					if(conversationList.isEmpty() == false) {
						//Iterate through the conversations about this item to find the conversation with the user
						for(Conversation conversation : conversationList) {
							
							conversation = (Conversation)conversation.fetchIfNeeded();
							if(conversation.getUserOne().getObjectId().equals(mForItem.getOwner().getObjectId()) || 
									conversation.getUserTwo().getObjectId().equals(mForItem.getOwner().getObjectId())){
								
							 thisConversation = conversation;
								
							}

						}
						
					}

					
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                return thisConversation;
                
			}		
			

		});

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	private ParseUser getLoggedInUser() {

		ParseUser user = ParseUser.getCurrentUser();
		Log.d(TAG, "Current User is :" + user);
		return user;
	}

	private void saveMessage(Message message) {
		final HashMap<String, String> map = new HashMap<String, String>();

		map.put("fromUserId", message.getFromUser().getObjectId());
		map.put("toUserId", message.getToUser().getObjectId());
		map.put("item", message.getItem().getObjectId());
		map.put("text", message.getText());
		try {
			com.parse.ParseCloud.callFunction("saveMessage", map);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * ParseCloud.callFunctionInBackground("sendMessage", map, new
		 * FunctionCallback<Object>() { public void done(Object object,
		 * ParseException e) { if (e == null) { Log.d("DEBUG","Success " +
		 * object.toString()); } else { Log.d("DEBUG","Error " + e); } } });
		 */

	}

}
