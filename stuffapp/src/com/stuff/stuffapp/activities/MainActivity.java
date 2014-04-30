package com.stuff.stuffapp.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.fragments.AddFragment;
import com.stuff.stuffapp.fragments.AddFragment.OnItemAddedListener;
import com.stuff.stuffapp.fragments.ConversationsFragment;
import com.stuff.stuffapp.fragments.DetailsFragment;
import com.stuff.stuffapp.fragments.HomeFragment;
import com.stuff.stuffapp.fragments.HomeFragment.OnItemClickedListener;
import com.stuff.stuffapp.fragments.MessagesFragment;
import com.stuff.stuffapp.fragments.ProfileFragment;
import com.stuff.stuffapp.fragments.SearchFragment;
import com.stuff.stuffapp.fragments.SearchFragment.OnSearchViewChangedListener;
import com.stuff.stuffapp.helpers.Ids;
import com.stuff.stuffapp.models.Conversation;
import com.stuff.stuffapp.models.Item;

public class MainActivity extends FragmentActivity implements OnItemClickedListener, OnItemAddedListener, OnSearchViewChangedListener {

	private static final String TAG = "MainActivity";

	private static final int FIVE_MINUTES = 10 * 60 * 1000;
	private static final int LOCATION_UPDATE_INTERVAL = FIVE_MINUTES;

	private SparseArray<Fragment> fragments;

	private int currentFragmentId;
	private int mCurrentSearchViewId;

	private LocationManager locationManager;
	private LocationListener locationListener;
	private Location lastKnownLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String conversationId = null;
		String parseNotificationData = getIntent().getStringExtra("com.parse.Data");

		if (parseNotificationData != null) {
			try {
				JSONObject parseNotificationJson = new JSONObject(parseNotificationData);
				conversationId = parseNotificationJson.getString("conversation_id");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ParseAnalytics.trackAppOpened(getIntent());

		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onProviderDisabled(String provider) {}

			@Override
			public void onLocationChanged(Location location) {
				Log.d(TAG + ".LocationListener",
						"Got location (" + location.getLatitude() + ", " + location.getLongitude() + ")");
				if (null == lastKnownLocation && isBetterLocation(location, lastKnownLocation)) {
					lastKnownLocation = location;
					Log.d(TAG + ".LocationListener", "Updating with better location");
				}
			}
		};

		if (savedInstanceState == null) {
			fragments = new SparseArray<Fragment>();
			if (conversationId != null) {
				ParseQuery<Conversation> query = new ParseQuery<Conversation>(Conversation.class);
				Conversation conversation = null;
				try {
					conversation = query.get(conversationId);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// We could display the conversations fragment.
				// For now just displaying message fragment.
				displayFragment(Ids.CONVERSATIONS, conversation);
			}
			else {
				displayFragment(Ids.HOME);
			}
		}
	}

	/**
	 * Register location listener when the activity comes online
	 */
	@Override
	protected void onResume() {
		super.onResume();

		String channelName = ParseUser.getCurrentUser().getUsername();
		if(channelName !=null) {
			PushService.subscribe(this, channelName, MainActivity.class);
		}
		else {
			Toast.makeText(this, "Unable to subscribe to null channel", Toast.LENGTH_SHORT).show();
		}
		

		// select the right location provider
		// TODO: check for location permissions, handle error case
		// TODO: find out about using LocationManager.PASSIVE_PROVIDER instead
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		Log.d(TAG, "Searching for best location provider");
		String locationProvider = locationManager.getBestProvider(criteria, true);
		if (locationProvider == null || locationProvider.isEmpty())
			Log.e(TAG, "Could not get location provider");
		else
			Log.d(TAG, "Got provider " + locationProvider);
		assert !locationProvider.isEmpty();

		if (null == lastKnownLocation) {
			Log.d(TAG, "Getting last known location from " + locationProvider);
			lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
			if (lastKnownLocation == null)
				Log.d(TAG, "There is no last known location");
			else
				Log.d(TAG, "Initialized last known location (" + lastKnownLocation.getLatitude() + ", "
						+ lastKnownLocation.getLongitude() + ")");
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
	
	private MenuItem itemSearch;
	private MenuItem miSearchToggle;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		itemSearch = (MenuItem) menu.findItem(R.id.itemSearch);
		final SearchView searchView = (SearchView) itemSearch.getActionView();
		searchView.setQueryHint("Enter keywords");
		searchView.setIconifiedByDefault(false);
		//Point p = new Point();
        //getWindowManager().getDefaultDisplay().getSize(p);
        //LayoutParams params = new LayoutParams(p.x, LayoutParams.MATCH_PARENT);
        //searchView.setLayoutParams(params);
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				Log.d(TAG, "SearchView: " + query);
				SearchFragment searchFragment = (SearchFragment) fragments.get(Ids.SEARCH);
				searchFragment.search(query);;
				searchView.clearFocus();
				return false;
			}
			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});

