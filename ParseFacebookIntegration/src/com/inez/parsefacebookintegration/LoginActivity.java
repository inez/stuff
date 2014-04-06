package com.inez.parsefacebookintegration;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			showUserDetailsActivity();
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

	private ProgressDialog progressDialog;
	
	public void onLoginClick(View view) {
		progressDialog = ProgressDialog.show(LoginActivity.this, "", getString(R.string.logging_in), true);
		List<String> permissions = Arrays.asList("basic_info", "user_about_me", "user_location");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {

			@Override
			public void done(ParseUser user, ParseException err) {
				progressDialog.dismiss();
				
				if (user == null) {
					Log.d(ParseFacebookIntegrationApplication.TAG, "Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.d(ParseFacebookIntegrationApplication.TAG, "User signed up and logged in through Facebook!");
					showUserDetailsActivity();
				} else {
					Log.d(ParseFacebookIntegrationApplication.TAG, "User logged in through Facebook!");
					showUserDetailsActivity();
				}
			}
			
		});
	}
	
	private void showUserDetailsActivity() {
		Intent intent = new Intent(this, UserDetailsActivity.class);
		startActivity(intent);
	}

}
