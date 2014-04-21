package com.stuff.stuffapp.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Message")
public class Message extends ParseObject implements Serializable {
	

	private static final long serialVersionUID = -7868320080366126498L;
	public Message() {
		super();
	}

	public String getText() {
		return getString("text");
	}
	public void setText(String text) {
		put("text",text);
	}
	
	public ParseUser getFromUser() {
		return getParseUser("fromUser");
	}
	public void setFromUser(ParseUser fromUser) {
		put("fromUser",fromUser);
	}
	public ParseUser getToUser() {
		return getParseUser("toUser");
	}
	public void setToUser(ParseUser toUser) {
		put("toUser",toUser);
	}
	
	public Date getSeenAt() {
		return getDate("seenAt");
	}
	public void setSeenAt(Date seenAt) {
		put("seenAt",seenAt);
	} 
	
	

}
