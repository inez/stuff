package com.stuff.stuffapp.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject implements Serializable {
	
	private static final String ATTR_CREATED_AT = "createdAt";
	private static final String ATTR_SEEN_AT = "seenAt";
	private static final String ATTR_FROM_USER = "fromUser";
	private static final String ATTR_TO_USER = "toUser";
	private static final String ATTR_ITEM = "item";
	private static final String ATTR_TEXT = "text";
	
	private static final String ATTR_FROM_USER_ID = "fromUserId";
	private static final String ATTR_TO_USER_ID = "toUserId";
	private static final String ATTR_ITEM_ID = "itemId";
	
	
	private static final long serialVersionUID = -7868320080366126498L;
	public Message() {
		super();
	}

	public String getText() {
		return getString(ATTR_TEXT);
	}
	public void setText(String text) {
		put(ATTR_TEXT,text);
	}
	
	public ParseUser getFromUser() {
		return getParseUser(ATTR_FROM_USER);
	}
	public void setFromUser(ParseUser fromUser) {
		put(ATTR_FROM_USER,fromUser);
	}
	public ParseUser getToUser() {
		return getParseUser(ATTR_TO_USER);
	}
	public void setToUser(ParseUser toUser) {
		put(ATTR_TO_USER,toUser);
	}
	
	public Date getSeenAt() {
		return getDate(ATTR_SEEN_AT);
	}
	public void setSeenAt(Date seenAt) {
		put(ATTR_SEEN_AT,seenAt);
	} 
	
	public void setItem(Item item) {
		put(ATTR_ITEM,item);
	}
	
	public Item getItem() {
		return (Item) getParseObject(ATTR_ITEM);
	}
	
	//TODO: Fix this to include actual objects. 

	public String getFromUserId() {
		return getString(ATTR_FROM_USER_ID);
	}
	public void setFromUserId(String fromUserId) {
		put(ATTR_FROM_USER_ID,fromUserId);
	}
	public String getToUserId() {
		return getString(ATTR_TO_USER_ID);
	}
	public void setToUserId(String toUserId) {
		put(ATTR_TO_USER_ID,toUserId);
	}

	public void setItemId(String itemId) {
		put(ATTR_ITEM_ID,itemId);
	}
	
	public String getItemId() {
		return getString(ATTR_ITEM_ID);
	}
	

}
