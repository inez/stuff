package com.stuff.stuffapp.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.stuff.stuffapp.adapters.ConversationListAdapter;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.Item;

public class Helper {

	public static String getUserName(ParseUser user) {
		try {
			if (user.get("profile") != null) {
				JSONObject userProfile = user.getJSONObject("profile");
				return userProfile.getString("name");
			}
		} catch (JSONException e) {
		}
		return "";
	}

	public static void findConversation(final Item item, final ConversationListener conversationListener) {

		
		ParseQuery<Conversation> itemQuery = ParseQuery
				.getQuery(Conversation.class);
		itemQuery.whereEqualTo(Conversation.ATTR_ITEM, item);

		ArrayList<ParseQuery<Conversation>> userQueries = new ArrayList<ParseQuery<Conversation>>();
		ParseQuery<Conversation> user1Query = new ParseQuery<Conversation>(
				Conversation.class);
		user1Query.whereEqualTo(Conversation.ATTR_USER_ONE,
				ParseUser.getCurrentUser());
		ParseQuery<Conversation> user2Query = new ParseQuery<Conversation>(
				Conversation.class);
		user2Query.whereEqualTo(Conversation.ATTR_USER_TWO,
				ParseUser.getCurrentUser());

		userQueries.add(user1Query);
		userQueries.add(user2Query);

		
		ParseQuery<Conversation> tempQuery = ParseQuery.or(userQueries);
		tempQuery.whereMatchesKeyInQuery(Conversation.ATTR_ITEM,
				Conversation.ATTR_ITEM, itemQuery);
		tempQuery.findInBackground(new FindCallback<Conversation>() {

			@Override
			public void done(List<Conversation> conversationList, ParseException ex) {

				if(ex == null) { 
					
					if(conversationList.isEmpty() == false) {
						//Iterate through the conversations about this item to find the conversation with the user
						for(Conversation conversation : conversationList) {
							
							try {
								conversation = (Conversation)conversation.fetchIfNeeded();
								conversationListener.conversationAvailable(conversation);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							if(conversation.getUserOne().getObjectId().equals(item.getOwner().getObjectId()) || 
									conversation.getUserTwo().getObjectId().equals(item.getOwner().getObjectId())){
								
								conversationListener.conversationAvailable(conversation);
								
							}

						
						
					}

					
				}
					
				}

			}
		});

	}

}


