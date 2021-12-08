package com.mengjia.baseLibrary.app;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/9/28
 * Time: 17:03
 */
public class ActivityManager {
    private ActivityManager() {
    }

    private static final class ActivityManagerHolder {
        private static final ActivityManager ACTIVITY_MANAGER = new ActivityManager();
    }

    public static ActivityManager getInstance() {
        return ActivityManagerHolder.ACTIVITY_MANAGER;
    }

    //提供栈对象
    private static Stack<WeakReference<Activity>> activityStack = new Stack<>();

    //把Activity添加到栈里面
    public void addActivityManager(Activity activity) {
        if (activity != null) {
            activityStack.add(new WeakReference<Activity>(activity));
        }
    }

    public Activity getLastActivity() {
        if (activityStack.size()>0){
            WeakReference<Activity> activityWeakReference = activityStack.lastElement();
            if (activityWeakReference != null) {
                return activityWeakReference.get();
            }
        }

        return null;
    }

    //删除当前的activity
    public void removeCurrent() {
        //方式二：
        WeakReference<Activity> activityWeakReference = activityStack.lastElement();
        Activity activity = activityWeakReference.get();
        activity.finish();
        activityStack.remove(activityWeakReference);
    }

    //移除指定的Activity
    public void remove(Activity activity) {
        if (activity != null) {

            for (int i = activityStack.size() - 1; i >= 0; i--) {
                WeakReference<Activity> activityWeakReference = activityStack.get(i);
                Activity currentActivity = activityWeakReference.get();
                if (currentActivity != null) {
                    if (currentActivity.getClass().equals(activity.getClass())) {
                        currentActivity.finish();//销毁当前的activity
                        activityStack.remove(i);//从栈空间移除
                    }
                } else {
                    activityStack.remove(i);//从栈空间移除
                }

            }
        }
    }

    //移除所有的Activity
    public void removeAll(Activity activity) {
        for (int i = activityStack.size() - 1; i >= 0; i--) {
            activity.finish();
            activityStack.get(i).get().finish();
            activityStack.remove(i);
        }
    }

    //返回栈大小
    public int size() {
        return activityStack.size();
    }

}
