package com.mengjia.baseLibrary.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ByteUtil {
    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    public static int toInt(byte[] data) {
        if (data == null || data.length != 4) return 0x0;
        // ----------
        return (int) ( // NOTE: type cast not necessary for int
                (0xff & data[0]) << 24 |
                        (0xff & data[1]) << 16 |
                        (0xff & data[2]) << 8 |
                        (0xff & data[3]) << 0
        );
    }

    /**
     * byte数组转哈希值
     *
     * @param bytes
     * @param len
     * @return
     */
    public static long BPHash(byte[] bytes, int len) {
        long hash = 0;

        for (int i = 0; i < len; i++) {
            hash = hash << 7 ^ bytes[i];
        }

        return hash;
    }

    /**
     * 字节转十六进制
     *
     * @param b 需要进行转换的byte字节
     * @return 转换后的Hex字符串
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 字节数组转16进制
     *
     * @param bytes 需要转换的byte数组
     * @return 转换后的Hex字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * Hex字符串转byte
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     *
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }


    public static int[] btoi(byte[] btarr) {
        if (btarr.length % 4 != 0) {
            return null;
        }
        int[] intarr = new int[btarr.length / 4];

        int i1, i2, i3, i4;
        //j循环int		k循环byte数组
        for (int j = 0, k = 0; j < intarr.length; j++, k += 4) {
            i1 = btarr[k];
            i2 = btarr[k + 1];
            i3 = btarr[k + 2];
            i4 = btarr[k + 3];

            if (i1 < 0) {
                i1 += 256;
            }
            if (i2 < 0) {
                i2 += 256;
            }
            if (i3 < 0) {
                i3 += 256;
            }
            if (i4 < 0) {
                i4 += 256;
            }
            intarr[j] = (i1 << 24) + (i2 << 16) + (i3 << 8) + (i4 << 0);//保存Int数据类型转换

        }
        return intarr;
    }

    public static byte[] itob(int[] intarr) {
        int bytelength = intarr.length * 4;//长度
        byte[] bt = new byte[bytelength];//开辟数组
        int curint = 0;
        for (int j = 0, k = 0; j < intarr.length; j++, k += 4) {
            curint = intarr[j];
            bt[k] = (byte) ((curint >> 24) & 0b1111_1111);//右移4位，与1作与运算
            bt[k + 1] = (byte) ((curint >> 16) & 0b1111_1111);
            bt[k + 2] = (byte) ((curint >> 8) & 0b1111_1111);
            bt[k + 3] = (byte) ((curint >> 0) & 0b1111_1111);
        }
        return bt;
    }

    /**
     * int转byte
     *
     * @param x
     * @return
     */
    public static byte intToByte(int x) {
        return (byte) x;
    }

    /**
     * byte转int
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        //Java的byte是有符号，通过 &0xFF转为无符号
        return b & 0xFF;
    }

    /**
     * byte[]转int
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    public static int byteArrayToInt(byte[] b, int index) {
        return b[index + 3] & 0xFF |
                (b[index + 2] & 0xFF) << 8 |
                (b[index + 1] & 0xFF) << 16 |
                (b[index + 0] & 0xFF) << 24;
    }

    /**
     * int转byte[]
     *
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }

    /**
     * short转byte[]
     *
     * @param b
     * @param s
     * @param index
     */
    public static void byteArrToShort(byte b[], short s, int index) {
        b[index + 1] = (byte) (s >> 8);
        b[index + 0] = (byte) (s >> 0);
    }

    /**
     * byte[]转short
     *
     * @param b
     * @param index
     * @return
     */
    public static short byteArrToShort(byte[] b, int index) {
        return (short) (((b[index + 0] << 8) | b[index + 1] & 0xff));
    }

    /**
     * 16位short转byte[]
     *
     * @param s short
     * @return byte[]
     */
    public static byte[] shortToByteArr(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }

    /**
     * byte[]转16位short
     *
     * @param b
     * @return
     */
    public static short byteArrToShort(byte[] b) {
        return byteArrToShort(b, 0);
    }

    /**
     * long转byte[]
     *
     * @param x
     * @return
     */
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * byte[]转Long
     *
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    /**
     * 从byte[]中抽取新的byte[]
     *
     * @param data  - 元数据
     * @param start - 开始位置
     * @param end   - 结束位置
     * @return 新byte[]
     */
    public static byte[] getByteArr(byte[] data, int start, int end) {
        byte[] ret = new byte[end - start];
        for (int i = 0; (start + i) < end; i++) {
            ret[i] = data[start + i];
        }
        return ret;
    }

    /**
     * 流转换为byte[]
     *
     * @param inStream
     * @return
     */
    public static byte[] readInputStream(InputStream inStream) {
        ByteArrayOutputStream output = null;

        try {
            output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = inStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                return null;
            }
        }
    }

    /**
     * byte[]转inputstream
     *
     * @param b
     * @return
     */
    public static InputStream readByteArr(byte[] b) {
        return new ByteArrayInputStream(b);
    }

    /**
     * byte数组内数字是否相同
     *
     * @param s1
     * @param s2
     * @return
     */
    public static boolean isEq(byte[] s1, byte[] s2) {
        int slen = s1.length;
        if (slen == s2.length) {
            for (int index = 0; index < slen; index++) {
                if (s1[index] != s2[index]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * byte数组转换为Stirng
     *
     * @param s1     -数组
     * @param encode -字符集
     * @param err    -转换错误时返回该文字
     * @return
     */
    public static String getString(byte[] s1, String encode, String err) {
        try {
            return new String(s1, encode);
        } catch (UnsupportedEncodingException e) {
            return err == null ? null : err;
        }
    }

    /**
     * byte数组转换为Stirng
     *
     * @param s1-数组
     * @param encode-字符集
     * @return
     */
    public static String getString(byte[] s1, String encode) {
        return getString(s1, encode, null);
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] toBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param b
     * @return
     */
    public static String byteArrToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

}
