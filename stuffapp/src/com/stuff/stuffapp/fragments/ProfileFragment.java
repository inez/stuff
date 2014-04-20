package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stuff.stuffapp.R;

public class ProfileFragment extends Fragment {
	private static final String TAG = "ProfileFragment";
	
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

        viewPager = (ViewPager) view.findViewById(R.id.profile_view_pager);
        // temporary
        firstProfileFrag = new AboutMeFragment();
        secondProfileFrag = new MyItemsFragment();

        return view;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);

        pagerAdapter = new ProfilePagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
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
