package com.mengjia.baseLibrary.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class ViewUtils {
    private static float sSysDensity;
    private static float sSysScaledDensity;
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    public static void setCustomDensity(@Nullable Activity activity, @Nullable final Application application) {
        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sSysDensity == 0) {
            sSysDensity = appDisplayMetrics.density;
            sSysScaledDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if (newConfig != null && newConfig.fontScale > 0) {
                        sSysScaledDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {
                }
            });
        }

        final float targetDensity = appDisplayMetrics.widthPixels / 360; //(暂定宽度为360，也可以改为高度方向等);
        final float targetScaleDensity = targetDensity * (sSysScaledDensity / sSysDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);
        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaleDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics atyDisplayMetrics = activity.getResources().getDisplayMetrics();
        atyDisplayMetrics.density = targetDensity;
        atyDisplayMetrics.scaledDensity = targetScaleDensity;
        atyDisplayMetrics.densityDpi = targetDensityDpi;

    }

    /**
     * 动态创建view的id
     */
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }
        } else {
            return View.generateViewId();
        }
    }


}
