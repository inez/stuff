package com.stuff.stuffapp.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
	
    // begin bug fix for map within fragment
    // http://stackoverflow.com/questions/14929907/causing-a-java-illegalstateexception-error-no-activity-only-when-navigating-to
    // https://code.google.com/p/android/issues/detail?id=42601
    private static final Field sChildFragmentManagerField;
    static {
        Field f = null;
        try {
            f = Fragment.class.getDeclaredField("mChildFragmentManager");
            f.setAccessible(true);
        }
        catch (NoSuchFieldException e) {
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
    // end bug fix

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

        // dynamically create map fragment
        FragmentManager fm = this.getChildFragmentManager();
        mapFrag = (SupportMapFragment) fm.findFragmentById(R.id.flMap);
        if ( null == mapFrag ) {
            mapFrag = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.flMap, mapFrag).commit();
        }
        // NOTE: SupportMapFragment is initialized in onCreateView, but its map
        // is not available until later in the life-cycle. For now, we wait
        // until onResume.
        // The map is safely available inside SupportMapFragment.onCreateView
        // and Stack Overflow suggests sub-classing SupportMapFragment to
        // call a made-up callback (e.g., onMapReady()) to the Search fragment
        // in the sub-class's onCreateView.
        
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
	    super.onResume();
	    setUpMap();
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

	private void setUpMap() {
        if ( null != mapFrag && null != mapFrag.getMap() ) {
            // enable current location "blue dot" 
            mapFrag.getMap().setMyLocationEnabled(true);
            // center at current location
            if ( ((MainActivity) this.getActivity()).hasLastKnownLocation() ) {
                ParseGeoPoint loc = ((MainActivity) this.getActivity()).getLastKnownLocation();
                LatLng center = new LatLng(loc.getLatitude(), loc.getLongitude());
                mapFrag.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
            }
        }
        else {
            Toast.makeText(getActivity(), "Uh oh, could not get map", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "map is null in onResume");
        }
	}
}
