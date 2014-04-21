package com.stuff.stuffapp.fragments;

import java.util.Calendar;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Message;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessageCompose extends Fragment{
	
	private EditText etCompose = null;
	private Button btSend = null;
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup parent, Bundle savedInstanceState) {
		
		View view = inf.inflate(R.layout.fragment_message_compose,parent,false);
		etCompose =  (EditText) view.findViewById(R.id.etCompose);
		btSend = (Button) view.findViewById(R.id.btSend);
		
		btSend.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				Toast.makeText(getActivity(), "Sending", Toast.LENGTH_LONG).show();
				// TODO Auto-generated method stub
				
				Message message = new Message();
				message.setText(etCompose.getText().toString());
				//TODO: populate with appropriate values- remove hardcode. 
				message.setMessageId(1000);
				message.setSeenAt(Calendar.getInstance().getTime());
				//message.setFromUser(getCurrentUser());
				//message.setToUser(getCurrentUser());
				ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
				parseInstallation.put("message", message);
				
				parseInstallation.saveEventually(new SaveCallback(){

					@Override
					public void done(ParseException e) {
						e.printStackTrace();
						
					}
					
				});

			}
			
		});
        
		return view;
	}
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
	}
		

	
	private ParseUser getCurrentUser() { 
		
		//TODO: This will get the current user .		
		//Remove this and just add a call to getCurr
		return null; 
	}


}
