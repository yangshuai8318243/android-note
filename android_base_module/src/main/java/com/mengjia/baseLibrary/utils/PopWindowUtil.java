package com.mengjia.baseLibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Android Studio.
 * PopWindow帮助类
 * User: SnapeYang
 * Date: 2020/8/28
 * Time: 14:35
 */
public class PopWindowUtil {

    /**
     * @param cx    activity
     * @param view  传入需要显示在什么控件下
     * @param view1 传入内容的view
     * @return
     */
    public static PopupWindow makePopupWindow(Context cx, View view, View view1, int color) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wmManager = (WindowManager) cx.getSystemService(Context.WINDOW_SERVICE);
        wmManager.getDefaultDisplay().getMetrics(dm);
        int Hight = dm.heightPixels;

        PopupWindow mPopupWindow = new PopupWindow(cx);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(color));
        view1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        // 设置PopupWindow的大小（宽度和高度）
        mPopupWindow.setWidth(view.getWidth());
        mPopupWindow.setHeight((Hight - view.getBottom()) * 2 / 3);
        // 设置PopupWindow的内容view
        mPopupWindow.setContentView(view1);
        mPopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        mPopupWindow.setTouchable(true); // 设置PopupWindow可触摸
        mPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸

        return mPopupWindow;
    }

    /**
     * @param cx   此处必须为Activity的实例
     * @param view 显示在该控件之下
     * @param xOff 距离view的x轴偏移量
     * @param yOff 距离view的y轴偏移量
     * @param anim 弹出及消失动画
     * @return
     */
    public static PopupWindow showLocationWithAnimation(PopupWindow popupWindow, final Context cx, View view, int xOff, int yOff, int anim, final OnDissmissListener mListener) {
        // 弹出动画
        popupWindow.setAnimationStyle(anim);

        // 弹出PopupWindow时让后面的界面变暗
        WindowManager.LayoutParams parms = ((Activity) cx).getWindow().getAttributes();
        parms.alpha = 0.5f;
        ((Activity) cx).getWindow().setAttributes(parms);

        int[] positon = new int[2];
        view.getLocationOnScreen(positon);
        // 弹窗的出现位置，在指定view之下
        popupWindow.showAsDropDown(view, positon[0] + xOff, positon[1] + yOff);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // PopupWindow消失后让后面的界面变亮
                WindowManager.LayoutParams parms = ((Activity) cx).getWindow().getAttributes();
                parms.alpha = 1.0f;
                ((Activity) cx).getWindow().setAttributes(parms);
                //自定义接口进行弹出框消失时的操作
                if (mListener != null) {
                    mListener.dissmiss();
                }
            }
        });

        return popupWindow;
    }

    public interface OnDissmissListener {
        void dissmiss();
    }
}
