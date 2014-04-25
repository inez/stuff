package com.stuff.stuffapp.adapters;

import org.ocpsoft.prettytime.PrettyTime;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.models.Item;

public class HomeFeedAdapter extends ParseQueryAdapter<Item> {

	static class ViewHolder {
		ParseImageView ivItemPicture;
		TextView tvName, tvDistanceAndTime;
	}

	private Context context;

	public HomeFeedAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Item>() {
			@Override
			public ParseQuery<Item> create() {
				ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
				query.orderByDescending("createdAt");
				query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
				query.include("owner");
				return query;
			}
		});
		this.context = context;
	}

	@Override
	public View getItemView(Item item, View v, ViewGroup parent) {
		ViewHolder holder;
		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_home, null);
			holder = new ViewHolder();
			holder.ivItemPicture = (ParseImageView) v.findViewById(R.id.ivItemPicture);
			holder.tvName = (TextView) v.findViewById(R.id.tvName);
			holder.tvDistanceAndTime = (TextView) v.findViewById(R.id.tvDistanceAndTime);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}

		//
		// tvName
		//
		holder.tvName.setText(item.getName());

		//
		// ivItemPicture
		//
		holder.ivItemPicture.setVisibility(View.INVISIBLE);
		ImageLoader imageLoader = ImageLoader.getInstance();
		// TODO: Use proper size image
		imageLoader.displayImage(item.getPhotoFile().getUrl(), holder.ivItemPicture, new ImageLoadingListener() {
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}

			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				arg1.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}

			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
		});

		//
		// tvDistanceAndTime
		//
		ParseGeoPoint itemLocation = item.getLocation();
		ParseGeoPoint userLocation = ((MainActivity) context).getLastKnownLocation();
		// TODO: Better handling for case when location is not available.
		// TODO: i18n
		// TODO: mile vs. miles
		if (itemLocation != null && userLocation != null) {
			double distanceToItem = userLocation.distanceInMilesTo(itemLocation);
			holder.tvDistanceAndTime.setText(String.format("%.1f miles, %s", distanceToItem,
					(new PrettyTime()).format(item.getCreatedAt())));
		} else {
			holder.tvDistanceAndTime.setText((new PrettyTime()).format(item.getCreatedAt()));
		}

		return v;
	}
}
