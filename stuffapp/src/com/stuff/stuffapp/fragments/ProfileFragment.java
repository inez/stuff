package com.stuff.stuffapp.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.MyItemsAdapter;

public class ProfileFragment extends Fragment {
	private static final String TAG = "ProfileFragment";
	
	private View view;
	private ListView lv_myItems;
	private MyItemsAdapter adapter;

	public static ProfileFragment newInstance() {
		ProfileFragment fragment = new ProfileFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        lv_myItems = (ListView) view.findViewById(R.id.lv_myItems);
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
		if (currentUser.get("profile") != null) {
			JSONObject userProfile = currentUser.getJSONObject("profile");
			try {
				ProfilePictureView ppv = (ProfilePictureView) view.findViewById(R.id.profile_picture_view);
				ppv.setProfileId(userProfile.get("facebookId").toString());
				
				tv_name.setText(userProfile.getString("name"));
				
				TextView tv_location = (TextView) view.findViewById(R.id.tv_location);
				tv_location.setText(userProfile.getString("location"));
			} catch (JSONException e) {
				Log.d(TAG, "Error parsing saved user data.");
			}
		}
		else {
		    tv_name.setText("User information unavailable");
		}

        return view;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

	    if ( null == adapter ) {
	        adapter = new MyItemsAdapter(getActivity());
	    }
        lv_myItems.setAdapter(adapter);         
	}
}
