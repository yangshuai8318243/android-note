package com.mengjia.baseLibrary.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppMenIfoUtil {

    public static List<String> getMemInfo() {
        List<String> result = new ArrayList<>();

        try {
            String line;
            BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
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
     * 获取当前应用所占内存
     *
     * @param context
     * @return
     */
    public static String getCurrentProcessMemory(Context context) {
        if (context!=null){
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            String pkgName = context.getPackageName();

            List<ActivityManager.RunningAppProcessInfo> appList = activityManager.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo appInfo : appList) {
                if (appInfo.processName.equals(pkgName)) {
                    int[] pidArray = new int[]{appInfo.pid};

                    Debug.MemoryInfo[] memoryInfo = activityManager.getProcessMemoryInfo(pidArray);

                    float temp = (float) memoryInfo[0].getTotalPrivateDirty() / 1024.0f;

                    return String.format("%.2f", temp) + "MB";
                }
            }
        }
        return "获取失败";
    }


    /**
     * @param context
     * @return
     */
    public static String getTotalSpace(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        String toString = new StringBuilder()
                .append("threshold:")
                .append(memoryInfo.threshold / MemoryConstants.MB)
                .append("\n")
                .append("availMem:")
                .append(memoryInfo.availMem / MemoryConstants.MB)
                .append("\n")
                .append("lowMemory:")
                .append(memoryInfo.lowMemory)
                .append("\n")
                .append("totalMem:")
                .append(memoryInfo.totalMem / MemoryConstants.MB)
                .append("\n")
                .toString();
        return toString;
    }


    /**
     * /proc/meminfo
     * <p>
     * MemTotal:        2902436 kB
     * MemFree:          199240 kB
     * MemAvailable:    1088764 kB
     * Buffers:           40848 kB
     * Cached:           862908 kB
     * SwapCached:        54696 kB
     * Active:          1222848 kB
     * Inactive:         671468 kB
     * Active(anon):     758516 kB
     * Inactive(anon):   242560 kB
     * Active(file):     464332 kB
     * Inactive(file):   428908 kB
     * Unevictable:        5972 kB
     * Mlocked:             256 kB
     * SwapTotal:       1048572 kB
     * SwapFree:         537124 kB
     * Dirty:                12 kB
     * Writeback:             0 kB
     * AnonPages:        988820 kB
     * Mapped:           508996 kB
     * Shmem:              4800 kB
     * Slab:             157204 kB
     * SReclaimable:      57364 kB
     * SUnreclaim:        99840 kB
     * KernelStack:       41376 kB
     * PageTables:        51820 kB
     * NFS_Unstable:          0 kB
     * Bounce:                0 kB
     * WritebackTmp:          0 kB
     * CommitLimit:     2499788 kB
     * Committed_AS:   76292324 kB
     * VmallocTotal:   258867136 kB
     * VmallocUsed:           0 kB
     * VmallocChunk:          0 kB
     * CmaTotal:              0 kB
     * CmaFree:               0 kB
     * <p>
     * MemFree: LowFree与HighFree的总和，被系统留着未使用的内存
     * <p>
     * Buffers: 用来给文件做缓冲大小
     * <p>
     * Cached: 被高速缓冲存储器（cache memory）用的内存的大小（等于 diskcache minus SwapCache ）.
     * <p>
     * SwapCached:被高速缓冲存储器（cache memory）用的交换空间的大小，已经被交换出来的内存，但仍然被存放在swapfile                    中。用来在需要的时候很快的被替换而不需要再次打开I/O端口。
     * <p>
     * Active: 在活跃使用中的缓冲或高速缓冲存储器页面文件的大小，除非非常必要否则不会被移作他用.
     * <p>
     * Inactive: 在不经常使用中的缓冲或高速缓冲存储器页面文件的大小，可能被用于其他途径.
     * <p>
     * HighTotal:
     * <p>
     * HighFree: 该区域不是直接映射到内核空间。内核必须使用不同的手法使用该段内存。
     * <p>
     * LowTotal:
     * <p>
     * LowFree: 低位可以达到高位内存一样的作用，而且它还能够被内核用来记录一些自己的数据结构。Among many
     * <p>
     * 　　　　　other things, it is where everything from the Slab is allocated. Bad things happen when you’re out 　　　　　of lowmem.
     * <p>
     * SwapTotal: 交换空间的总大小
     * <p>
     * SwapFree: 未被使用交换空间的大小
     * <p>
     * Dirty: 等待被写回到磁盘的内存大小。
     * <p>
     * Writeback: 正在被写回到磁盘的内存大小。
     * <p>
     * AnonPages：未映射页的内存大小
     * <p>
     * Mapped: 设备和文件等映射的大小。
     * <p>
     * Slab: 内核数据结构缓存的大小，可以减少申请和释放内存带来的消耗。
     * <p>
     * SReclaimable:可收回Slab的大小
     * <p>
     * SUnreclaim：不可收回Slab的大小（SUnreclaim+SReclaimable＝Slab）
     * <p>
     * PageTables：管理内存分页页面的索引表的大小。
     * <p>
     * NFS_Unstable:不稳定页表的大小
     * <p>
     * VmallocTotal: 可以vmalloc虚拟内存大小
     * <p>
     * VmallocUsed: 已经被使用的虚拟内存大小
     */
    private static String getFieldFromMeminfo(String field) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
        Pattern p = Pattern.compile(field + "\\s*:\\s*(.*)");

        try {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    return m.group(1);
                }
            }
        } finally {
            br.close();
        }

        return null;
    }

    private static String memoryTotal;

    public static String getMemTotal() {
        if (!TextUtils.isEmpty(memoryTotal)) {
            return memoryTotal;
        }
        String result = null;

        try {
            result = getFieldFromMeminfo("MemTotal");
        } catch (IOException e) {
            e.printStackTrace();
        }
        memoryTotal = result;
        return result;
    }


    public static String getMemAvailable() {
        String result = null;

        try {
            result = getFieldFromMeminfo("MemAvailable");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

}
