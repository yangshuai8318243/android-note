package com.mengjia.baseLibrary.mvp.data;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MvpData {

    private Class mClassName;
    private String mFragmentName;
    private Bundle mData;
    private List<String> permissions = new ArrayList<>();

    public Class getmClassName() {
        return mClassName;
    }

    public void setmClassName(Class mClassName) {
        this.mClassName = mClassName;
    }

    public String getmFragmentName() {
        return mFragmentName;
    }

    public void setmFragmentName(String mFragmentName) {
        this.mFragmentName = mFragmentName;
    }

    public Bundle getmData() {
        return mData;
    }

    public void setmData(Bundle mData) {
        this.mData = mData;
    }

    public String[] getPermissions() {
        return permissions.toArray(new String[]{});
    }

    public void add(List<String> permissions) {
        this.permissions.addAll(permissions);
    }

    public void setPermissions(String permission) {
        this.permissions.add(permission);
    }

}
