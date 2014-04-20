package com.stuff.stuffapp.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import com.parse.ParseUser;

public class Helper {

	public static String getUserName(ParseUser user) {
		try {
			if (user.get("profile") != null) {
				JSONObject userProfile = user.getJSONObject("profile");
					return userProfile.getString("name");
			}
		} catch (JSONException e) {
		}
		return "";
	}
}
