package com.stuff.stuffapp.adapters;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.ParseQuery.CachePolicy;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.R.color;
import com.stuff.stuffapp.R.drawable;
import com.stuff.stuffapp.R.id;
import com.stuff.stuffapp.R.layout;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.ConversationReply;
import com.stuff.stuffapp.models.Item;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ConversationAdapter extends ParseQueryAdapter<ConversationReply> {
	private Context mContext;

	public ConversationAdapter(Context context, final Item item) {
		super(context, new ParseQueryAdapter.QueryFactory<ConversationReply>() {
        	public ParseQuery<ConversationReply> create() {
                Conversation conversation = findConversation(item);
                
                ParseQuery<ConversationReply> query = null;
                //This should never be null?
                if(conversation != null) {
                	
                	query = new ParseQuery<ConversationReply>(ConversationReply.class);
                	query.whereEqualTo(ConversationReply.ATTR_CONVERSATION, conversation);
                	
                } else  {
                	//something is broken handle this case. 
                }
                return query;
                                                            
                
        	}
        });
		this.mContext = context;
	}
	
	private static Conversation findConversation(Item item) {
		
		Conversation thisConversation = null;
		ParseQuery<Conversation> itemQuery = ParseQuery.getQuery(Conversation.class); 
        itemQuery.whereEqualTo(Conversation.ATTR_ITEM,item);
        				                
        ArrayList<ParseQuery<Conversation>> userQueries = new ArrayList<ParseQuery<Conversation>>();
        ParseQuery<Conversation> user1Query = new ParseQuery<Conversation>(Conversation.class);
        user1Query.whereEqualTo(Conversation.ATTR_USER_ONE, ParseUser.getCurrentUser());
        ParseQuery<Conversation> user2Query = new ParseQuery<Conversation>(Conversation.class);
        user2Query.whereEqualTo(Conversation.ATTR_USER_TWO, ParseUser.getCurrentUser());
        
        userQueries.add(user1Query);
        userQueries.add(user2Query);
       
        ParseQuery<Conversation> orUserQueries = ParseQuery.or(userQueries);                
        orUserQueries.whereMatchesKeyInQuery(Conversation.ATTR_ITEM, Conversation.ATTR_ITEM, itemQuery);     
        try {
			List<Conversation>conversationList = orUserQueries.find();
			if(conversationList.isEmpty() == false) {
				//Iterate through the conversations about this item to find the conversation with the user
				for(Conversation conversation : conversationList) {
					
					conversation = (Conversation)conversation.fetchIfNeeded();
					if(conversation.getUserOne().getObjectId().equals(item.getOwner().getObjectId()) || 
							conversation.getUserTwo().getObjectId().equals(item.getOwner().getObjectId())){
						
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

	@Override
	public View getItemView(ConversationReply message, View convertView, ViewGroup parent) {
		//ConversationReply message = (ConversationReply) this.getItem(position);
		ViewHolder holder;  
		if(convertView == null)
		{
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.message_row, parent, false);
			holder.message = (TextView) convertView.findViewById(R.id.message_text);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getText());
		
		LayoutParams lp = (LayoutParams) holder.message.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage())
		{
			holder.message.setBackgroundDrawable(null);
			lp.gravity = Gravity.LEFT;
			holder.message.setTextColor(R.color.textFieldColor);
		}
		else
		{		
			//Check whether message is mine to show green background and align to right
			if(message.isMine())
			{
				holder.message.setBackgroundResource(R.drawable.speech_bubble_green);
				lp.gravity = Gravity.RIGHT;
			}
			//If not mine then it is from sender to show orange background and align to left
			else
			{
				holder.message.setBackgroundResource(R.drawable.speech_bubble_orange);
				lp.gravity = Gravity.LEFT;
			}
			holder.message.setLayoutParams(lp);
			holder.message.setTextColor(R.color.textColor);	
		}
		return convertView;
	}
	private static class ViewHolder
	{
		TextView message;
	}


}
