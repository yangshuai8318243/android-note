package com.mengjia.baseLibrary.error;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.mengjia.baseLibrary.BuildConfig;
import com.mengjia.baseLibrary.log.AppLog;
import com.mengjia.baseLibrary.net.http.NetworkUtil;
import com.mengjia.baseLibrary.net.http.OkHttpHelp;
import com.mengjia.baseLibrary.net.http.OkNetManager;
import com.mengjia.baseLibrary.utils.AppInformationUitls;
import com.mengjia.baseLibrary.utils.AppMenIfoUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    // CrashHandler实例
    private static CrashHandler sCrashHandler = new CrashHandler();

    private Context mContext;

    private String sdkVar = "";

    //异常保存路径
    private String savePath;
    //报错上报 地址
    private String crashUrl;
    //玩家id
    private String userId = "";

    // 默认的未捕获异常处理器
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // 用来存储应用信息和设备信息
    private Map<String, String> mInfo = new LinkedHashMap<>();
    private OkHttpHelp okHttpHelp;


    private CrashHandler() {
        initOk();
    }

    private void initOk() {
        okHttpHelp = new OkHttpHelp();
        OkHttpClient build = new OkHttpClient.Builder().build();
        okHttpHelp.setOkHttpClient(build);
    }

    public static CrashHandler getInstance() {
        return sCrashHandler;
    }


    public void init(Context context, String savePath) {
        mContext = context.getApplicationContext();
        // 获取默认的未捕获异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置CrashHandler为默认的未捕获异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        this.savePath = savePath;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCrashUrl(String crashUrl) {
        this.crashUrl = crashUrl;
    }

    public void setSdkVar(String sdkVar) {
        this.sdkVar = sdkVar;
    }

    @Override
    public void uncaughtException(Thread t, Throwable ex) {
        // 处理异常
        handleException(ex);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 结束应用
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, ex);
        } else {
            System.exit(1);
        }
    }

    private void handleException(Throwable e) {
        // 收集应用信息和设备信息
        collectInfo();
        // 保存崩溃信息到SD卡
        saveInfo(e);
    }

    private void collectClassInfo(Class cls) {
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mInfo.put(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void collectInfo() {
        // 收集BuildConfig类信息
        collectClassInfo(BuildConfig.class);
        // 收集Build.VERSION类信息
        collectClassInfo(Build.VERSION.class);
        // 收集Build类信息
        collectClassInfo(Build.class);

        List<String> memInfo = AppMenIfoUtil.getMemInfo();
        String toJson = new Gson().toJson(memInfo);
        mInfo.put("memInfo", toJson);
        mInfo.put("CurrentProcessMemory", AppMenIfoUtil.getCurrentProcessMemory(mContext));
    }

    private void saveInfo(Throwable ex) {

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : mInfo.entrySet()) {
            stringBuilder.append(entry.getKey() + " = " + entry.getValue() + "\n");
        }

        String stackTraceString = Log.getStackTraceString(ex);

        HashMap<String, String> stringHashMap = new HashMap<>();
        stringHashMap.put("packageName", mContext.getPackageName());
        stringHashMap.put("sdkEdition", sdkVar);
        stringHashMap.put("device", AppInformationUitls.getPhoneModel());
        stringHashMap.put("runningEnvironment", AppInformationUitls.getOS());
        stringHashMap.put("networkEnvironment", NetworkUtil.getNetMode(mContext));
        if (!TextUtils.isEmpty(userId)) {
            stringHashMap.put("userId", userId);
        }
        stringHashMap.put("desc", stackTraceString);
        stringHashMap.put("errorType", "Error");

        if (!TextUtils.isEmpty(crashUrl)) {
            okHttpHelp.requestSyn(crashUrl, stringHashMap, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    AppLog.e(TAG, "上传失败", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    AppLog.e(TAG, "崩溃日志上传", response.body().string());
                }
            });
        }

    }


    private void saveLocal(String stackTrace) {

        File dir = new File(savePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 以当前时间来命名崩溃日志文件
        long timestamp = System.currentTimeMillis();

        String time = SimpleDateFormat.getDateTimeInstance().format(new Date(System.currentTimeMillis()));
        File file = new File(dir, "crash-" + time + "-" + timestamp + ".log");

        // 保存崩溃信息到文件
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(stackTrace);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

