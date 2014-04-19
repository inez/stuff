package com.stuff.stuffapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Item;

public class HomeFeedAdapter extends ParseQueryAdapter<Item> {

	private static String TAG = "HomeFeedAdapter";

	public HomeFeedAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Item>() {

			@Override
			public ParseQuery<Item> create() {
				ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
				query.orderByDescending("createdAt");
				query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
				return query;
			}
			
		});
	}

	@Override
	public View getItemView(Item item, View v, ViewGroup parent) {
		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_home, null);
		}

		final ParseImageView iv_photo = (ParseImageView) v.findViewById(R.id.iv_photo);
		iv_photo.setParseFile(item.getPhotoFile200());
		iv_photo.loadInBackground();
		iv_photo.loadInBackground(new GetDataCallback() {
			@Override
			public void done(byte[] arg0, ParseException arg1) {
				iv_photo.setVisibility(View.VISIBLE);
			}
		});

		TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
		tv_name.setText(item.getName());

		//TextView tv_description = (TextView) v.findViewById(R.id.tv_description);
		//tv_description.setText(item.getDescription());

		return v;
	}

}
