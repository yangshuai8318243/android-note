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
     * 将px值转换为dip或dp值，保证尺寸大小不变
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }


    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    /**
     * 屏幕宽度（像素）
     */
    public static int getWindowWidth(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.widthPixels;
    }


    /**
     * 屏幕高度（像素）
     */
    public static int getWindowHeight(Activity context) {
        DisplayMetrics metric = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metric);
        return metric.heightPixels;
    }

    /**
     * 获得屏幕高度
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
     * 获得屏幕宽度
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
     * 获得状态栏的高度
     *
     * @param context
     * @return 状态栏高度
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
     * 屏幕分辨率
     *
     * @param mContext
     * @return 屏幕分辨率信息
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
     * 屏幕信息对象
     */
    public static class ScreenInfo {

        /**
         * 英寸
         */
        public double size;

        public String sizeStr;

        /**
         * 高
         */
        public int heightPixels;

        /**
         * 宽
         */
        public int widthPixels;

        public String screenRealMetrics;

        /**
         * 显示器的逻辑密度
         */
        public float density;

        /**
         * 屏幕密度为点每英寸
         */
        public int densityDpi;

        public String densityDpiStr;

        /**
         * 显示在显示器的字体的定标因子
         */
        public float scaledDensity;

        /**
         * 每在 X 维屏幕英寸的确切物理像素
         */
        public float xdpi;

        /**
         * 每在 Y 维屏幕英寸的确切物理像素
         */
        public float ydpi;

        /**
         * 在屏幕中显示的参考密度
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
     * 获取当前屏幕旋转角度
     *
     * @param activity
     * @return 0表示是竖屏; 90表示是左横屏; 180表示是反向竖屏; 270表示是右横屏
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
     * 是否有刘海屏
     *
     * @param activity
     * @return
     */
    public static boolean hasNotchInScreen(Activity activity) {
        if (initHasNotchInScreen) {
            initHasNotchInScreen = false;
            // android  P 以上有标准 API 来判断是否有刘海屏
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
                        //通过判断是否存在rects来确定是否刘海屏手机
                        if (rects != null && rects.size() > 0) {
                            hasNotchInScreen = true;
                            return true;
                        }
                    }
                }
            }
            // 通过其他方式判断是否有刘海屏  目前官方提供有开发文档的就 小米，vivo，华为（荣耀），oppo
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
     * 判断华为刘海屏
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
     * 判断OPPO刘海屏
     *
     * @param activity
     * @return
     */
    private static boolean hasNotchOPPO(Activity activity) {
        return activity.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * 判断vivo刘海屏
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
     * 判断小米刘海屏
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
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = SystemProperties.getMethod("getInt", paramTypes);
            //参数
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
