package com.mengjia.baseLibrary.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mengjia.baseLibrary.R;


/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/9/11
 * Time: 14:36
 */
public class LoadingView extends LinearLayout {
    private static final int LOADING_TIME = 800;
    private ImageView imageView;
    private boolean isStop = true;
    private TextView textView;

    public LoadingView(Context context) {
        super(context);
        initView(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        imageView = new ImageView(context);
        imageView.setImageResource(R.mipmap.loading_icon);
        LayoutParams layoutParams = new LayoutParams(100, 100);
        imageView.setLayoutParams(layoutParams);
        addView(imageView);
        textView = new TextView(context);
        textView.setText("");
        textView.setTextSize(21);
        textView.setVisibility(GONE);
        addView(textView);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return true;
    }

    private Runnable runAnim = new Runnable() {
        @Override
        public void run() {
            //构造ObjectAnimator对象的方法
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "rotation", 0.0F, 360.0F);//设置先顺时针360度旋转然后逆时针360度旋转动画
            animator.setDuration(LOADING_TIME);//设置旋转时间
            animator.start();//开始执行动画（顺时针旋转动画）
            if (!isStop) {
                postDelayed(runAnim, LOADING_TIME);
            }
        }
    };



    //实现先顺时针360度旋转然后逆时针360度旋转动画功能
    public void startAnim() {
        isStop = false;
        post(runAnim);
    }

    public void stopAnim() {
        isStop = true;
    }
}
