package com.stuff.stuffapp;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseFacebookUtils;

public class StuffApplication extends Application {

	public void onCreate() {
		super.onCreate();

		// According to parse.com documentation Application ID and Client Key can be public
		Parse.initialize(this, "2vWbCt6aWC1e0pCRk8BxsuXO1XXk9tqpp1ZMvOzr", "zKk8JQ6cHcIiUCx9r5hVlSsvDAgBGVlJeesXeii0");
		
		ParseFacebookUtils.initialize(getString(R.string.app_id));
	}  

}
