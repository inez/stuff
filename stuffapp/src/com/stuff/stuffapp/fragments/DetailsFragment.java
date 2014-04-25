package com.stuff.stuffapp.fragments;

import org.ocpsoft.prettytime.PrettyTime;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.fragments.HomeFragment.OnItemClickedListener;
import com.stuff.stuffapp.helpers.Helper;
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
        
        ParseImageView ivItemPicture = (ParseImageView) view.findViewById(R.id.ivItemPicture);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(item.getPhotoFile().getUrl(), ivItemPicture);
        
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        tvName.setText(item.getName());
        
        
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

        return view;
	}

}
