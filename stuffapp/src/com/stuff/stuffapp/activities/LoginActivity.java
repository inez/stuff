package com.stuff.stuffapp.activities;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;

public class LoginActivity extends Activity {

	private static String TAG = "LoginActivity";

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ParseAnalytics.trackAppOpened(getIntent());
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			Log.d(TAG, "Already logged in.");
			showMainActivity();
		} else {
			Log.d(TAG, "Not logged in yet.");			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}
	
	public void onLoginClick(View view) {
		Log.d(TAG, "onLoginClick");
		progressDialog = ProgressDialog.show(LoginActivity.this, "", getString(R.string.please_wait), true);
		List<String> permissions = Arrays.asList("basic_info", "user_about_me", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {

			@Override
			public void done(ParseUser parseUser, ParseException err) {
				progressDialog.dismiss();
				
				if ( parseUser == null) {
					Log.d(TAG, "Uh oh. The user cancelled the Facebook login.");
				} else {
					if (parseUser.isNew()) {
						Log.d(TAG, "User signed up and logged in through Facebook!");
					} else {
						Log.d(TAG, "User logged in through Facebook!");
					}
					// FIXME: This request is used to retrieve from Facebook data required to display correctly profile fragment.
					// It is async so there is a possibility that user could go to profile fragment before it is finished.
					Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
						new Request.GraphUserCallback() {
							@Override
							public void onCompleted(GraphUser facebookUser, Response response) {
								if (facebookUser != null) {
									JSONObject userProfile = new JSONObject();
									try {
										userProfile.put("facebookId", facebookUser.getId());
										userProfile.put("name", facebookUser.getName());
										userProfile.put("location", (String) facebookUser.getLocation().getProperty("name"));
									} catch (Exception e) {
										Log.d(TAG, "Error parsing returned user data.");
									}
									ParseUser parseUser = ParseUser.getCurrentUser();
									parseUser.put("profile", userProfile);
									parseUser.saveInBackground();
								}
							}
						}
					);
					request.executeAsync();
					showMainActivity();
				}
			}
			
		});
	}

	public void onLogoutClick(View view) {
		ParseUser.logOut();
	}
	
	private void showMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
}
