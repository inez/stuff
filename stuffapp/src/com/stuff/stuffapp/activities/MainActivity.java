package com.stuff.stuffapp.activities;

import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;

import com.parse.ParseGeoPoint;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.fragments.AddFragment;
import com.stuff.stuffapp.fragments.AddFragment.OnItemAddedListener;
import com.stuff.stuffapp.fragments.DetailsFragment;
import com.stuff.stuffapp.fragments.HomeFragment;
import com.stuff.stuffapp.fragments.SearchFragment;
import com.stuff.stuffapp.fragments.HomeFragment.OnItemClickedListener;
import com.stuff.stuffapp.fragments.MainFragment;
import com.stuff.stuffapp.fragments.ProfileFragment;
import com.stuff.stuffapp.helpers.Ids;
import com.stuff.stuffapp.models.Item;

public class MainActivity extends FragmentActivity implements OnItemClickedListener, OnItemAddedListener {
	private static final String TAG = "MainActivity";
	private static final int FIVE_MINUTES = 10 * 60 * 1000;
	private static final int LOCATION_UPDATE_INTERVAL = FIVE_MINUTES;

	private SparseArray<Fragment> fragments;
	
	private int currentFragmentId;

	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location lastKnownLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            
            @Override
            public void onProviderEnabled(String provider) {
            }
            
            @Override
            public void onProviderDisabled(String provider) {
            }
            
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG + ".LocationListener", "Got location (" + location.getLatitude() + ", " + location.getLongitude() + ")");
                if ( isBetterLocation(location, lastKnownLocation) ) {
                    lastKnownLocation = location;
                    Log.d(TAG + ".LocationListener", "Updating with better location");
                }
            }
        };

        if (savedInstanceState == null) {
        	fragments = new SparseArray<Fragment>();
			displayFragment(Ids.HOME);
        }
	}

	/**
	 * Register location listener when the activity comes online
	 */
	@Override
	protected void onResume() {
	    super.onResume();

	    // select the right location provider
	    // TODO: find out about using LocationManager.PASSIVE_PROVIDER instead
	    Criteria criteria = new Criteria();
	    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
	    criteria.setPowerRequirement(Criteria.POWER_LOW);
	    String locationProvider = locationManager.getBestProvider(criteria, true);
	    assert !locationProvider.isEmpty();

	    if ( null == lastKnownLocation ) {
	        lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
	        Log.d(TAG, "Initialized last known location (" + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude() + ")");
	    }

	    // get location updates every 5 minutes
	    locationManager.requestLocationUpdates(locationProvider, FIVE_MINUTES, 0, locationListener);
        Log.d(TAG, "Registered listener for location updates every 5 minutes");
	};

	/**
	 * Remove location listener to save power
	 */
	@Override
	protected void onPause() {
	    super.onPause();

	    locationManager.removeUpdates(locationListener);
        Log.d(TAG, "Turned off location updates");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void onMenuItemClick(View view) {
		int fragmentId = 0;

		switch ( view.getId() ) {
			case R.id.iv_home:
				fragmentId = Ids.HOME;
				break;
			case R.id.iv_search:
				fragmentId = Ids.SEARCH;
				break;
			case R.id.iv_add:
				fragmentId = Ids.ADD;
				break;
			case R.id.iv_message:
				fragmentId = Ids.MESSAGE;
				break;
			case R.id.iv_profile:
				fragmentId = Ids.PROFILE;
				break;
		}
		
		if ( fragmentId == currentFragmentId ) {
			return;
		}

		if(fragmentId != 0) {
			displayFragment(fragmentId);
		}
	}
	
	private View current;
	
	private void displayFragment(int fragmentId) {
		Log.d(TAG, "displayFragment: " + String.valueOf(fragmentId));

		Fragment fragment = fragments.get(fragmentId);
		View v = null;

		if ( fragment == null ) {
			switch(fragmentId) {
				case Ids.HOME:
					fragment = HomeFragment.newInstance();
					break;
				case Ids.SEARCH:
					fragment = SearchFragment.newInstance();
					break;
				case Ids.ADD:
					fragment = AddFragment.newInstance();
					break;
				case Ids.MESSAGE:
					fragment = MainFragment.newInstance(String.valueOf(fragmentId));
					break;
				case Ids.PROFILE:
					fragment = ProfileFragment.newInstance();
					break;
			}
			fragments.append(fragmentId, fragment);
		}

		switch(fragmentId) {
			case Ids.HOME:
				v = findViewById(R.id.iv_home);
				break;
			case Ids.SEARCH:
				v = findViewById(R.id.iv_search);
				break;
			case Ids.ADD:
				v = findViewById(R.id.iv_add);
				break;
			case Ids.MESSAGE:
				v = findViewById(R.id.iv_message);
				break;
			case Ids.PROFILE:
				v = findViewById(R.id.iv_profile);
				break;
		}

		
		if(v != null) {
			v.setBackgroundColor(Color.parseColor("#cccccc"));
			if(current != null) {
				current.setBackgroundColor(Color.TRANSPARENT);
			}
			current = v;
		}

		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fl_container, fragment);
		ft.commit();

		currentFragmentId = fragmentId;
	}

	@Override
	public void onItemClicked(Item item) {
		Log.d(TAG, "onItemClicked: " + item.getName());
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.fl_container, DetailsFragment.newInstance(item));
		ft.addToBackStack("details");
		ft.commit();
	}

	@Override
	public void onItemAdded(Item item) {
		Log.d(TAG, "onItemAdded: " + item.getName());

		fragments.remove(Ids.ADD);
		displayFragment(Ids.HOME);
	}

	/**
	 * Determines whether or not one Location reading is better than the current Location fix
	 * @param location  The new Location that you want to evaluate
	 * @param currentBestLocation  The current Location fix, to which you want to compare the new one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if ( currentBestLocation == null ) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > LOCATION_UPDATE_INTERVAL;
	    boolean isSignificantlyOlder = timeDelta < -LOCATION_UPDATE_INTERVAL;
	    boolean isNewer = timeDelta > 0;

	    // If current location is older than the update interval, use the new location
	    // because the user has likely moved
	    if ( isSignificantlyNewer ) {
	        return true;
	    }
        // If the new location is older, it must be worse
	    else if ( isSignificantlyOlder ) {
	        return false;
	    }

	    // Check if the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if ( isMoreAccurate ) {
	        return true;
	    }
	    else if ( isNewer && !isLessAccurate ) {
	        return true;
	    }
	    else if ( isNewer && !isSignificantlyLessAccurate && isFromSameProvider ) {
	        return true;
	    }
	    return false;
	}

	/**
	 * Checks if two providers are the same
	 */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) return provider2 == null;
	    return provider1.equals(provider2);
	}

	/**
	 * Returns last known location (e.g., to a fragment) as a ParseGeoPoint
	 */
	public ParseGeoPoint getLastKnownLocation() {
	    return new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
	}
}
