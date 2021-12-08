package com.mengjia.baseLibrary.app;

import android.view.View;

import androidx.annotation.IdRes;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/29
 * Time: 15:07
 */
public abstract class BaseAppViewHolder {
    protected abstract <V extends View> V findViewById(@IdRes int id);

    public abstract void freed();
}
