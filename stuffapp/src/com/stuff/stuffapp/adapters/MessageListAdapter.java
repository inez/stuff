package com.stuff.stuffapp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Message;

public class MessageListAdapter extends ParseQueryAdapter<Message>{

	private Context mContext;

	public MessageListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Message>() {

			@Override
			public ParseQuery<Message> create() {
				ParseQuery<Message> query = new ParseQuery<Message>(Message.class);
				query.orderByDescending("createdAt");
				query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
				query.include("fromUser");
				query.include("toUser");
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
