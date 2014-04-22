package com.stuff.stuffapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TempConversationReply")
public class TempConversationReply extends ParseObject {

	public TempConversation getConversation() {
		return (TempConversation) getParseObject("conversation");
	}

	public void setConversation(TempConversation conversation) {
		put("conversation", conversation);
	}
	
	public String getText() {
		return getString("text");
	}

	public void setText(String text) {
		put("text", text);
	}
	
	public void setUser(ParseUser user) {
		put("user", user);
	}
	
	public ParseUser getUser() {
		return getParseUser("user");
	}

}
