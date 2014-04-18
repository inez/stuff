package com.stuff.stuffapp.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.SaveCallback;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Item;

public class AddFragment extends Fragment {

	private static String TAG = "AddFragment";
	
	private View view;
	
	private Button button_add;
	
	private EditText et_name;

	private EditText et_description;
	
	private ProgressDialog progressDialog;
	
	public static AddFragment newInstance() {
		AddFragment fragment = new AddFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_add, container, false);

        button_add = (Button) view.findViewById(R.id.button_add);
        et_name = (EditText) view.findViewById(R.id.et_name);
        et_description = (EditText) view.findViewById(R.id.et_description);
        
        button_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait), true);
				
				Item item = new Item();
				item.setName(et_name.getText().toString());
				item.setDescription(et_description.getText().toString());
				item.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						progressDialog.dismiss();
					}

				});
			}

        });

		return view;
    }

	/*
	To display keyboard
	@Override
	public void onResume() {
		super.onResume();
        EditText someEditText = (EditText) view.findViewById(R.id.textView2);
        someEditText.requestFocus(); 
        InputMethodManager mgr =      (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(someEditText, InputMethodManager.SHOW_IMPLICIT);
	}
	*/

}
