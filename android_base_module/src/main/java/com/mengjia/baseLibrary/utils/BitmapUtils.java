package com.mengjia.baseLibrary.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Base64;

import com.mengjia.baseLibrary.log.AppLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Android Studio.
 * User: SnapeYang
 * Date: 2020/8/29
 * Time: 16:00
 */
public class BitmapUtils {

    public static final String TAG = "BitmapUtils";

    /**
     * 质量压缩
     * 设置bitmap options属性，降低图片的质量，像素不会减少
     * 第一个参数为需要压缩的bitmap图片对象，第二个参数为压缩后图片保存的位置
     * 设置options 属性0-100，来实现压缩（因为png是无损压缩，所以该属性对png是无效的）
     *
     * @param bmp
     * @param file
     * @param quality 0-100 100为不压缩
     */
    public static void qualityCompress(Bitmap bmp, File file, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        fileOutput(baos.toByteArray(), file);
    }

    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return
     */
    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static void sizeCompressARGB8888AndPNG(Bitmap bmp, File file, int ratio) {
        sizeCompress(bmp, file, ratio, Bitmap.Config.ARGB_8888, Bitmap.CompressFormat.PNG);
    }

    /**
     * 尺寸压缩（通过缩放图片像素来减少图片占用内存大小）
     *
     * @param bmp   直接返回bitmap
     * @param ratio
     * @return
     */
    public static Bitmap sizeCompressARGB8888AndPNG(Bitmap bmp, int ratio) {
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);
        return result;
    }


    public static void sizeCompressARGB8888AndPNG(File bmpFile, File file, int ratio) {
        // 压缩Bitmap到对应尺寸
        Bitmap bitmap = BitmapFactory.decodeFile(bmpFile.getPath() + File.separatorChar + bmpFile.getName());
        sizeCompressARGB8888AndPNG(bitmap, file, ratio);
    }

    /**
     * 尺寸压缩（通过缩放图片像素来减少图片占用内存大小）
     *
     * @param bmp
     * @param file
     * @param ratio 尺寸压缩倍数,值越大，图片尺寸越小
     */
    public static void sizeCompress(Bitmap bmp, File file, int ratio, Bitmap.Config config, Bitmap.CompressFormat compressFormat) {
        // 压缩Bitmap到对应尺寸
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() / ratio, bmp.getHeight() / ratio, config);
        Canvas canvas = new Canvas(result);
        Rect rect = new Rect(0, 0, bmp.getWidth() / ratio, bmp.getHeight() / ratio);
        canvas.drawBitmap(bmp, null, rect, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        result.compress(compressFormat, 100, baos);

        byte[] bytes = baos.toByteArray();

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        AppLog.i(TAG, "============》", bitmap.getWidth(), bitmap.getHeight());

        fileOutput(baos.toByteArray(), file);
    }

    /**
     * 通过判断返回一个正常的资源
     * 判断是否是.9图
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static Drawable getImageFromBitmap(Context context, Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Drawable defaultDrawable = null;
        byte[] chunk = bitmap.getNinePatchChunk();

        if (NinePatch.isNinePatchChunk(chunk)) {
            defaultDrawable = new NinePatchDrawable(context.getResources(), bitmap, chunk, new Rect(), null);
        } else {
            defaultDrawable = new BitmapDrawable(context.getResources(), bitmap);
        }
        return defaultDrawable;
    }


    /**
     * 采样率压缩（设置图片的采样率，降低图片像素）
     *
     * @param filePath
     * @param inSampleSize 数值越高，图片像素越低 2的幂
     */
    public static byte[] samplingRateCompress(String filePath, int inSampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
//          options.inJustDecodeBounds = true;//为true的时候不会真正加载图片，而是得到图片的宽高信息。
        //采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * 获得一个Bitmap大小
     *
     * @param bitmap
     * @return
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }


    public static void samplingRateCompress(String filePath, File file, int inSampleSize) {
        byte[] data = samplingRateCompress(filePath, inSampleSize);
        fileOutput(data, file);
    }

    /**
     * bitmap 转换byte数组
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToBytes(Bitmap bitmap) {
        if (bitmap == null) return null;
        int bytes = bitmap.getByteCount();

        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmap.copyPixelsToBuffer(buf);
        byte[] byteArray = buf.array();
        return byteArray;
    }

    /**
     * 使用Matrix
     *
     * @param bitmap 原始的Bitmap
     * @param width  目标宽度
     * @param height 目标高度
     * @return 缩放后的Bitmap
     */
    public static Bitmap scaleMatrix(Bitmap bitmap, float width, float height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleW = width / w;
        float scaleH = height / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // 长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    public static void fileOutput(byte[] bytes, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtil.closeOutput(fos);
        }
    }

    public static String bitmaptoPNGString(Bitmap bitmap) {
        return bitmaptoString(bitmap, Bitmap.CompressFormat.PNG);
    }

    /**
     * 将bitmap转换成为Base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        if (bitmap == null) return "";
        // 将Bitmap转换成字符串
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, 10, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return string;
    }

    /**
     * 将Base64转换成为Bitmap
     *
     * @param string
     * @return
     */
    public static Bitmap stringtoBitmap(String string) {
        //将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.NO_WRAP);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 读取照片旋转角度
     *
     * @param path 照片路径
     * @return 角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        if (bitmap == null) {
            return null;
        }
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }


}
