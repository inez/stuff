package com.stuff.stuffapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ConversationReply")
public class ConversationReply extends ParseObject {

	private static final String ATTR_CONVERSATION = "conversation";
	private static final String ATTR_TEXT = "text";
	private static final String ATTR_USER = "user";

	public Conversation getConversation() {
		return (Conversation) getParseObject(ATTR_CONVERSATION);
	}

	public void setConversation(Conversation conversation) {
		put(ATTR_CONVERSATION, conversation);
	}
	
	public String getText() {
		return getString(ATTR_TEXT);
	}

	public void setText(String text) {
		put(ATTR_TEXT, text);
	}
	
	public void setUser(ParseUser user) {
		put(ATTR_USER, user);
	}
	
	public ParseUser getUser() {
		return getParseUser(ATTR_USER);
	}

}