		miSearchToggle = (MenuItem) menu.findItem(R.id.miSearchToggle);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if ( item == miSearchToggle ) {
	        // toggle the currently selected search view on the underlying search fragment
	        SearchFragment searchFrag = (SearchFragment) fragments.get(Ids.SEARCH); 
	        if ( null != searchFrag ) {
	            int newPage = (mCurrentSearchViewId + 1) % 2;
	            searchFrag.changeSearchView(newPage);
	            changeSearchViewIcon(newPage);
	        }
	    }
	    return true;
	}

	public void onMenuItemClick(View view) {
		int fragmentId = 0;

		switch (view.getId()) {
		case R.id.ivHome:
			fragmentId = Ids.HOME;
			break;
		case R.id.ivSearch:
			fragmentId = Ids.SEARCH;
			break;
		case R.id.ivAdd:
			fragmentId = Ids.ADD;
			break;
		case R.id.ivMessage:
			fragmentId = Ids.MESSAGE;
			break;
		case R.id.ivProfile:
			fragmentId = Ids.PROFILE;
			break;

		}

		if (fragmentId == currentFragmentId) {
			return;
		}

		if (fragmentId != 0) {
			displayFragment(fragmentId);
		}
	}

	private void displayFragment(int fragmentId) {
		displayFragment(fragmentId, null);
	}

	private void displayFragment(int fragmentId, Conversation conversation) {
		Log.d(TAG, "displayFragment: " + String.valueOf(fragmentId));

		Fragment fragment = fragments.get(fragmentId);

		if (fragment == null) {
			switch (fragmentId) {
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
				fragment = MessagesFragment.newInstance();
				break;
			case Ids.PROFILE:
				fragment = ProfileFragment.newInstance();
				break;
			case Ids.CONVERSATIONS:
				// Load the conversation based on the query.
				fragment = ConversationsFragment.newInstance(conversation);
				break;
			}
			fragments.append(fragmentId, fragment);
		}
				
		ActionBar ab = getActionBar();
		ImageView v;

		if ( itemSearch != null ) {
			itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			miSearchToggle.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			ab.setDisplayShowHomeEnabled(true);
		}
		
		v = (ImageView) findViewById(R.id.ivHome);
		if ( fragmentId == Ids.HOME ) {
			v.setImageResource(R.drawable.ic_home_active);
			ab.setLogo(R.drawable.stuff);
			ab.setTitle("");
		} else {
			v.setImageResource(R.drawable.ic_home);
		}

		v = (ImageView) findViewById(R.id.ivSearch);
		if ( fragmentId == Ids.SEARCH ) {
			v.setImageResource(R.drawable.ic_search_active);
			if ( itemSearch != null ) {
                miSearchToggle.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
				itemSearch.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
				ab.setDisplayShowHomeEnabled(false);
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
			}
		} else {
			v.setImageResource(R.drawable.ic_search);
		}

		v = (ImageView) findViewById(R.id.ivAdd);
		if ( fragmentId == Ids.ADD ) {
			v.setImageResource(R.drawable.ic_add_active);
			ab.setLogo(R.drawable.ic_launcher);
			ab.setTitle("Add stuff");
		} else {
			v.setImageResource(R.drawable.ic_add);
		}

		v = (ImageView) findViewById(R.id.ivMessage);
		if ( fragmentId == Ids.MESSAGE ) {
			v.setImageResource(R.drawable.ic_message_active);
			ab.setLogo(R.drawable.ic_launcher);
			ab.setTitle("Messages");
		} else {
			v.setImageResource(R.drawable.ic_message);
		}

		v = (ImageView) findViewById(R.id.ivProfile);
		if ( fragmentId == Ids.PROFILE ) {
			v.setImageResource(R.drawable.ic_profile_active);
			ab.setLogo(R.drawable.ic_launcher);
			ab.setTitle("Profile");
		} else {
			v.setImageResource(R.drawable.ic_profile);
		}

		FragmentManager fm = getSupportFragmentManager();
		fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.flContainer, fragment);
		ft.commit();

		currentFragmentId = fragmentId;
	}

	@Override
	public void onItemClicked(Item item) {
		Log.d(TAG, "onItemClicked: " + item.getName());

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, DetailsFragment.newInstance(item));
		ft.addToBackStack("details");
		ft.commit();
	}

	@Override
	public void onItemAdded(Item item) {
		Log.d(TAG, "onItemAdded: " + item.getName());

		fragments.remove(Ids.ADD);
		displayFragment(Ids.HOME);
	}

	@Override
	public void onMessageCompose(Conversation conversation) {
		Log.d(TAG, "onMessageCompose: " + conversation);

		// FragmentTransaction ft =
		// getSupportFragmentManager().beginTransaction();
		// ft.replace(R.id.flContainer,
		// MessageComposeFragment.newInstance(item));
		// ft.addToBackStack("messageCompose");
		// ft.commit();

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.flContainer, ConversationsFragment.newInstance(conversation));
		ft.addToBackStack("messageCompose");
		ft.commit();

	}

	/**
	 * Determines whether or not one Location reading is better than the current
	 * Location fix
	 * 
	 * @param location
	 *            The new Location that you want to evaluate
	 * @param currentBestLocation
	 *            The current Location fix, to which you want to compare the new
	 *            one
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > LOCATION_UPDATE_INTERVAL;
		boolean isSignificantlyOlder = timeDelta < -LOCATION_UPDATE_INTERVAL;
		boolean isNewer = timeDelta > 0;

		// If current location is older than the update interval, use the new
		// location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
		}
		// If the new location is older, it must be worse
		else if (isSignificantlyOlder) {
			return false;
		}

		// Check if the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if two providers are the same
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null)
			return provider2 == null;
		return provider1.equals(provider2);
	}

	/**
	 * Returns last known location (e.g., to a fragment) as a ParseGeoPoint
	 */
	public ParseGeoPoint getLastKnownLocation() {
		if (null != lastKnownLocation)
			return new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
		else
			// TODO: deal with no last known location!
			return null;
	}

	public boolean hasLastKnownLocation() {
		return null != lastKnownLocation;
	}

	/**
	 * Tracks changes when underlying search fragment toggles between list and map views
	 */
    @Override
    public void onChangeView(int position) {
        mCurrentSearchViewId = position;
        changeSearchViewIcon(mCurrentSearchViewId);
    }

    /**
     * Sets the appropriate icon for the search toggle button.
     * For list view, the icon should be a map; for map view, the icon should be a list.
     */
    private void changeSearchViewIcon(int position) {
        if ( null != miSearchToggle ) {
            if ( position == 0 )
                miSearchToggle.setIcon(R.drawable.ic_map_view);
            else
                miSearchToggle.setIcon(R.drawable.ic_list_view);
        }
    }
}
