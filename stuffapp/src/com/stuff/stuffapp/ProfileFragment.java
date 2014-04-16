package com.stuff.stuffapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;
import com.parse.ParseFacebookUtils;

public class ProfileFragment extends Fragment {

	private static String TAG = "ProfileFragment";
	
	private View view;

	public static ProfileFragment newInstance() {
		ProfileFragment fragment = new ProfileFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
						//Log.d(TAG, "Location name: " + user.getLocation().getProperty("name"));
						ProfilePictureView ppv = (ProfilePictureView) view.findViewById(R.id.profile_picture_view);
						ppv.setProfileId(user.getId());
					}
				}
			}
		);
		request.executeAsync();
	}
}
