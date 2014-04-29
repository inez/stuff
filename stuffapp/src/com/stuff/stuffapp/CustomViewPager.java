package com.stuff.stuffapp;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class CustomViewPager extends ViewPager {

    public CustomViewPager(Context context) {
        super(context);
    	Log.d("CustomViewPager", "CustomViewPager 1");
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    	Log.d("CustomViewPager", "CustomViewPager 2");
    }

	@Override
	protected boolean canScroll(final View v, final boolean checkV, final int dx, final int x, final int y) {
		// TODO: Not sure if this code is needed anymore
		if ( y > getHeight() / 2 - 40 && y < getHeight() / 2 + 40 && x > 0 && x < 200 ) {
    		return false;
    	}
		return super.canScroll(v, checkV, dx, x, y);
	}

}