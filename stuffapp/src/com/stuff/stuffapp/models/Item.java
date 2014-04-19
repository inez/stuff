package com.stuff.stuffapp.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Item")
public class Item extends ParseObject {

	public Item() {
		super();
	}
	
	public void setName(String name) {
		put("name", name);
	}

	public String getName() {
		return getString("name"); 
	}

	public void setDescription(String description) {
		put("description", description);
	}
	
	public String getDescription() {
		return getString("description"); 
	}
	
	public void setOwner(ParseUser user) {
		put("owner", user);
	}
	
	public ParseUser getOwner() {
		return (ParseUser) getParseObject("user");
	}
	
	public ParseFile getPhotoFile() {
        return getParseFile("photo");
    }

	public void setPhotoFile(ParseFile file) {
        put("photo", file);
    }

}
