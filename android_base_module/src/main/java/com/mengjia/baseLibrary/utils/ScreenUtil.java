package com.mengjia.baseLibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.Surface;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.mengjia.baseLibrary.log.AppLog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/9/11
 * Time: 10:55
 */
public class ScreenUtil {

    public static final int SCREEN_ROTATION_0 = 0;
    public static final int SCREEN_ROTATION_90 = 1;
    public static final int SCREEN_ROTATION_180 = 2;
    public static final int SCREEN_ROTATION_270 = 3;

    public static void setDefaultDisplay(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Configuration configuration = context.getResources().getConfiguration();
            configuration.densityDpi = getDefaultDisplayDensity();
            context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
//            context.createConfigurationContext(configuration);
        }
    }

    public static int getDefaultDisplayDensity() {
        try {
            Class clazz = Class.forName("android.view.WindowManagerGlobal");
            Method method = clazz.getMethod("getWindowManagerService");
            method.setAccessible(true);
            Object iwm = method.invoke(clazz);
            Method getInitialDisplayDensity = iwm.getClass().getMethod("getInitialDisplayDensity", int.class);
            getInitialDisplayDensity.setAccessible(true);
            Object densityDpi = getInitialDisplayDensity.invoke(iwm, Display.DEFAULT_DISPLAY);
            return (int) densityDpi;
        } catch (Exception e) {
            e.printStackTrace();
            return 500;
        }
    }

    /**
     * ???px????????????dip???dp??????????????????????????????
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * ???dip???dp????????????px??????????????????????????????
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * ???px????????????sp??????????????????????????????
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * ???sp????????????px??????????????????????????????
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * ????????????????????????
     */
    public static int getWindowWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }


    /**
     * ????????????????????????
     */
    public static int getWindowHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * ??????????????????
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * ??????????????????
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * ????????????????????????
     *
     * @param context
     * @return ???????????????
     */
    public static int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * ???????????????
     *
     * @param mContext
     * @return ?????????????????????
     */
    public static ScreenInfo getScreenInfo(Context mContext) {
        ScreenInfo result = new ScreenInfo();
        int widthPixels;
        int heightPixels;

        WindowManager w = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        widthPixels = metrics.widthPixels;
        heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                widthPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthPixels = realSize.x;
                heightPixels = realSize.y;
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        result.widthPixels = widthPixels;
        result.heightPixels = heightPixels;
        result.screenRealMetrics = widthPixels + "x" + heightPixels;
        result.density = metrics.density;
        result.density_default = DisplayMetrics.DENSITY_DEFAULT;
        result.densityDpi = metrics.densityDpi;
        result.densityDpiStr = metrics.densityDpi + " dpi";
        result.scaledDensity = metrics.scaledDensity;
        result.xdpi = metrics.xdpi;
        result.ydpi = metrics.ydpi;
        result.size = (Math.sqrt(Math.pow(widthPixels, 2) + Math.pow(heightPixels, 2)) / metrics.densityDpi);
        result.sizeStr = String.format("%.2f", result.size);

        return result;
    }

    public static List<String> getGpuFreqVolt() {
        List<String> result = new ArrayList<>();

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader("/proc/gpufreq/gpufreq_opp_dump"));
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * ??????????????????
     */
    public static class ScreenInfo {

        /**
         * ??????
         */
        public double size;

        public String sizeStr;

        /**
         * ???
         */
        public int heightPixels;

        /**
         * ???
         */
        public int widthPixels;

        public String screenRealMetrics;

        /**
         * ????????????????????????
         */
        public float density;

        /**
         * ???????????????????????????
         */
        public int densityDpi;

        public String densityDpiStr;

        /**
         * ??????????????????????????????????????????
         */
        public float scaledDensity;

        /**
         * ?????? X ????????????????????????????????????
         */
        public float xdpi;

        /**
         * ?????? Y ????????????????????????????????????
         */
        public float ydpi;

        /**
         * ?????????????????????????????????
         */
        public int density_default;

        @Override
        public String toString() {
            return "ScreenInfo{" +
                    "size=" + size +
                    ", sizeStr='" + sizeStr + '\'' +
                    ", heightPixels=" + heightPixels +
                    ", screenRealMetrics='" + screenRealMetrics + '\'' +
                    ", widthPixels=" + widthPixels +
                    ", density=" + density +
                    ", densityDpi=" + densityDpi +
                    ", densityDpiStr='" + densityDpiStr + '\'' +
                    ", scaledDensity=" + scaledDensity +
                    ", xdpi=" + xdpi +
                    ", ydpi=" + ydpi +
                    ", density_default=" + density_default +
                    '}';
        }

        public String toPointStr() {
            Point point = new Point(widthPixels, heightPixels);
            return point.toString();
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param activity
     * @return 0???????????????; 90??????????????????; 180?????????????????????; 270??????????????????
     */
    public static int getDisplayRotation(Activity activity) {
        if (activity == null)
            return 0;

        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return SCREEN_ROTATION_0;
            case Surface.ROTATION_90:
                return SCREEN_ROTATION_90;
            case Surface.ROTATION_180:
                return SCREEN_ROTATION_180;
            case Surface.ROTATION_270:
                return SCREEN_ROTATION_270;
        }
        return 0;
    }

    private static boolean hasNotchInScreen;

    private static boolean initHasNotchInScreen = true;

    /**
     * ??????????????????
     *
     * @param activity
     * @return
     */
    public static boolean hasNotchInScreen(Activity activity) {
        if (initHasNotchInScreen) {
            initHasNotchInScreen = false;
            // android  P ??????????????? API ???????????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                View decorView = activity.getWindow().getDecorView();
                WindowInsets windowInsets = decorView.getRootWindowInsets();
                if (windowInsets != null) {
                    DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        displayCutout = activity.getWindowManager().getDefaultDisplay().getCutout();
                    }
                    if (displayCutout != null) {
                        List<Rect> rects = displayCutout.getBoundingRects();
                        //????????????????????????rects??????????????????????????????
                        if (rects != null && rects.size() > 0) {
                            hasNotchInScreen = true;
                            return true;
                        }
                    }
                }
            }
            // ??????????????????????????????????????????  ??????????????????????????????????????? ?????????vivo????????????????????????oppo
            String manufacturer = Build.MANUFACTURER;
            if (TextUtils.isEmpty(manufacturer)) {
                hasNotchInScreen = false;
            } else if (manufacturer.equalsIgnoreCase("HUAWEI")) {
                hasNotchInScreen = hasNotchHw(activity);
            } else if (manufacturer.equalsIgnoreCase("xiaomi")) {
                hasNotchInScreen = hasNotchXiaoMi(activity);
            } else if (manufacturer.equalsIgnoreCase("oppo")) {
                hasNotchInScreen = hasNotchOPPO(activity);
            } else if (manufacturer.equalsIgnoreCase("vivo")) {
                hasNotchInScreen = hasNotchVIVO();
            } else {
                hasNotchInScreen = false;
            }

        }
        return hasNotchInScreen;
    }

    /**
     * ?????????????????????
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchHw(Activity activity) {

        try {
            ClassLoader cl = activity.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            return (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ??????OPPO?????????
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchOPPO(Activity activity) {
        return activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * ??????vivo?????????
     *
     * @return
     */
    private static boolean hasNotchVIVO() {
        try {
            Class<?> c = Class.forName("android.util.FtFeature");
            Method get = c.getMethod("isFeatureSupport", int.class);
            return (boolean) (get.invoke(c, 0x20));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ?????????????????????
     *
     * @param activity
     * @return
     */
    public static boolean hasNotchXiaoMi(Activity activity) {
        String key = "ro.miui.notch";
        int result = 0;
        try {
            ClassLoader classLoader = activity.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
            //????????????
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = SystemProperties.getMethod("getInt", paramTypes);
            //??????
            Object[] params = new Object[2];
            params[0] = new String(key);
            params[1] = new Integer(0);
            result = (Integer) getInt.invoke(SystemProperties, params);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result == 1;
    }
}
