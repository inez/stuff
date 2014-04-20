package com.stuff.stuffapp.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;

public class AboutMeFragment extends Fragment {
    private static final String TAG = "AboutMeFragment";

    public static final AboutMeFragment newInstance() {
        return new AboutMeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);

        TextView tvName = (TextView) view.findViewById(R.id.tv_profile_name);
        TextView tvLocation = (TextView) view.findViewById(R.id.tv_profile_location);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if ( null != currentUser.get("profile") ) {
            JSONObject userProfile = currentUser.getJSONObject("profile");
            try {
                ProfilePictureView ppv = (ProfilePictureView) view.findViewById(R.id.profile_picture_view);
                ppv.setProfileId(userProfile.get("facebookId").toString());
                
                tvName.setText(userProfile.getString("name"));
                
                tvLocation.setText(userProfile.optString("location"));
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing saved user data.");
            }
        }
        else {
            tvName.setText("User information unavailable");
        }

        return view;
    }
}
