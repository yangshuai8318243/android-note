package com.mengjia.baseLibrary.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密算法
 */
public class EncryptUitls {

    protected static MessageDigest messagedigest = null;

    /**
     * MessageDigest初始化
     *
     */
    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("MD5FileUtil messagedigest初始化失败");
            e.printStackTrace();
        }
    }

    /**
     * 对文件进行MD5加密
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            FileChannel ch = in.getChannel();
            MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            messagedigest.update(byteBuffer);
            return ByteUtil.byteArrToHexString(messagedigest.digest());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeInput(in);
        }

        return null;
    }

    /**
     * 对字符串进行MD5加密
     *
     * @param s
     * @return
     */
    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    /**
     * 对byte类型的数组进行MD5加密
     *
     * @param bytes
     * @return
     */
    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return ByteUtil.byteArrToHexString(messagedigest.digest());
    }


}
