package com.stuff.stuffapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.stuff.stuffapp.R;
import com.stuff.stuffapp.adapters.MyItemsAdapter;

public class MyItemsFragment extends Fragment {
    private static final String TAG = "MyItemsFragment";

    private ListView lvMyItems;
    private MyItemsAdapter adapter;

    public static final MyItemsFragment newInstance() {
        return new MyItemsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_my_items, container, false);

        lvMyItems = (ListView) view.findViewById(R.id.lvMyItems);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if ( null == adapter ) {
            adapter = new MyItemsAdapter(getActivity());
        }
        lvMyItems.setAdapter(adapter);
    }
}
