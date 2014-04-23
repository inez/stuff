package com.stuff.stuffapp.fragments;

import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.activities.MainActivity;
import com.stuff.stuffapp.helpers.Ids;
import com.stuff.stuffapp.models.Item;

public class AddFragment extends Fragment {
	
	public interface OnItemAddedListener {
        public void onItemAdded(Item item);
    }

	private static String TAG = "AddFragment";
	
	private View view;
	
	private Button btAdd;
	
	private EditText etName;

	private EditText etDescription;
	
	private ProgressDialog progressDialog;
	
	private Bitmap photo;

	public static AddFragment newInstance() {
		AddFragment fragment = new AddFragment();
		return fragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
        view = inflater.inflate(R.layout.fragment_add, container, false);

        LinearLayout ll_select_picture = (LinearLayout) view.findViewById(R.id.llSelectPicture);
        ll_select_picture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), Ids.ACTION_GET_CONTENT);
			}
		});

        LinearLayout ll_capture_picture = (LinearLayout) view.findViewById(R.id.llCapturePicture);
        ll_capture_picture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Not supported yet", Toast.LENGTH_SHORT).show();
			}
		});

        btAdd = (Button) view.findViewById(R.id.btAdd);
        etName = (EditText) view.findViewById(R.id.etName);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        
        btAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if ( etName.getText().toString().trim().length() == 0 ) {
					Toast.makeText(getActivity(), "Please provide a name", Toast.LENGTH_LONG).show();
					return;
				}
				if ( etName.getText().toString().trim().length() < 5 ) {
					Toast.makeText(getActivity(), "Name must be at least 5 characters", Toast.LENGTH_LONG).show();
					return;
				}
				if ( photo == null ) {
					Toast.makeText(getActivity(), "Please select an image", Toast.LENGTH_LONG).show();
					return;
				}

				progressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.please_wait), true);

				ParseUser parseUser = ParseUser.getCurrentUser();

				ParseACL acl = new ParseACL();
				acl.setWriteAccess(parseUser, true);
				acl.setPublicWriteAccess(false);
				acl.setPublicReadAccess(true);
				
				final Item item = new Item();
				item.setACL(acl);				
				item.setOwner(parseUser);
				item.setName(etName.getText().toString());
				item.setDescription(etDescription.getText().toString());
				//item.setLocation(userLocation);
				item.setLocation(((MainActivity) getActivity()).getLastKnownLocation()); 
				item.setPhotoFile(new ParseFile(getBitmapAsBytaArray(photo), "photo.jpg"));

				Bitmap photo200 = Bitmap.createScaledBitmap(photo, 200, 200 * photo.getHeight() / photo.getWidth(), false);
				item.setPhotoFile200(new ParseFile(getBitmapAsBytaArray(photo200), "photo200.jpg"));

				Bitmap photo100 = Bitmap.createScaledBitmap(photo200, 100, 100 * photo200.getHeight() / photo200.getWidth(), false);
				item.setPhotoFile100(new ParseFile(getBitmapAsBytaArray(photo100), "photo100.jpg"));

				item.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException arg0) {
						progressDialog.dismiss();
						
						// Hide soft keyboard
				        InputMethodManager mgr = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				        mgr.hideSoftInputFromWindow(etName.getWindowToken(), InputMethodManager.SHOW_IMPLICIT);

						((OnItemAddedListener) getActivity()).onItemAdded(item);
					}

				});
			}

        });

/*
        // get location from current Parse user
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        ParseGeoPoint.getCurrentLocationInBackground(5000, criteria, new LocationCallback() {
            @Override
            public void done(ParseGeoPoint geoPoint, ParseException error) {
                if ( null == error ) {
                    AddFragment.this.userLocation = geoPoint;
                    Log.d(AddFragment.TAG, "Got user location: " + geoPoint.getLatitude() + ", " + geoPoint.getLongitude());
                }
                else {
                    Toast.makeText(getActivity(), "Unable to get user's location", Toast.LENGTH_SHORT).show();
                    Log.e(AddFragment.TAG, "Unable to get user's location: " + error.getMessage());
                }
            }
        });
*/        

        return view;
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Ids.ACTION_GET_CONTENT) {
                Uri selectedImageUri = data.getData();

                try {
					photo = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
					photo = Bitmap.createScaledBitmap(photo, 600, 600 * photo.getHeight() / photo.getWidth(), false);

					Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
					if (cursor.getCount() == 1) {
						cursor.moveToFirst();
						int orientation =  cursor.getInt(0);
						Matrix matrix = new Matrix();
						matrix.postRotate(orientation);
						photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
					}

					ImageView ivPreview = (ImageView) view.findViewById(R.id.ivPreview);
					ivPreview.setImageBitmap(photo);
					ivPreview.setVisibility(ImageView.VISIBLE);
				} catch (Exception e) {
				}
            }
        }
    }

	@Override
	public void onResume() {
		super.onResume();
		
		// Display soft keyboard
        InputMethodManager mgr = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
	}
	
	// TODO: Make it a static helper
	private byte[] getBitmapAsBytaArray(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
		return bos.toByteArray();
	}

	/*
	public String getPath(Uri contentUri) {
	    String res = null;
	    String[] proj = { MediaStore.Images.Media.DATA };
	    Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
	    if(cursor.moveToFirst()){;
	       int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	       res = cursor.getString(column_index);
	    }
	    cursor.close();
	    return res;
	}
	*/
	
	


}
