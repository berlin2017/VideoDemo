package com.berlin.livedemo;

import android.content.Context;
import android.util.AttributeSet;

import io.vov.vitamio.widget.VideoView;

/**
 * Created by AnHuiNews on 2017/5/2.
 */

public class MyVideoView extends VideoView {
    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getHolder().setSizeFromLayout();
    }


}
