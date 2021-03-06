package com.stuff.stuffapp.adapters;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQuery.CachePolicy;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.ConversationReply;
import com.stuff.stuffapp.models.Item;

public class ConversationListAdapter extends ParseQueryAdapter<Conversation> {

	private static final String TAG = "ConversationListAdapter";
	
	public ConversationListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Conversation>() {
        	public ParseQuery<Conversation> create() {
                ParseUser user = ParseUser.getCurrentUser();
                
                ParseQuery<Conversation> queryUserOne = new ParseQuery<Conversation>(Conversation.class);
                queryUserOne.whereEqualTo(Conversation.ATTR_USER_ONE, user);
                queryUserOne.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);

                ParseQuery<Conversation> queryUserTwo = new ParseQuery<Conversation>(Conversation.class);
                queryUserTwo.whereEqualTo(Conversation.ATTR_USER_TWO, user);
                queryUserTwo.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
                
                List<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();
                queries.add(queryUserOne);
                queries.add(queryUserTwo);
                
                ParseQuery<Conversation> query =  ParseQuery.or(queries);
                query.include(Conversation.ATTR_USER_ONE);
                query.include(Conversation.ATTR_USER_TWO);
                query.include(Conversation.ATTR_ITEM);
                query.include(Conversation.ATTR_RECENT_REPLY);
                query.setCachePolicy(CachePolicy.CACHE_THEN_NETWORK);
                return query;
        	}
        });
	}

	@Override
	public View getItemView(Conversation conversation, View v, ViewGroup parent) {
		final ViewHolder holder;
		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_conversations, null);
			holder = new ViewHolder();
			holder.ivProfilePicture = (ParseImageView) v.findViewById(R.id.ivProfilePicture);
			holder.ivPhoto = (ParseImageView) v.findViewById(R.id.ivPhoto);
			holder.tvText = (TextView) v.findViewById(R.id.tvText);
			holder.tvName = (TextView) v.findViewById(R.id.tvName);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag(); 
		}
		
		ImageLoader imageLoader = ImageLoader.getInstance();

		ParseUser otherUser;
		if(conversation.getUserOne().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
			otherUser = conversation.getUserTwo();
		} else {
			otherUser = conversation.getUserOne();
		}
		
		JSONObject profileData = otherUser.getJSONObject("profile");
		
		//
		// ivPhoto
		
		//
		Item i = conversation.getItem();

        if(i !=null) {
        	imageLoader.displayImage(i.getPhotoFile100().getUrl(), holder.ivPhoto);
    		
    		//
    		// ivProfile
    		//
    		try {
        		imageLoader.displayImage("http://graph.facebook.com/" + profileData.get("facebookId").toString() + "/picture?type=square", holder.ivProfilePicture);
            } catch (JSONException e) {
            	e.printStackTrace();
            }
        }
		
		
		//
		// tvText
		//
		ConversationReply recentReply = conversation.getRecentReply();
		if(recentReply != null) {
			holder.tvText.setText(recentReply.getText());
		} else {
			holder.tvText.setText("");
		}
		
		//
		// tvName
		//
		try {
			holder.tvName.setText(profileData.get("name").toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return v;
	}
	
    static class ViewHolder {
		ParseImageView ivProfilePicture;
    	ParseImageView ivPhoto;
		TextView tvText, tvName;
	}

}
