package com.mengjia.baseLibrary.utils;

import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 日志打印帮助类，可以打印堆栈
 */
public class PrintUtils {

    public static String errInfo(Throwable e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            // 将出错的栈信息输出到printWriter中
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }

    public static void printStackTrace(String TAG) {
        // 调试打印堆栈而不退出
//        Log.d(TAG, Log.getStackTraceString(new Throwable()));

        // 创建异常打印堆栈
        Exception e = new Exception(TAG);
        e.printStackTrace();

//        // 获取当前线程的堆栈
//        for (StackTraceElement i : Thread.currentThread().getStackTrace()) {
//            Log.i(TAG, i.toString());
//        }

//        RuntimeException re = new RuntimeException();
//        re.fillInStackTrace();
//        Log.i(TAG, "stackTrace", re);
    }


    public static void printProcess(String TAG) {
        //获取当前进程的方法
        //获取消耗的时间
        long elapsedCpuTime = Process.getElapsedCpuTime();
        //获取该进程的ID
        int myPid = Process.myPid();
        //获取该线程的ID
        int myTid = Process.myTid();
        //获取该进程的用户ID
        int myUid = Process.myUid();
        String format = String.format("当前进程为%d,当前线程为%d,当前用户id为%d", myPid, myTid, myUid);

        Log.e(TAG, "--------------应用状态-------------");
        Log.e(TAG, format);
    }
}
