package com.stuff.stuffapp.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Item;

public class SearchMapFragment extends Fragment {

	private static String TAG = "SearchMapFragment";

	// BEGIN BUG FIX for fragment within fragment
	// http://stackoverflow.com/questions/14929907/causing-a-java-illegalstateexception-error-no-activity-only-when-navigating-to
	// https://code.google.com/p/android/issues/detail?id=42601
	private static final Field sChildFragmentManagerField;
	static {
		Field f = null;
		try {
			f = Fragment.class.getDeclaredField("mChildFragmentManager");
			f.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "Error getting mChildFragmentManager field", e);
		}
		sChildFragmentManagerField = f;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		if (sChildFragmentManagerField != null) {
			try {
				sChildFragmentManagerField.set(this, null);
			} catch (Exception e) {
				Log.e(TAG, "Error setting mChildFragmentManager field", e);
			}
		}
	}

	// END BUG FIX

	private View view;

	private ResultFragmentsPagerAdapter adapter;

	private SlidingUpPanelLayout searchSlidingLayout;

	private List<Item> results;

	private ViewPager vpResults;

	public static SearchMapFragment newInstance() {
		SearchMapFragment fragment = new SearchMapFragment();
		return fragment;
	}

	private SupportMapFragment mapFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");

		view = inflater.inflate(R.layout.fragment_map_search, container, false);
		searchSlidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.searchSlidingLayout);

		FragmentManager fm = getChildFragmentManager();
		mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.flMap);
		if (mapFragment == null) {
			mapFragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.flMap, mapFragment).commit();

		}

		vpResults = (ViewPager) view.findViewById(R.id.vpResults);
		adapter = new ResultFragmentsPagerAdapter(getChildFragmentManager());
		vpResults.setAdapter(adapter);

		return view;
	}

	private void hideSlidingPanel() {
		if (searchSlidingLayout != null) {
			searchSlidingLayout.getChildAt(1).setVisibility(View.GONE);
		}
	}

	private void showSlidingPanel() {
		if (searchSlidingLayout != null) {
			searchSlidingLayout.getChildAt(1).setVisibility(View.VISIBLE);
		}
	}

	private List<Marker> markers = new ArrayList<Marker>();

	public void displayResults(List<Item> results) {
		for (Marker marker : markers) {
			marker.remove();
		}
		markers.clear();

		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (Item item : results) {
			if (item.getLocation() != null) {
				// instantiate marker with default icon
				Marker marker = mapFragment.getMap().addMarker(
						new MarkerOptions().position(
								new LatLng(item.getLocation().getLatitude(), item.getLocation().getLongitude())).title(
								item.getName()));
				// asynchronously load the item's thumbnail image and set icon
				// when loaded
				item.getPhotoFile100().getDataInBackground(new MarkerGetDataCallback(marker));
				markers.add(marker);
				builder.include(marker.getPosition());
			}
		}

		if (markers.size() > 0) {
			LatLngBounds bounds = builder.build();
			int padding = 150; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			mapFragment.getMap().animateCamera(cu);
		}

		this.results = results;

		vpResults = (ViewPager) view.findViewById(R.id.vpResults);
		adapter = new ResultFragmentsPagerAdapter(getChildFragmentManager());
		Log.d(TAG, "Created new pager adapter");
		vpResults.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		vpResults.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				Log.d(TAG, "onPageSelected: " + String.valueOf(position));
				markers.get(position).showInfoWindow();
				CameraUpdate cu = CameraUpdateFactory.newLatLng(markers.get(position).getPosition());
				mapFragment.getMap().animateCamera(cu);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		searchSlidingLayout.collapsePane();
	}

	// extends FragmentStatePagerAdapter instead of FragmentPagerAdapter to
	// refresh
	// item detail page when conducting new search
	// This causes app crash due to bug in support library:
	// https://code.google.com/p/android/issues/detail?id=37484
	// TODO: monitor when temporary work-around is no longer needed
	private class ResultFragmentsPagerAdapter extends FragmentStatePagerAdapter {
		public ResultFragmentsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// TODO: Perhaps DetailsFragment could be cached locally with item
			// id as a cache key
			return DetailsFragment.newInstance(results.get(position));
		}

		@Override
		public int getCount() {
			return results == null ? 0 : results.size();
		}

		// temporary workaround -- this is probably not the right solution
		// http://stackoverflow.com/questions/11097091/android-app-crashing-after-a-while-using-fragments-and-viewpager
		@Override
		public Parcelable saveState() {
			// do nothing
			return null;
		}
	}

	private class MarkerGetDataCallback extends GetDataCallback {
		private Marker mMarker;

		public MarkerGetDataCallback(Marker m) {
			super();
			mMarker = m;
		}

		@Override
		public void done(byte[] data, ParseException e) {
			Bitmap thumbnail = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (null != mMarker) {
				mMarker.setIcon(BitmapDescriptorFactory.fromBitmap(thumbnail));
			}
		}
	}
}
