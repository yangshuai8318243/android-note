package com.mengjia.baseLibrary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;

import androidx.annotation.DrawableRes;

import com.mengjia.baseLibrary.app.BaseApp;

import java.io.File;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/31
 * Time: 16:41
 */
public class ResourcesUtil {
    public static final String RES_URL_HEAD = ContentResolver.SCHEME_ANDROID_RESOURCE + "://";

    public static String getResourcesUri(@DrawableRes int id) {
        return getResourcesUri(id, BaseApp.getInstance());
    }

    public static String getResourcesUri(@DrawableRes int id, Context context) {
        Resources resources = context.getResources();
        String uriPath = RES_URL_HEAD +
                resources.getResourcePackageName(id) + "/" +
                resources.getResourceTypeName(id) + "/" +
                resources.getResourceEntryName(id);
        return uriPath;
    }

    public static boolean isResourcesUri(String url) {
        return url.contains(RES_URL_HEAD);
    }

    public static String getFrescoResourcesUri(@DrawableRes int id) {
        String packageName = BaseApp.getInstance().getPackageName();
        String toString = new StringBuilder()
                .append("res://")
                .append(packageName)
                .append("/")
                .append(id)
                .toString();
        return toString;
    }

    public static String getAssetsUri(String path, String name) {
        String toString = new StringBuilder()
                .append("file")
                .append("://")
                .append("android_asset")
                .append(File.separatorChar)
                .append(path)
                .append(File.separatorChar)
                .append(name)
                .toString();
        return toString;
    }


    public static int getMipmapId(String paramString) {
        Context applicationContext = BaseApp.getInstance().getApplicationContext();
        return applicationContext.getResources().getIdentifier(paramString, "mipmap", applicationContext.getPackageName());
    }

    public static int getStringId(String paramString) {
        Context applicationContext = BaseApp.getInstance().getApplicationContext();
        return applicationContext.getResources().getIdentifier(paramString, "string", applicationContext.getPackageName());
    }

    public static int getBoolId(String paramString) {
        Context applicationContext = BaseApp.getInstance().getApplicationContext();
        return applicationContext.getResources().getIdentifier(paramString, "bool", applicationContext.getPackageName());
    }

    public static int getColorIdRes(String paramString) {
        Context applicationContext = BaseApp.getInstance().getApplicationContext();
        int color = applicationContext.getResources().getIdentifier(paramString, "color", applicationContext.getPackageName());
        return color != 0? applicationContext.getResources().getColor(color) : 0;
    }

    public static int getColorId(String paramString) {
        Context applicationContext = BaseApp.getInstance().getApplicationContext();
        return applicationContext.getResources().getIdentifier(paramString, "color", applicationContext.getPackageName());
    }

    public static int getDrawableId(String paramString) {
        Context applicationContext = BaseApp.getInstance().getApplicationContext();
        return applicationContext.getResources().getIdentifier(paramString, "drawable", applicationContext.getPackageName());
    }

    public static int getInteger(String paramString) {
        Context applicationContext = BaseApp.getInstance().getApplicationContext();
        int integer = applicationContext.getResources().getIdentifier(paramString, "integer", applicationContext.getPackageName());
        return applicationContext.getResources().getInteger(integer);
    }
}
