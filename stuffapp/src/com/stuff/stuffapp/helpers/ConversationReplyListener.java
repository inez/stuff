package com.stuff.stuffapp.helpers;

import java.util.List;

import com.stuff.stuffapp.models.ConversationReply;

public interface ConversationReplyListener {
	
	public void conversationRepliesAvailable(List<ConversationReply> replies);

}
