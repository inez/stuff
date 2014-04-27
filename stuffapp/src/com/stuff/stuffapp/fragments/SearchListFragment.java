package com.stuff.stuffapp.fragments;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.models.Item;

public class SearchListFragment extends Fragment {

	private static String TAG = "SearchListFragment";

	private View view;

	public static SearchListFragment newInstance() {
		SearchListFragment fragment = new SearchListFragment();
		return fragment;
	}

	private SearchListAdapter searchListAdapter;

	private List<Item> items;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");

		view = inflater.inflate(R.layout.fragment_home, container, false);

		if (savedInstanceState == null) {
			items = new ArrayList<Item>();
			if (searchListAdapter == null) {
				searchListAdapter = new SearchListAdapter(getActivity(), items);
			}
			ListView lvHome = (ListView) view.findViewById(R.id.lvHome);
			lvHome.setAdapter(searchListAdapter);
		}

		return view;
	}

	public void displayResults(List<Item> results) {
		Log.d(TAG, "displayResults, size: " + results.size());
		searchListAdapter.clear();
		searchListAdapter.addAll(results);
	}

	static class ViewHolder {
		ParseImageView ivItemPicture;
		TextView tvName, tvDistanceAndTime;
	}

	private class SearchListAdapter extends ArrayAdapter<Item> {

		public SearchListAdapter(Context context, List<Item> items) {
			super(context, 0, items);
		}

		public View getView(int position, View v, ViewGroup parent) {
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

			Item item = getItem(position);

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
			ParseGeoPoint userLocation = ((MainActivity) getActivity()).getLastKnownLocation();
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

}
