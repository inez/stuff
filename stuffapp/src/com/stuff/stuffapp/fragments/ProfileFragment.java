package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;
import com.stuff.stuffapp.R;

public class ProfileFragment extends Fragment {

	private static String TAG = "ProfileFragment";
	
	private View view;

	public static ProfileFragment newInstance() {
		ProfileFragment fragment = new ProfileFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
        	makeMeRequest();
        }
        
		return view;
    }
	
	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
			new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						ProfilePictureView ppv = (ProfilePictureView) view.findViewById(R.id.profile_picture_view);
						ppv.setProfileId(user.getId());
						
						TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
						tv_name.setText(user.getName());

						TextView tv_location = (TextView) view.findViewById(R.id.tv_location);
						tv_location.setText(user.getLocation().getProperty("name").toString());
					}
				}
			}
		);
		request.executeAsync();
	}
}
