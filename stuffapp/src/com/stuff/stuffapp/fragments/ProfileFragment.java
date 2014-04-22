package com.stuff.stuffapp.fragments;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stuff.stuffapp.R;
import com.viewpagerindicator.UnderlinePageIndicator;

public class ProfileFragment extends Fragment {
	private static final String TAG = "ProfileFragment";

	// begin bug fix for ViewPager within fragment
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

	private View view;
    private ViewPager viewPager;
	private ProfilePagerAdapter pagerAdapter;

	// temporary
	private AboutMeFragment firstProfileFrag;
	private MyItemsFragment secondProfileFrag;

	public static ProfileFragment newInstance() {
		ProfileFragment fragment = new ProfileFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        // create view pager (but don't bind adapter until activity is available)
        viewPager = (ViewPager) view.findViewById(R.id.profile_view_pager);

        pagerAdapter = new ProfilePagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // bind the title indicator to the view pager
        UnderlinePageIndicator underlinePageIndicator = (UnderlinePageIndicator) view.findViewById(R.id.profile_titles);
        underlinePageIndicator.setViewPager(viewPager);
        underlinePageIndicator.setFades(false);
        underlinePageIndicator.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }
            
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // temporary
        if ( firstProfileFrag==null ) firstProfileFrag = new AboutMeFragment();
        if ( secondProfileFrag==null ) secondProfileFrag = new MyItemsFragment();

        return view;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	}

	/**
	 * Internal adapter class for ViewPager 
	 */
	public class ProfilePagerAdapter extends FragmentPagerAdapter {
        public ProfilePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if ( position == 0 )
                return firstProfileFrag;
            else if ( position == 1 )
                return secondProfileFrag;
            else return firstProfileFrag;
        }

        @Override
        public int getCount() {
            return 2;
        }
	}
}
