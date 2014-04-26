package com.stuff.stuffapp.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.SquareImageView;
import com.stuff.stuffapp.models.Item;

public class ProfileFragment extends Fragment {

	private static final String TAG = "ProfileFragment";

	private View view;
	
	private ProfileAdapter profileAdapter;
	
	private GridView gvItems;

	public static ProfileFragment newInstance() {
		ProfileFragment fragment = new ProfileFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		view = inflater.inflate(R.layout.fragment_profile, container, false);
		gvItems = (GridView) view.findViewById(R.id.gvItems);

		if(savedInstanceState == null) {
        	if(profileAdapter == null) {
        		profileAdapter = new ProfileAdapter(getActivity());
        	}
        	gvItems.setAdapter(profileAdapter);
		}
		
		ImageLoader imageLoader = ImageLoader.getInstance();
		JSONObject profileData = ParseUser.getCurrentUser().getJSONObject("profile");

		//
		// ivProfilePicture
		//
		ImageView ivProfilePicture = (ImageView) view.findViewById(R.id.ivProfilePicture);
		try {
			imageLoader.displayImage("http://graph.facebook.com/" + profileData.get("facebookId").toString() + "/picture?type=normal", ivProfilePicture);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//
		// tvNameAndLocation
		//
		TextView tvNameAndLocation = (TextView) view.findViewById(R.id.tvNameAndLocation);
		String name = null, location = null;

		try {
			name = profileData.get("name").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			location = profileData.get("location").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		tvNameAndLocation.setText(name + (location != null ? "\n" + location : ""));
		
		return view;
	}

	static class ViewHolder {
		SquareImageView sivItem;
	}

	public class ProfileAdapter extends ParseQueryAdapter<Item> {


		public ProfileAdapter(Context context) {
	        super(context, new ParseQueryAdapter.QueryFactory<Item>() {
	            @Override
	            public ParseQuery<Item> create() {
	                ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
	                query.whereEqualTo("owner", ParseUser.getCurrentUser());
	                query.orderByAscending("createdAt");
	                query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
	                return query;
	            }
	        });
	    }

	    @Override
	    public View getItemView(Item item, View v, ViewGroup parent) {
			ViewHolder holder;
			if (v == null) {
				v = View.inflate(getContext(), R.layout.item_grid_profile, null);
				holder = new ViewHolder();
				holder.sivItem = (SquareImageView) v.findViewById(R.id.sivItem);
				v.setTag(holder);
			} else {
				holder = (ViewHolder) v.getTag();
			}

			ImageLoader imageLoader = ImageLoader.getInstance();
	        imageLoader.displayImage(item.getPhotoFile200().getUrl(), holder.sivItem);

	    	return v;
	    }
	}

}
