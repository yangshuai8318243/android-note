package com.mengjia.baseLibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.mengjia.baseLibrary.log.AppLog;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/9/11
 * Time: 16:58
 */
public class InterceptRelativeLayout extends RelativeLayout {
    private static final String TAG = "InterceptRelativeLayout";
    private boolean isTouch = false;

    public InterceptRelativeLayout(Context context) {
        super(context);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouch(boolean touch) {
        isTouch = touch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isTouch;
    }


}
