package com.stuff.stuffapp.adapters;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.RoundedImageView;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.ConversationReply;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ConversationAdapter extends ParseQueryAdapter<ConversationReply>  {
	private Context mContext;
    private Conversation conversation;
    private Map<String,Bitmap> userImageMap;
    
	public ConversationAdapter(Context context, final Conversation c) {
		super(context, new ParseQueryAdapter.QueryFactory<ConversationReply>() {
        	public ParseQuery<ConversationReply> create() {
                
                ParseQuery<ConversationReply> query = null;
                //This should never be null?
                if(c != null) {
                	
                	query = new ParseQuery<ConversationReply>(ConversationReply.class);
                	query.whereEqualTo(ConversationReply.ATTR_CONVERSATION, c);
                	
                } else  {
                	//something is broken handle this case. 
                }
                return query;
                                                            
                
        	}
        });
		this.mContext = context;
		this.conversation = c;
		userImageMap = new HashMap<String,Bitmap>();
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
			holder.rivMessageProfilePicture = (RoundedImageView)convertView.findViewById(R.id.rivMessageProfilePicture);
			convertView.setTag(holder);
		}
		else
			holder = (ViewHolder) convertView.getTag();
		
		holder.message.setText(message.getText());
		
		LayoutParams lpMessage = (LayoutParams)holder.message.getLayoutParams();
		LayoutParams lpRoundedImageView = (LayoutParams)holder.rivMessageProfilePicture.getLayoutParams();
		//check if it is a status message then remove background, and change text color.
		if(message.isStatusMessage())
		{
			holder.message.setBackgroundDrawable(null);
			
			lpMessage.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			holder.message.setTextColor(R.color.textFieldColor);
		}
		else
		{	
			loadProfileImage(message,holder);
						//Check whether message is mine to show my profile picture and align to right
			if(message.isMine())
			{

				lpRoundedImageView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lpMessage.addRule(RelativeLayout.LEFT_OF,R.id.rivMessageProfilePicture);
			}
			//If not mine then it is from sender to show their profile picture and align to left
			else
			{

				lpRoundedImageView.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lpMessage.addRule(RelativeLayout.RIGHT_OF,R.id.rivMessageProfilePicture);
			}
			holder.message.setLayoutParams(lpMessage);
			holder.message.setTextColor(R.color.textColor);	
		}
		convertView.refreshDrawableState();
		return convertView;
	}
	
	private void loadProfileImage(ConversationReply message, ViewHolder holder) {

		
		
		ParseUser user = null;
		try {
			user = message.getUser().fetchIfNeeded();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(userImageMap.get(user.getObjectId()) == null) {
			
			ImageLoader imageLoader = ImageLoader.getInstance();
			JSONObject profileData = user.getJSONObject("profile");

			try {
				
				
				//imageLoader.displayImage("http://graph.facebook.com/" + profileData.get("facebookId").toString() + "/picture?type=normal", holder.rivMessageProfilePicture);
				Bitmap bitmap = imageLoader.loadImageSync("http://graph.facebook.com/" + profileData.get("facebookId").toString() + "/picture?type=normal");
				userImageMap.put(user.getObjectId(), bitmap);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			
		}
		holder.rivMessageProfilePicture.setImageBitmap(userImageMap.get(user.getObjectId()));
		

		
	}
	private static class ViewHolder
	{
		TextView message;
		RoundedImageView rivMessageProfilePicture;
		
	}
	



}

