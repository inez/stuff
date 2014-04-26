package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import android.widget.ImageView;

import android.widget.TextView;


import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.ConversationAdapter;
import com.stuff.stuffapp.fragments.HomeFragment.OnItemClickedListener;
import com.stuff.stuffapp.helpers.ConversationListener;
import com.stuff.stuffapp.helpers.Helper;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.Item;

public class DetailsFragment extends Fragment implements ConversationListener{

	private static String TAG = "DetailsFragment";

	private View view;
	
	private Item item;
	
	private Conversation conversation = null;

	public static DetailsFragment newInstance(Item item) {
		DetailsFragment fragment = new DetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("item", item);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		item = (Item) args.getSerializable("item");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: " + item.getName());
        view = inflater.inflate(R.layout.fragment_details, container, false);

        ParseImageView ivPhoto = (ParseImageView) view.findViewById(R.id.ivPhoto);
        ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(item.getPhotoFile().getUrl(), ivPhoto);

        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(item.getName());

        TextView tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDescription.setText(item.getDescription());

        TextView tvOwner = (TextView) view.findViewById(R.id.tvOwner);
        tvOwner.setText(Helper.getUserName(item.getOwner()));
        
        Button button = (Button)view.findViewById(R.id.btContact);
       
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if (conversation == null) {
					//We dont have any conversation about this item between these people. create one. 

					conversation = new Conversation();
					conversation.setItem(ParseObject.createWithoutData(Item.class, item.getObjectId()));
					conversation.setUserOne(ParseUser.getCurrentUser());
					conversation.setUserTwo(ParseObject.createWithoutData(ParseUser.class, item.getOwner().getObjectId()));
					conversation.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(ParseException ex) {
							
							if(ex != null) {
								Log.d(TAG, "Error saving conversation. ex :" + ex);
								ex.printStackTrace();
							}
							
						}
					});
							
					
	
				}
				
				((OnItemClickedListener) getActivity()).onMessageCompose(conversation);
			}
        	
        });
        ImageView ivMessage = (ImageView) view.findViewById(R.id.ivMessage);
        ivMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.flContainer, MessageComposeFragment.newInstance(item));
				ft.addToBackStack("compose");
				ft.commit();
			}
		});

        //Start the find conversation query
        Helper.findConversation(item,this);
        return view;
	}

	@Override
	public void conversationAvailable(Conversation conversation) {
	
		this.conversation = conversation;
		
		Toast.makeText(getActivity(), "Found Conversation:" + conversation, Toast.LENGTH_LONG).show();
		
	}

	
	
}


