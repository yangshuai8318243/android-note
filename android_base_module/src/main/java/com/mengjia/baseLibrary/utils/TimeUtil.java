package com.mengjia.baseLibrary.utils;

import android.annotation.SuppressLint;

import androidx.annotation.IntDef;

import com.mengjia.baseLibrary.log.AppLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/24
 * Time: 18:33
 */
public class TimeUtil {

    /**
     * Created by goldze on 2017/5/14.
     * 时间相关常量
     */
    public interface TimeConstants {

        /**
         * 毫秒与毫秒的倍数
         */
        int MSEC = 1;
        /**
         * 秒与毫秒的倍数
         */
        int SEC = 1000;
        /**
         * 分与毫秒的倍数
         */
        int MIN = 60 * SEC;
        /**
         * 时与毫秒的倍数
         */
        int HOUR = 60 * MIN;
        /**
         * 天与毫秒的倍数
         */
        int DAY = 24 * HOUR;

    }

    private static List<String> weekStrs;

    static {
        weekStrs = new ArrayList<>();
        weekStrs.add("周日");
        weekStrs.add("周一");
        weekStrs.add("周二");
        weekStrs.add("周三");
        weekStrs.add("周四");
        weekStrs.add("周五");
        weekStrs.add("周六");
    }

    public interface TimeFormatConstants {
        String COMPLETE_DISCONNECTION_WITHOUT_SEPARATION = "yyyy-MM-dd-HH-mm-ss";
        String COMPLETE_STANDARD = "yyyy/MM/dd HH:mm:ss";
        String COMPLETE_STANDARD_NOT_SECOND = "yyyy/MM/dd HH:mm";
        String HH_MM_SS = "HH:mm:ss";
    }

    public static String dateToWeek(long time, List<String> list) {
        if (list == null || list.size() < 7) {
            list = weekStrs;
        }
        Date date = new Date(time);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        int i = instance.get(Calendar.DAY_OF_WEEK);
        return list.get(i - 1);
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s, String fm) throws ParseException {
        String res;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fm);
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 获取当前0点时间戳
     *
     * @return
     */
    public static long getThatDay0Time() {
        return (System.currentTimeMillis() - (System.currentTimeMillis() + TimeZone.getDefault().getRawOffset()) % (24 * 60 * 60 * 1000));
    }

    /**
     * 获取n天前0点时间戳
     *
     * @return
     */
    public static long getBeforeDay0Time(int day) {
        long thatDay0Time = getThatDay0Time();
        Date date = new Date(thatDay0Time);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.DATE, -day);
        Date time = instance.getTime();
        return time.getTime();
    }

    /**
     * 获取昨天0点时间戳
     *
     * @return
     */
    public static long getYesterDay0Time() {
        long thatDay0Time = getThatDay0Time();
        Date date = new Date(thatDay0Time);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.HOUR, -24);
        Date time = instance.getTime();
        return time.getTime();
    }

    /**
     * 获取明天0点时间戳
     *
     * @return
     */
    public static long getTomorrowDay0Time() {
        long thatDay0Time = getThatDay0Time();
        Date date = new Date(thatDay0Time);
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        instance.add(Calendar.HOUR, 24);
        Date time = instance.getTime();
        return time.getTime();
    }

    /**
     * 获取星期一开始时间戳
     *
     * @return
     */
    public static long getWeekStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar. DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTimeInMillis();
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeFormatConstants.COMPLETE_STANDARD);
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(Long time) {
        String res;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(TimeFormatConstants.COMPLETE_STANDARD);
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(Long time, String fm) {
        String res;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fm);
        Date date = new Date(time);
        res = simpleDateFormat.format(date);
        return res;
    }
}
