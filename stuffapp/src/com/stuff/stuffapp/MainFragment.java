package com.stuff.stuffapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainFragment extends Fragment {

	private String name;
	
	public static MainFragment newInstance(String name) {
		MainFragment fragment = new MainFragment();
		Bundle bundle = new Bundle();
		bundle.putString("name", name);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		name = args.getString("name");
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        TextView tv = (TextView) view.findViewById(R.id.textView1);
        tv.setText(name);
		return view;
    }
}
