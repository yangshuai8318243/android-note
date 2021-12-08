package com.mengjia.baseLibrary.utils;

import com.mengjia.baseLibrary.log.AppLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingUtils {

    public static PingInfo ping(String url) {
        PingInfo _info = new PingInfo();
        _info.host = url;
        if (url.isEmpty()) {
            return _info;
        }
        BufferedReader in = null;
        // 将要执行的ping命令,此命令是windows格式的命令
        Runtime r = Runtime.getRuntime();
        String regx = ".*?(time=(.*?)\\s*ms).*";
        String pingCommand = "ping -c 1 -w 2000 " + url;
        try {
            // 执行命令并获取输出
            System.out.println(pingCommand);
            Process p = r.exec(pingCommand);
            if (p == null) {
                return _info;
            }
            // 逐行检查输出,计算类似出现=23ms TTL=62字样的次数
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            float _pingtime = 0;
            while ((line = in.readLine()) != null) {
                if (line.matches(regx)) {
                    String pingtime = line.replaceAll(regx, "$2");
                    _pingtime = Float.parseFloat(pingtime);
                    AppLog.e("PingUtils:time:" + _pingtime + "ms");  //提取想要的信息
                    _info.time = _pingtime;
                }
            }
            AppLog.e("PingUtils:ping:" + _info.toString());
            return _info;
        } catch (Exception ex) {
            ex.printStackTrace(); // 出现异常则返回假
            return _info;
        } finally {
            try {
                if (in != null) {
                    in.close();
                } else {
                    AppLog.e("PingUtils:in = null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static final class PingInfo {
        public String host;
        public float time = -1;

        @Override
        public String toString() {
            return String.format("host=%s,time=%s", host, time);
        }
    }
}
