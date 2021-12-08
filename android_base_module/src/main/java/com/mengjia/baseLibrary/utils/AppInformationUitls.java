package com.mengjia.baseLibrary.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;

import androidx.core.app.ActivityCompat;

import com.mengjia.baseLibrary.log.AppLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.mengjia.baseLibrary.utils.MemoryConstants.GB;
import static com.mengjia.baseLibrary.utils.MemoryConstants.KB;
import static com.mengjia.baseLibrary.utils.MemoryConstants.MB;

public class AppInformationUitls {
    private static final String TAG = "AppInformationUitls";

    /**
     * 获取手机唯一id
     *
     * @param context
     * @return
     */
    public static String getDeviceUniqueId(Context context) {
        String androidId = getAndroidId(context);
        String serialNumber = getSerialNumber();
        if (TextUtils.isEmpty(androidId)) {
            androidId = "";
        }
        if (TextUtils.isEmpty(serialNumber)) {
            serialNumber = "";
        }
        String uniqueId = androidId + serialNumber;

        if (TextUtils.isEmpty(uniqueId)) {
            uniqueId = "";
        }
        return uniqueId;
    }

    /**
     * 获取手机androidId
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

    /**
     * 获得SD卡总大小
     */
    public static String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return getFileSize(blockSize * totalBlocks);
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     */
    public static String getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return getFileSize(blockSize * availableBlocks);
    }

    /**
     * 文件大小获取
     *
     * @param length 文件大小，单位KB
     * @return 文件大小字符串
     */
    public static String getFileSize(long length) {
        if (length >= GB) {
            return String.format("%.2f GB", length * 1.0 / GB);
        } else if (length >= MB) {
            return String.format("%.2f MB", length * 1.0 / MB);
        } else {
            return String.format("%.2f KB", length * 1.0 / KB);
        }
    }

    /**
     * 获取IMEI号，IESI号，手机型号
     */
    @SuppressLint("MissingPermission")
    private String getInfo(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mTm.getDeviceId();
        String imsi = mTm.getSubscriberId();
        String mtype = getSystemModel(); // 手机型号
        String mtyb = getDeviceBrand();//手机品牌
        String numer = "";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
        }
        String i = "-";
        return imei +
                i +
                imsi +
                i +
                mtype +
                i +
                mtyb +
                i +
                numer;
    }

    /**
     * 获取机型
     */
    public static String getPhoneModel() {
        return getDeviceBrand() + " " + getSystemModel();
    }

    /**
     * 获取手机厂商
     *
     * @return 手机厂商
     */
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 获取手机型号
     *
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取当前手机的ip
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    public static String getSystem() {
        return "aos";
    }

    /**
     * 打印签名
     *
     * @param context
     */
    public static void getSingnature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signingInfo = packageInfo.signatures;
            for (Signature st : signingInfo) {
                MessageDigest instance = MessageDigest.getInstance("SHA-1");
                instance.update(st.toByteArray());
                AppLog.e(TAG, "-----getSingnature----->", Base64.encodeToString(instance.digest(), Base64.DEFAULT));
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static String MinCpuFreq;

    /**
     * 获取CPU最小频率（单位KHZ）
     *
     * @return
     */
    public static String getMinCpuFreq() {
        if (TextUtils.isEmpty(MinCpuFreq)) {
            String result = "";
            ProcessBuilder cmd;
            try {
                String[] args = {"/system/bin/cat",
                        "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"};
                cmd = new ProcessBuilder(args);
                Process process = cmd.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[24];
                while (in.read(re) != -1) {
                    result = result + new String(re);
                }
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                result = "N/A";
            }
            MinCpuFreq = result;
        }
        return MinCpuFreq.trim();
    }

    private static String MaxCpuFreq = "";

    /**
     * 获取CPU最大频率（单位KHZ）
     *
     * @return
     */
    public static String getMaxCpuFreq() {
        if (MaxCpuFreq.equals("")) {
            String result = "";
            ProcessBuilder cmd;
            try {
                String[] args = {"/system/bin/cat",
                        "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
                cmd = new ProcessBuilder(args);
                Process process = cmd.start();
                InputStream in = process.getInputStream();
                byte[] re = new byte[24];
                while (in.read(re) != -1) {
                    result = result + new String(re);
                }
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                result = "N/A";
            }
            MaxCpuFreq = result;
        }
        return MaxCpuFreq.trim();
    }

    private static int CPUCores = -1;

    /**
     * cpu核数
     *
     * @return
     */
    public static int getNumberOfCPUCores() {
        if (CPUCores < 0) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                // Gingerbread doesn't support giving a single application access to both cores, but a
                // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
                // chipset and Gingerbread; that can let an app in the background run without impacting
                // the foreground application. But for our purposes, it makes them single core.
                CPUCores = 1;
            }
            try {
                CPUCores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
            } catch (SecurityException e) {
                CPUCores = 0;
            } catch (NullPointerException e) {
                CPUCores = 0;
            }
        }
        return CPUCores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    /**
     * 实时获取CPU当前频率（单位KHZ）
     *
     * @return
     */
    public static String getCurCpuFreq() {
        String result = "N/A";
        try {
            FileReader fr = new FileReader(
                    "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            result = text.trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 通过getprop获取系统信息
     *
     * @param propName
     * @return
     */
    public static String getSystemProperty(String propName) {
        String result = "";
        Process process = null;
        BufferedReader bufferedReader = null;
        try {
            process = Runtime.getRuntime().exec("getprop " + propName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader, 8 * 1024);
            String info;
            while ((info = bufferedReader.readLine()) != null) {
                result += info;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }



    /**
     * 获取运营商代码,
     *
     * @param context
     * @return
     */
    public static String getNetOperatorNumber(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return manager.getSimOperator();
    }

    /**
     * 获得机身内存总大小
     */
    public static String getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return getFileSize(blockSize * totalBlocks);
    }

    /**
     * 获得机身可用内存
     */
    public static String getRomAvailableSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return getFileSize(blockSize * availableBlocks);
    }

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid.replaceAll("-", "");
    }

    /**
     * 获取手机SerialNumber
     *
     * @return
     */
    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    /**
     * 内核版本
     *
     * @return
     */
    public static String getLinuxCore_Ver() {
        Process process = null;
        String kernelVersion = "";
        try {
            process = Runtime.getRuntime().exec("cat / proc / version");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kernelVersion;
    }


    /**
     * 获取手机deviceId
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getDeviceId(Context context) {
        String deviceId = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            try {
                deviceId = telephonyManager.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deviceId;
    }


    private static String macAddress = "";

    /**
     * 获取设备mac地址
     *
     * @param context
     * @return
     */
    public static String getMacFromHardware(Context context) {
        try {
            if (TextUtils.isEmpty(macAddress)) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//5.0以下
                    macAddress = getMacAddress(context);
                    if (macAddress != null) {
                        AppLog.i("android 5.0以前的方式获取mac" + macAddress);
                        macAddress = macAddress.replaceAll(":", "");
                        if (!macAddress.equalsIgnoreCase("020000000000")) {
                            return macAddress;
                        }
                    }
                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    macAddress = getMacAddress();
                    if (macAddress != null) {
                        AppLog.i("android 6~7 的方式获取的mac" + macAddress);
                        macAddress = macAddress.replaceAll(":", "");
                        if (!macAddress.equalsIgnoreCase("020000000000")) {
                            return macAddress;
                        }
                    }
                } else {
                    macAddress = getMacFromHardware();
                    AppLog.i("android 7以后 的方式获取的mac" + macAddress);
                    macAddress = macAddress.replaceAll(":", "");
                    if (!macAddress.equalsIgnoreCase("020000000000")) {
                        return macAddress;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.e("getMacFromHardware:" + e.getMessage());
        }
        return macAddress;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     *
     * @return
     */
    private static String getMacAddress() {
        String WifiAddress = "";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 获取手机macAddress
     * 6.0之前（不包括6.0）
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String macAddress = "";
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                macAddress = info.getMacAddress();
            }
        }
        return macAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     * Android 7.0 之后获取mac地址
     *
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            AppLog.i("all:" + all.size());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                AppLog.i("macBytes:" + macBytes.length + "," + nif.getName());

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取当前手机系统语言。
     *
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
    }

    /**
     * 获取操作系统
     *
     * @return
     */
    public static String getOS() {
        return "Android" + android.os.Build.VERSION.RELEASE;
    }
}
