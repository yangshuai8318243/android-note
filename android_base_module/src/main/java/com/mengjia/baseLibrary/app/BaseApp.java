package com.mengjia.baseLibrary.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.utils.FileUtil;
import com.mengjia.baseLibrary.utils.KeyboardUtils;
import com.mengjia.baseLibrary.utils.ViewUtils;

public class BaseApp extends Application {
    private static Application sInstance;
    public static  String TAG = "";
    public static boolean tst = false;

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        TAG = getClass().getName();
        setApplication(this);
        FileUtil.init(this);
//        new ANRWatchDog().start();
//        BlockCanary.install(this, new AppBlockCanaryContext()).start();
//        initListenerActivity();
    }

    /**
     * 当主工程没有继承BaseApplication时，可以使用setApplication方法初始化BaseApplication
     *
     * @param application
     */
    public static synchronized void setApplication(@NonNull Application application) {
        if (sInstance == null) {
            sInstance = application;
        }
    }

    /**
     * 获得当前app运行的Application
     */
    public static Application getInstance() {
        if (sInstance == null) {
            AppLog.e(TAG, "please inherit BaseApplication or call setApplication.");
        }
        return sInstance;
    }

    private void initListenerActivity() {
        sInstance.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                ViewUtils.setCustomDensity(activity, BaseApp.this);
                KeyboardUtils.initKeyboardListener(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
//                BaseApp.this.onActivityDestroyed(activity);
            }
        });
    }

    protected void onActivityDestroyed(@NonNull Activity activity) {

    }

}
