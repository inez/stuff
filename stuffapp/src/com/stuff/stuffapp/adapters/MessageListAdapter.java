package com.stuff.stuffapp.adapters;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.helpers.Helper;
import com.stuff.stuffapp.models.Item;
import com.stuff.stuffapp.models.Message;

public class MessageListAdapter extends ParseQueryAdapter<Message>{

	private static String TAG ="MessageListAdapter";
	
	Context mContext;
	
	static class ViewHolder {
	 		
	    ImageView ivItem;
	    ProfilePictureView ppvUser;
	    TextView tvUserName;
	    TextView tvDate;
        Message message; 
	}


	public MessageListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Message>() {

			@Override
			public ParseQuery<Message> create() {
				ParseQuery<Message> query = new ParseQuery<Message>(Message.class);
				query.orderByDescending("createdAt");
				query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
				query.include("fromUser");
			    query.include("itemId");
			    query.include("toUserId");
				return query;
			}
			
		});
		mContext = context;
	}
	
	@Override
	public View getItemView(final Message message, View v, ViewGroup parent) {
        final ViewHolder holder;
		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_messages, null);
			holder = new ViewHolder();
			
			holder.ivItem = (ImageView) v.findViewById(R.id.iv_item);
			holder.ppvUser = (ProfilePictureView) v.findViewById(R.id.ppv_user);
			holder.tvUserName = (TextView) v.findViewById(R.id.tv_userName);
			holder.tvDate = (TextView) v.findViewById(R.id.tv_date);
			v.setTag(holder);
			
		} else  {
						
			holder = (ViewHolder)v.getTag();
		}


		ParseUser fromUser = message.getFromUser();

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("username", message.getToUserId());
		
		query.findInBackground(new FindCallback<ParseUser>() {
			  public void done(List<ParseUser> objects, ParseException e) {
			    if (e == null) {
			        ParseUser toUser = (ParseUser)objects.get(0);
			        message.setToUser(toUser);
			    } else {
			        Log.d(TAG,e.toString());
			    }
			  }

			});
		
		ParseQuery<Item> queryItem = new ParseQuery<Item>(Item.class);
		queryItem.whereEqualTo("objectId", message.getItemId());
		
		queryItem.findInBackground(new FindCallback<Item>() {
			  public void done(List<Item> objects, ParseException e) {
			    if (e == null) {
			        Item item = (Item)objects.get(0);
			        message.setItem(item);
			    } else {
			        Log.d(TAG,e.toString());
			    }
			  }

			});
		
		holder.ivItem.setVisibility(View.INVISIBLE);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(message.getItem().getPhotoFile200().getUrl(), holder.ivItem, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				holder.ivItem.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		}) ;

				

        ParseUser user = message.getToUser();
        if ( null != user.get("profile") ) {
            JSONObject userProfile = user.getJSONObject("profile");
            try {

                holder.ppvUser.setProfileId(userProfile.get("facebookId").toString());
                
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing saved user data.");
            }
        }
        else {
            
        }
        
        holder.tvUserName.setText(Helper.getUserName(message.getToUser()));
        holder.tvDate.setText(message.getCreatedAt().toString());
				
		return v;
	}
}
