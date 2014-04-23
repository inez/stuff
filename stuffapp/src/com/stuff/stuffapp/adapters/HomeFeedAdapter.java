package com.stuff.stuffapp.adapters;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

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
    private Context mContext;

    static class ViewHolder {
		ParseImageView ivPhoto;
		TextView tvName, tvCoordinates, tvDistance;
	}
	
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
		mContext = context;
	}

	@Override
	public View getItemView(Item item, View v, ViewGroup parent) {
		final ViewHolder holder;
		if (v == null) {
			v = View.inflate(getContext(), R.layout.item_list_home, null);
			holder = new ViewHolder();
			holder.ivPhoto =  (ParseImageView) v.findViewById(R.id.ivPhoto);
			holder.tvName = (TextView) v.findViewById(R.id.tvName);
			holder.tvCoordinates = (TextView) v.findViewById(R.id.tvCoordinates);
			holder.tvDistance = (TextView) v.findViewById(R.id.tvDistance);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag(); 
		}

		holder.ivPhoto.setVisibility(View.INVISIBLE);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(item.getPhotoFile200().getUrl(), holder.ivPhoto, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
				holder.ivPhoto.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
			}
		}) ;

		holder.tvName.setText(item.getName());

        ParseGeoPoint coord = item.getLocation();
        ParseGeoPoint userLocation = ((MainActivity) mContext).getLastKnownLocation();
		if ( null != coord && null != userLocation ) {
			holder.tvCoordinates.setText("(" + coord.getLatitude() + ", " + coord.getLongitude() + ")");
			double distanceToItem = userLocation.distanceInMilesTo(coord);
			NumberFormat df = DecimalFormat.getInstance();
			df.setMinimumFractionDigits(1);
			df.setMaximumFractionDigits(1);
			df.setRoundingMode(RoundingMode.UP);
            // TODO: support internationalization
			holder.tvDistance.setText("Approximately " + df.format(distanceToItem) + " miles");
		} else {
		    // need to clear out the coordinate text if item has no location
		    // otherwise might print coordinates from a previously-displayed item entry
			holder.tvCoordinates.setText(null);
			// TODO: support internationalization
			holder.tvDistance.setText("Distance unavailable");
		}

		return v;
	}
}
