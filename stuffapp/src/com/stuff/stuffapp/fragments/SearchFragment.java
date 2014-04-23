package com.stuff.stuffapp.fragments;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.adapters.SearchAdapter;
import com.stuff.stuffapp.models.Item;

public class SearchFragment extends Fragment {

	public interface OnItemClickedListener {
        public void onItemClicked(Item item);
    }
	
	private static String TAG = "SearchFragment";
	
	private View view;
	private ListView lvSearch;
	private Button btSearch;
	private EditText etQuery;
	private SupportMapFragment mapFrag;
	
	private SearchAdapter adapter;
	private List<Item> items;
	
	public static SearchFragment newInstance() {
		SearchFragment fragment = new SearchFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_search, container, false);
        
        lvSearch = (ListView) view.findViewById(R.id.lvSearch);
        btSearch = (Button) view.findViewById(R.id.btSearch);
        etQuery = (EditText) view.findViewById(R.id.etQuery);

        // initialize map
        if ( null == mapFrag )
            mapFrag = (SupportMapFragment) this.getFragmentManager().findFragmentById(R.id.fragMap);
        // enable current location "blue dot" 
        mapFrag.getMap().setMyLocationEnabled(true);
        // center at current location
        if ( ((MainActivity) this.getActivity()).hasLastKnownLocation() ) {
            ParseGeoPoint loc = ((MainActivity) this.getActivity()).getLastKnownLocation();
            LatLng center = new LatLng(loc.getLatitude(), loc.getLongitude());
            mapFrag.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10));
        }

        btSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String query = etQuery.getText().toString().trim();
				if ( query.length() < 1 ) {
					Toast.makeText(getActivity(), "Search query must be at least 1 characters", Toast.LENGTH_LONG).show();
					return;
				}
				getSearchResults(query, new FindCallback<Item>() {
					@Override
					public void done(List<Item> arg0, ParseException arg1) {
						if(arg0 != null) {
							Log.d(TAG, "Got results");
							adapter.clear();
							adapter.addAll(arg0);
						}
					}
				});
			}
		});
        
        if ( items == null ) {
        	items = new ArrayList<Item>();
        }
        if ( adapter == null ) {
        	adapter = new SearchAdapter(getActivity(), items);
        }

        lvSearch.setAdapter(adapter);

		return view;
    }
	
	private void getSearchResults(String query, FindCallback<Item> callback) {
		ParseQuery<Item> query1 = new ParseQuery<Item>(Item.class);
		query1.whereContains("searchable", query.toLowerCase());
		query1.findInBackground(callback);
		/*
        ParseQuery<Item> query1 = new ParseQuery<Item>(Item.class);
        query1.whereContains("name", query);

        ParseQuery<Item> query2 = new ParseQuery<Item>(Item.class);
        query2.whereContains("description", query);
        
        List<ParseQuery<Item>> queries = new ArrayList<ParseQuery<Item>>();
        queries.add(query1);
        queries.add(query2);

        ParseQuery.or(queries).findInBackground(callback);
        */
	}

}
