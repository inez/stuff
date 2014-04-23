package com.stuff.stuffapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Conversation")
public class Conversation extends ParseObject  {

	public static final String ATTR_USER_ONE = "userOne";
	public static final String ATTR_USER_TWO = "userTwo";
	public static final String ATTR_ITEM = "item";
	
	public Conversation() {
		super();
	}
	
	
	public ParseUser getUserOne() {
		return getParseUser(ATTR_USER_ONE);
	}

	public void setUserOne(ParseUser user) {
		put(ATTR_USER_ONE, user);
	}
	
	public ParseUser getUserTwo() {
		return getParseUser(ATTR_USER_TWO);
	}

	public void setUserTwo(ParseUser user) {
		put(ATTR_USER_TWO, user);
	}

	public Item getItem() {
		return (Item) getParseObject(ATTR_ITEM);
	}

	public void setItem(Item item) {
		put(ATTR_ITEM, item);
	}

}
