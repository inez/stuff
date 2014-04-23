package com.stuff.stuffapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ConversationReply")
public class ConversationReply extends ParseObject {

	public static final String ATTR_CONVERSATION = "conversation";
	public static final String ATTR_TEXT = "text";
	public static final String ATTR_USER = "user";
	public static final String ATTR_STATUS_MESSAGE ="statusMessage";

	private boolean isStatusMessage = false;
	public boolean isStatusConversationReply;
	
	public ConversationReply() {
		super();
	}
	
	public ConversationReply(String messageText, boolean isStatusMessage) {
		super();
		
		setText(messageText);
		this.isStatusMessage = false;
	}
	
	public ConversationReply(boolean status, String messageText) {
		super();
		setText(messageText);
		this.isStatusMessage = status;
	}
	
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

	public boolean isStatusMessage() {
		// TODO Auto-generated method stub
		return isStatusMessage;
	}

	public boolean isMine() {

		return getUser().getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
	}

}
