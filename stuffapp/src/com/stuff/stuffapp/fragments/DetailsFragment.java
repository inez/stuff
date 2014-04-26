package com.stuff.stuffapp.fragments;

import org.json.JSONException;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.models.Item;

public class DetailsFragment extends Fragment {

	private static String TAG = "DetailsFragment";

	private View view;

	private Item item;

	public static DetailsFragment newInstance(Item item) {
		DetailsFragment fragment = new DetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("item", item);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		item = (Item) args.getSerializable("item");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView: " + item.getName());
		view = inflater.inflate(R.layout.fragment_details, container, false);

		//
		// tvName
		//
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		tvName.setText(item.getName());

		//
		// ivItemPicture
		//
		ParseImageView ivItemPicture = (ParseImageView) view.findViewById(R.id.ivItemPicture);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(item.getPhotoFile().getUrl(), ivItemPicture);

		//
		// tvDistance
		//
		ParseGeoPoint itemLocation = item.getLocation();
		ParseGeoPoint userLocation = ((MainActivity) getActivity()).getLastKnownLocation();
		TextView tvDistance = (TextView) view.findViewById(R.id.tvDistance);
		if (itemLocation != null && userLocation != null) {
			double distanceToItem = userLocation.distanceInMilesTo(itemLocation);
			tvDistance.setText(String.format("%.1f miles", distanceToItem));
		}

		//
		// tvTime
		//
		TextView tvTime = (TextView) view.findViewById(R.id.tvTime);
		tvTime.setText((new PrettyTime()).format(item.getCreatedAt()));

		//
		// tvDescription
		//
		TextView tvDescription = (TextView) view.findViewById(R.id.tvDescription);
		String description = item.getDescription();
		if (description.trim().length() == 0) {
			description = "<i>No description</i>";
		}
		tvDescription.setText(Html.fromHtml(description));

		JSONObject profileData = item.getOwner().getJSONObject("profile");

		//
		// ivProfilePicture
		//
		ImageView ivProfilePicture = (ImageView) view.findViewById(R.id.ivProfilePicture);
		try {
			imageLoader.displayImage("http://graph.facebook.com/" + profileData.get("facebookId").toString()
					+ "/picture?type=normal", ivProfilePicture);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//
		// tvNameAndLocation
		//
		TextView tvNameAndLocation = (TextView) view.findViewById(R.id.tvNameAndLocation);
		String name = null, location = null;

		try {
			name = profileData.get("name").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		try {
			location = profileData.get("location").toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}

		tvNameAndLocation.setText(name + (location != null ? "\n" + location : ""));

		return view;
	}

}
