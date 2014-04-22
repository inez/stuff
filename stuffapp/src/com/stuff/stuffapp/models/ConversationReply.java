package com.stuff.stuffapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TempConversationReply")
public class ConversationReply extends ParseObject {

	public Conversation getConversation() {
		return (Conversation) getParseObject("conversation");
	}

	public void setConversation(Conversation conversation) {
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
