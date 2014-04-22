package com.stuff.stuffapp.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("TempConversation")
public class Conversation extends ParseObject  {

	public Conversation() {
		super();
	}
	
	public ParseUser getUserOne() {
		return getParseUser("user_one");
	}

	public void setUserOne(ParseUser user) {
		put("user_one", user);
	}
	
	public ParseUser getUserTwo() {
		return getParseUser("user_two");
	}

	public void setUserTwo(ParseUser user) {
		put("user_two", user);
	}

	public Item getItem() {
		return (Item) getParseObject("item");
	}

	public void setItem(Item item) {
		put("item", item);
	}
	

}
