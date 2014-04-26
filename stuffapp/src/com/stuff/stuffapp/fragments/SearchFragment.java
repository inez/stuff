package com.stuff.stuffapp.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
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
    private GoogleMap mMap;
	
    private SearchAdapter adapter;
    private List<Item> items;

    private SearchPagerAdapter mPagerAdapter;
    ViewPager mPager;

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
        
        btSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = etQuery.getText().toString().trim();
                if ( query.length() < 1 ) {
                    Toast.makeText(getActivity(), "Search query must be at least 1 characters", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.d(TAG, "Searching for items");
                getSearchResults(query, new FindCallback<Item>() {
                    @Override
                    public void done(List<Item> objects, ParseException e) {
                        if(objects != null) {
                            Log.d(TAG, "Got " + objects.size() + " results");
                            adapter.clear();
                            adapter.addAll(objects);
                            // view pager
                            mPagerAdapter = new SearchPagerAdapter(SearchFragment.this.getChildFragmentManager(), objects);
                            mPager.setAdapter(mPagerAdapter);
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

        // search results view pager
        if ( null == mPagerAdapter ) mPagerAdapter = new SearchPagerAdapter(this.getChildFragmentManager(), new ArrayList<Item>());
        mPager = (ViewPager) view.findViewById(R.id.vpSearchResults);
        mPager.setAdapter(mPagerAdapter);

        // begin temporary
        OnClickListener arrowListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( R.id.btnArrowIndicatorLeft == v.getId() ) {
                    Toast.makeText(getActivity(), "Tap left", Toast.LENGTH_SHORT).show();
                }
                else if ( R.id.btnArrowIndicatorRight == v.getId() ) {
                    Toast.makeText(getActivity(), "Tap right", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "Unrecognized arrow", Toast.LENGTH_SHORT).show();
                }
            }
        };
        Button btnArrowLeft = (Button) view.findViewById(R.id.btnArrowIndicatorLeft);
        btnArrowLeft.setOnClickListener(arrowListener);
        Button btnArrowRight = (Button) view.findViewById(R.id.btnArrowIndicatorRight);
        btnArrowRight.setOnClickListener(arrowListener);
        // end temporary

        // sliding panel stuff
        final SlidingUpPanelLayout slidingLayout = (SlidingUpPanelLayout) view.findViewById(R.id.searchSlidingLayout);
        final TextView slidingHeader = (TextView) view.findViewById(R.id.tvSlider);
        slidingLayout.setDragView(slidingHeader);
        
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        // dynamically create map fragment
        if ( null == mapFrag ) {
            Log.i(TAG, "Re-creating map fragment");
            mapFrag = SupportMapFragment.newInstance();

            // NOTE: SupportMapFragment is initialized in onCreateView, but its map
            // is not available until later in the life-cycle. For now, we wait
            // until onResume to get or manipulate the map object.
            // Technically, the map is ready inside SupportMapFragment.onCreateView
            // and Stack Overflow suggests sub-classing SupportMapFragment to call
            // a made-up callback (e.g., onMapReady()) to the Search fragment in
            // the sub-class's onCreateView.
        }
        FragmentManager fm = this.getChildFragmentManager();
        fm.beginTransaction().replace(R.id.flMap, mapFrag).commit();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        setUpMapIfNeeded();
    }

    private void getSearchResults(String query, FindCallback<Item> callback) {
        ParseQuery<Item> query1 = new ParseQuery<Item>(Item.class);
        query1.whereContains("searchable", query.toLowerCase());
        query1.include("owner");
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

    private void setUpMapIfNeeded() {
        if ( null == mMap ) {
            mMap = mapFrag.getMap();
            assert mMap != null;
            setUpMap();
        }
    }

    private void setUpMap() {
        Log.i(TAG, "Setting up map");

        // enable current location "blue dot" 
        mMap.setMyLocationEnabled(true);
        // center at current location
        if ( ((MainActivity) this.getActivity()).hasLastKnownLocation() ) {
            ParseGeoPoint loc = ((MainActivity) this.getActivity()).getLastKnownLocation();
            LatLng center = new LatLng(loc.getLatitude(), loc.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
        }
    }

    public static class SearchPagerAdapter extends FragmentStatePagerAdapter {
        private List<Item> mSearchResults;

        public SearchPagerAdapter(FragmentManager fm, List<Item> searchResults) {
            super(fm);
            this.mSearchResults = searchResults;
        }

        @Override
        public int getCount() {
            return mSearchResults.size();
        }

        @Override
        public Fragment getItem(int position) {
            // TODO: optimize this to cache fragments
            return DetailsFragment.newInstance(mSearchResults.get(position));
        }
    }
}
