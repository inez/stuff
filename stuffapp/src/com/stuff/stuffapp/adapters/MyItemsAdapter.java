package com.stuff.stuffapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.stuff.stuffapp.R;
import com.stuff.stuffapp.models.Item;

public class MyItemsAdapter extends ParseQueryAdapter<Item> {
    public MyItemsAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<Item>() {
            @Override
            public ParseQuery<Item> create() {
                ParseQuery<Item> query = new ParseQuery<Item>(Item.class);
                query.whereEqualTo("owner", ParseUser.getCurrentUser());
                query.orderByAscending("createdAt");
                return query;
            }
        });
    }

    @Override
    public View getItemView(Item item, View view, ViewGroup parent) {
        if ( null == view ) {
            view = View.inflate(getContext(), R.layout.item_list_profile, null);
        }

        final ImageView iv_myItem = (ImageView) view.findViewById(R.id.iv_my_item);
        TextView tv_myItemName = (TextView) view.findViewById(R.id.tv_my_item_name);
        TextView tv_myItemDesc = (TextView) view.findViewById(R.id.tv_my_item_desc);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(item.getPhotoFile100().getUrl(), iv_myItem, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }
            
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            }
            
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                iv_myItem.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
            }
        });
        tv_myItemName.setText(item.getName());
        tv_myItemDesc.setText(item.getDescription());

        return view;
    }
}
