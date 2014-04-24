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
			holder.ivProfile = (ParseImageView) v.findViewById(R.id.ivProfile);
			holder.ivPhoto = (ParseImageView) v.findViewById(R.id.ivPhoto);
			holder.tvText = (TextView) v.findViewById(R.id.tvText);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag(); 
		}

		ConversationReply recentReply = conversation.getRecentReply();
		if(recentReply != null) {
			holder.tvText.setText(recentReply.getText());
		} else {
			holder.tvText.setText("");
		}

		ParseUser otherUser;
		if(conversation.getUserOne().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())) {
			otherUser = conversation.getUserTwo();
		} else {
			otherUser = conversation.getUserOne();
		}

		ImageLoader imageLoader = ImageLoader.getInstance();

		JSONObject userProfile = otherUser.getJSONObject("profile");
        try {
    		imageLoader.displayImage("http://graph.facebook.com/" + userProfile.get("facebookId").toString() + "/picture?type=square", holder.ivProfile);
    		Log.d(TAG, "http://graph.facebook.com/" + userProfile.get("facebookId").toString() + "/picture?type=square");
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing saved user data.");
        }
		imageLoader.displayImage(conversation.getItem().getPhotoFile100().getUrl(), holder.ivPhoto);
		
		return v;
	}
	
    static class ViewHolder {
    	ParseImageView ivProfile;
    	ParseImageView ivPhoto;
		TextView tvText;
	}

}
