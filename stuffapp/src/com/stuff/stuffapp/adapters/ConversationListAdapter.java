package com.stuff.stuffapp.adapters;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.helpers.Helper;
import com.stuff.stuffapp.models.Conversation;

public class ConversationListAdapter extends ParseQueryAdapter<Conversation> {

	public ConversationListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Conversation>() {
        	public ParseQuery<Conversation> create() {
                ParseUser user = ParseUser.getCurrentUser();
                
                ParseQuery<Conversation> queryUserOne = new ParseQuery<Conversation>(Conversation.class);
                queryUserOne.whereEqualTo(Conversation.ATTR_USER_ONE, user);

                ParseQuery<Conversation> queryUserTwo = new ParseQuery<Conversation>(Conversation.class);
                queryUserTwo.whereEqualTo(Conversation.ATTR_USER_TWO, user);
                
                List<ParseQuery<Conversation>> queries = new ArrayList<ParseQuery<Conversation>>();
                queries.add(queryUserOne);
                queries.add(queryUserTwo);
                
                ParseQuery<Conversation> query =  ParseQuery.or(queries);
                query.include(Conversation.ATTR_USER_ONE);
                query.include(Conversation.ATTR_USER_TWO);
                query.include(Conversation.ATTR_ITEM);
                return query;
        	}
        });
	}

	@Override
	public View getItemView(Conversation conversation, View v, ViewGroup parent) {
		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_conversations, null);
		}
		TextView tvUserOne = (TextView) v.findViewById(R.id.tvUserOne);
		TextView tvUserTwo = (TextView) v.findViewById(R.id.tvUserTwo);
		TextView tvItem = (TextView) v.findViewById(R.id.tvItem);
		
		tvUserOne.setText(Helper.getUserName(conversation.getUserOne()));
		tvUserTwo.setText(Helper.getUserName(conversation.getUserTwo()));
		tvItem.setText(conversation.getItem().getName());
		
		return v;
	}

}
