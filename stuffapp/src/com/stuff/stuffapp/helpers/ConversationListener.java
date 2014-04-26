package com.stuff.stuffapp.helpers;

import com.stuff.stuffapp.models.Conversation;

public interface ConversationListener {
	
	public void conversationAvailable(Conversation conversation);
}