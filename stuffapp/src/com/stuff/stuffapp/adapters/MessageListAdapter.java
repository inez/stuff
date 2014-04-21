package com.stuff.stuffapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Message;

public class MessageListAdapter extends ParseQueryAdapter<Message>{
	
	private static final String ATTR_CREATED_AT = "createdAt";
	private static final String ATTR_FROM_USER = "fromUser";
	private static final String ATTR_TO_USER = "toUser";

	private Context mContext;

	public MessageListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Message>() {

			@Override
			public ParseQuery<Message> create() {
				ParseQuery<Message> query = new ParseQuery<Message>(Message.class);
				query.orderByDescending(ATTR_CREATED_AT);
				query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
				query.include(ATTR_FROM_USER);
				query.include(ATTR_TO_USER);
				return query;
			}
			
		});
		mContext = context;
	}
	
	@Override
	public View getItemView(Message message, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_messages, null);
	
		} else {

		}

		

		return v;
	}
}
