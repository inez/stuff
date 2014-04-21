package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.parse.ParseImageView;
import com.stuff.stuffapp.R;
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

        ParseImageView iv_photo = (ParseImageView) view.findViewById(R.id.iv_photo);
        ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.displayImage(item.getPhotoFile().getUrl(), iv_photo);

        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(item.getName());

        TextView tv_desc = (TextView) view.findViewById(R.id.tv_desc);
        tv_desc.setText(item.getDescription());

        TextView tv_owner = (TextView) view.findViewById(R.id.tv_owner);
        tv_owner.setText(Helper.getUserName(item.getOwner()));
        
        Button button = (Button)view.findViewById(R.id.btContact);
        button.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				((OnItemClickedListener) getActivity()).onMessageCompose(item);
			}
        	
        });

        return view;
	}

}
