package com.mengjia.baseLibrary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.mengjia.baseLibrary.app.BaseApp;
import com.mengjia.baseLibrary.log.AppLog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class FileUtil {
    private static final String TAG = "FileUtil";
    private static final int BUFFER_SIZE = 1024;

    // 用于获取APP的所在包目录
    public static String s_packageName;
    //来获得当前应用程序对应的apk文件的路径
    public static String s_packageCodePath;
    // 获取该程序的安装包路径
    public static String s_packageResourcePath;
    // 获得根目录/data
    public static String s_DataDirectory = Environment.getDataDirectory().getPath();
    //获得缓存目录/cache
    public static String s_DownloadCacheDirectory = Environment.getDownloadCacheDirectory().getPath();
    //获得SD卡目录/mnt/sdcard
    public static String s_ExternalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
    // 获得系统目录/system
    public static String s_RootDirectoryy = Environment.getRootDirectory().getPath();
    // 缓存路径
    public static String s_CacheDirPath;

    public static String s_ExternalCacheDir;

    /**
     * 获取系统URI
     *
     * @param authorites
     * @param filePath
     * @return
     */
    public static Uri getFileUri(String filePath, String authorites) {
        Uri uri = null;
        File file = new File(filePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //第二个参数为 包名.fileprovider
            uri = MPFileProvider.getUriForFile(BaseApp.getInstance(), authorites, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }


    /**
     * 初始化获得部分路径
     *
     * @param application
     */
    public static void init(Context application) {
        s_packageName = application.getPackageName();
        s_packageCodePath = application.getPackageCodePath();
        s_CacheDirPath = application.getCacheDir().getPath();
        s_packageResourcePath = application.getPackageResourcePath();
        s_ExternalCacheDir = application.getExternalCacheDir().getPath();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File externalFilesDir = application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            s_ExternalStorageDirectory = externalFilesDir.getPath();
        }
    }


    //检查SDCard存在并且可以读写
    public static boolean isSDCardState() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 判断文件是否已经存在
     *
     * @param fileName 要检查的文件名
     * @return boolean, true表示存在，false表示不存在
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 新建目录
     *
     * @param path 目录的绝对路径
     * @return 创建成功则返回true
     */
    public static boolean createFolder(String path) {
        File file = new File(path);
        return file.mkdir();
    }

    /**
     * 创建文件
     *
     * @param path     文件所在目录的目录名
     * @param fileName 文件名
     * @return 文件新建成功则返回true
     */
    public static boolean createFile(String path, String fileName) {
        File file = new File(path + File.separator + fileName);
        if (file.exists()) {
            return false;
        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 删除单个文件
     *
     * @param path     文件所在的绝对路径
     * @param fileName 文件名
     * @return 删除成功则返回true
     */
    public static boolean deleteFile(String path, String fileName) {
        File file = new File(path + File.separator + fileName);
        return file.delete();
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param delFile      要删除的文件夹或文件名
     * @param isDeleteSelf 在是文件夹的情况下，是否删除自己当前文件夹
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String delFile, boolean isDeleteSelf) {
        File file = new File(delFile);
        if (!file.exists()) {
//            Toast.makeText(HnUiUtils.getContext(), "删除文件失败:" + delFile + "不存在！", Toast.LENGTH_SHORT).show();
            AppLog.e(TAG, "删除文件失败:" + delFile + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteSingleFile(delFile);
            else
                return deleteDirectory(delFile, isDeleteSelf);
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath$Name 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                AppLog.e(TAG, "--Method--", "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                AppLog.e(TAG, "删除单个文件" + filePath$Name + "失败！");
                return false;
            }
        } else {
            AppLog.e(TAG, "删除单个文件失败：" + filePath$Name + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param filePath     要删除的目录的文件路径
     * @param isDeleteSelf 是否删除自己当前文件夹
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath, boolean isDeleteSelf) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            AppLog.e(TAG, "删除目录失败：" + filePath + "不存在！");
            return false;
        }
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (File file : files) {
            // 删除子文件
            if (file.isFile()) {
                flag = deleteSingleFile(file.getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (file.isDirectory()) {
                flag = deleteDirectory(file
                        .getAbsolutePath(), true);
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            AppLog.e(TAG, "删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (isDeleteSelf) {
            if (dirFile.delete()) {
                AppLog.e(TAG, "--Method--", "Copy_Delete.deleteDirectory: 删除目录" + filePath + "成功！");
                return true;
            } else {
                AppLog.e(TAG, "删除目录：" + filePath + "失败！");
                return false;
            }
        } else {
            return true;
        }
    }

    /**
     * 删除一个目录（可以是非空目录）
     *
     * @param dir 目录绝对路径
     */
    public static boolean deleteDirection(File dir) {
        if (dir == null || !dir.exists() || dir.isFile()) {
            return false;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                deleteDirection(file);//递归
            }
        }
        dir.delete();
        return true;
    }

    /**
     * 将字符串写入文件
     *
     * @param text     写入的字符串
     * @param fileStr  文件的绝对路径
     * @param isAppend true从尾部写入，false从头覆盖写入
     */
    public static void writeFile(String text, String fileStr, boolean isAppend) {
        FileOutputStream f = null;
        try {
            File file = new File(fileStr);

            if (!file.exists()) {
                file.createNewFile();
            }
            f = new FileOutputStream(fileStr, isAppend);
            f.write(text.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeOutput(f);
        }
    }

    /**
     * 读取文件
     *
     * @param fileStr
     * @return
     */
    public static String readeFile(String fileStr) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fileInputStream = new FileInputStream(fileStr);
            byte[] bytes = new byte[1024];
            int read = fileInputStream.read(bytes);
            while (read > 0) {
                stringBuilder.append(new String(bytes, 0, read));
                read = fileInputStream.read(bytes);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public static void closeOutput(OutputStream fileOutputStream) {
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void closeInput(InputStream fileInputStream) {
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 拷贝文件
     *
     * @param srcPath     绝对路径
     * @param destDir     目标文件所在目录
     * @param newFileName 目标文件名
     * @return boolean true拷贝成功
     */
    public static boolean copyFile(String srcPath, String destDir, String newFileName) {
        boolean flag = false;
        File srcFile = new File(srcPath); // 源文件
        if (!srcFile.exists()) {
            AppLog.i(TAG, "FileUtils is copyFile：", "源文件不存在");
            return false;
        }
        // 获取待复制文件的文件名
        String destPath = destDir + File.separator + newFileName;
        if (destPath.equals(srcPath)) {
            AppLog.i(TAG, "FileUtils is copyFile：", "源文件路径和目标文件路径重复");
            return false;
        }
        File destFile = new File(destPath); // 目标文件
        if (destFile.exists() && destFile.isFile()) {
            AppLog.i(TAG, "FileUtils is copyFile：", "该路径下已经有一个同名文件");
            return false;
        }
        File destFileDir = new File(destDir);
        destFileDir.mkdirs();
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcPath);
            fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }

            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInput(fis);
            closeOutput(fos);
        }
        return flag;
    }


    /**
     * 拷贝文件
     *
     * @param imageData 图片数据
     * @param destPath  目标文件路径
     * @return
     */
    public static boolean copyByteImageFile(byte[] imageData, String destPath) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        FileOutputStream fos = null;
        try {
            File file = new File(destPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);
            //当指定压缩格式为PNG时保存下来的图片显示正常
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    /**
     * 拷贝文件
     *
     * @param inputStream 文件数据
     * @param destPath    目标文件路径
     * @return
     */
    public static boolean copyFile(InputStream inputStream, String destPath) {
        // 获取待复制文件的文件名
        File file = new File(destPath);

        boolean flag = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int c;
            while ((c = inputStream.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }

            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInput(inputStream);
            closeOutput(fos);
        }
        return flag;
    }

    /**
     * 重命名文件
     *
     * @param oldPath 旧文件的绝对路径
     * @param newPath 新文件的绝对路径
     * @return 文件重命名成功则返回true
     */
    public static boolean renameTo(String oldPath, String newPath) {
        if (oldPath.equals(newPath)) {
            AppLog.i(TAG, "FileUtils is renameTo：", "文件重命名失败：新旧文件名绝对路径相同");
            return false;
        }
        File oldFile = new File(oldPath);
        File newFile = new File(newPath);

        return oldFile.renameTo(newFile);
    }

    /**
     * 计算某个文件的大小
     *
     * @param path 文件的绝对路径
     * @return 文件大小
     */
    public static long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }


    /**
     * 计算某个文件夹的大小
     *
     * @param file 目录所在绝对路径
     * @return 文件夹的大小
     */
    public static double getDirSize(File file) {
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                return (double) file.length() / 1024 / 1024;
            }
        } else {
            return 0.0;
        }
    }

    /**
     * 获取某个路径下的文件列表
     *
     * @param path 文件路径
     * @return 文件列表File[] files
     */
    public static File[] getFileList(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                return files;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 计算某个目录包含的文件数量
     *
     * @param path 目录的绝对路径
     * @return 文件数量
     */
    public static int getFileCount(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        return files.length;
    }

    /**
     * 获取SDCard 总容量大小(MB)
     *
     * @param path 目录的绝对路径
     * @return 总容量大小
     */
    public long getSDCardTotal(String path) {

        if (null != path && path.equals("")) {

            StatFs statfs = new StatFs(path);
            //获取SDCard的Block总数
            long totalBlocks = statfs.getBlockCount();
            //获取每个block的大小
            long blockSize = statfs.getBlockSize();
            //计算SDCard 总容量大小MB
            return totalBlocks * blockSize / 1024 / 1024;

        } else {
            return 0;
        }
    }

    /**
     * 获取SDCard 可用容量大小(MB)
     *
     * @param path 目录的绝对路径
     * @return 可用容量大小
     */
    public long getSDCardFree(String path) {

        if (null != path && path.equals("")) {

            StatFs statfs = new StatFs(path);
            //获取SDCard的Block可用数
            long availaBlocks = statfs.getAvailableBlocks();
            //获取每个block的大小
            long blockSize = statfs.getBlockSize();
            //计算SDCard 可用容量大小MB
            return availaBlocks * blockSize / 1024 / 1024;

        } else {
            return 0;
        }
    }

    /**
     * 常用文件的文件头如下：(以前六位为准)
     * JPEG (jpg)，文件头：FFD8FF
     * PNG (png)，文件头：89504E47
     * GIF (gif)，文件头：47494638
     * TIFF (tif)，文件头：49492A00
     * Windows Bitmap (bmp)，文件头：424D
     * CAD (dwg)，文件头：41433130
     * Adobe Photoshop (psd)，文件头：38425053
     * Rich Text Format (rtf)，文件头：7B5C727466
     * XML (xml)，文件头：3C3F786D6C
     * HTML (html)，文件头：68746D6C3E
     * Email [thorough only] (eml)，文件头：44656C69766572792D646174653A
     * Outlook Express (dbx)，文件头：CFAD12FEC5FD746F
     * Outlook (pst)，文件头：2142444E
     * MS Word/Excel (xls.or.doc)，文件头：D0CF11E0
     * MS Access (mdb)，文件头：5374616E64617264204A
     * WordPerfect (wpd)，文件头：FF575043
     * Postscript (eps.or.ps)，文件头：252150532D41646F6265
     * Adobe Acrobat (pdf)，文件头：255044462D312E
     * Quicken (qdf)，文件头：AC9EBD8F
     * Windows Password (pwl)，文件头：E3828596
     * ZIP Archive (zip)，文件头：504B0304
     * RAR Archive (rar)，文件头：52617221
     * Wave (wav)，文件头：57415645
     * AVI (avi)，文件头：41564920
     * Real Audio (ram)，文件头：2E7261FD
     * Real Media (rm)，文件头：2E524D46
     * MPEG (mpg)，文件头：000001BA
     * MPEG (mpg)，文件头：000001B3
     * Quicktime (mov)，文件头：6D6F6F76
     * Windows Media (asf)，文件头：3026B2758E66CF11
     * MIDI (mid)，文件头：4D546864
     */
    public static String checkType(String strType) {
        switch (strType) {
            case "FFD8FF":
                return FILE_SUFFIX_JPG;
            case "89504E":
                return FILE_SUFFIX_PNG;
            case "474946":
                return FILE_SUFFIX_GIF;
            case "49492A00":
                return FILE_SUFFIX_TIF;
            case "424D":
                return FILE_SUFFIX_BMP;
            case "232141":
                return FILE_SUFFIX_AMR;
            default:
                return "";
        }
    }

    public static final String FILE_SUFFIX_JPG = "jpg";
    public static final String FILE_SUFFIX_PNG = "png";
    public static final String FILE_SUFFIX_GIF = "gif";
    public static final String FILE_SUFFIX_AMR = "amr";
    public static final String FILE_SUFFIX_TIF = "tif";
    public static final String FILE_SUFFIX_BMP = "bmp";

    /**
     * 获取文件类型后缀
     *
     * @param file 文件
     * @return 后缀
     */
    public static String getFileTypeSuffix(File file) {
        String fileTypeStr = getFileTypeStr(file);
        return checkType(fileTypeStr);
    }

    /**
     * 获取byte数据的类型的字符
     *
     * @param data
     * @return
     */
    public static String getByteDataTypeStr(byte[] data) {
        byte[] b = new byte[3];
        System.arraycopy(data, 0, b, 0, b.length);
        String toHexString = ByteUtil.byteArrToHexString(b);
        return checkType(toHexString.toUpperCase());
    }


    /**
     * 通过二进制判断文件类型
     *
     * @param file
     * @return 返回文件类型字符
     */
    public static String getFileTypeStr(File file) {
        String fielType = "";
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] b = new byte[3];
            fileInputStream.read(b, 0, b.length);
            String toHexString = ByteUtil.byteArrToHexString(b);
            fielType = toHexString.toUpperCase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeInput(fileInputStream);
            return fielType;
        }
    }

    /**
     * 通过二进制判断文件类型
     *
     * @param file
     * @return
     */
    public static String getFileTypeStr(byte[] file) {
        byte[] b = new byte[3];
        System.arraycopy(file, 0, b, 0, 4);
        return ByteUtil.byteArrToHexString(b);
    }

    public static boolean isImageFile(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String reg = ".+(\\.jpeg|\\.jpg|\\.gif|\\.bmp|\\.png).*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url.toLowerCase());
        return matcher.find();
    }

    public static boolean isVideoFile(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String reg = ".+(\\.avi|\\.wmv|\\.mpeg|\\.mp4|\\.mov|\\.mkv|\\.flv|\\.f4v|\\.m4v|\\.rmvb|\\.rm|\\.rmvb|\\.3gp|\\.dat|\\.ts|\\.mts|\\.vob).*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url.toLowerCase());
        return matcher.find();
    }

    public static boolean isUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String reg = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
        return url.matches(reg);
    }

    public static byte[] getFileByte(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 获取文件扩展名
     *
     * @param filename
     * @return
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 获取文件去除扩展名后的名字
     *
     * @param filename
     * @return
     */
    public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }


    /**
     * 读取文件转换成list
     *
     * @param fileName
     * @return
     */
    public static List<String> getFileDataToList(String fileName) {
        File srcFile = new File(fileName); // 源文件
        if (!srcFile.exists()) {
            AppLog.i(TAG, "getFileDataToList：", "源文件不存在");
            return null;
        }
        List<String> inputStreamToList = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(fileName);
            inputStreamToList = getInputStreamToList(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInput(in);
        }
        return inputStreamToList;
    }

    public static String getInputStreamToStr(InputStream inputStream) {
        if (inputStream == null) return null;

        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInput(inputStream);
            closeReader(reader);
        }
        return stringBuilder.toString();
    }

    public static List<String> getInputStreamToList(InputStream inputStream) {
        if (inputStream == null) return null;

        BufferedReader reader = null;
        ArrayList<String> strings = new ArrayList<>();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = reader.readLine()) != null) {
                strings.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInput(inputStream);
            closeReader(reader);
        }
        return strings;
    }

    public static void closeReader(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeReader(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 通过系统uri获得图片文件路径
     *
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Uri uri) {

        String[] projection = new String[]{
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA
        };
//
//
        final Context context = BaseApp.getInstance();
//        if (null == uri) return null;
//        final String scheme = uri.getScheme();
//        String data = null;
//        if (scheme == null)
//            data = uri.getPath();
//        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
//            data = uri.getPath();
//        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
//            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
//            if (null != cursor) {
//                if (cursor.moveToFirst()) {
//                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//                    if (index > -1) {
//                        data = cursor.getString(index);
//                    }
//                }
//                cursor.close();
//            }
//        }

        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    /**
     * zip解压
     *
     * @param srcFile     zip源文件
     * @param destDirPath 解压后的目标文件夹
     * @throws RuntimeException 解压失败会抛出运行时异常
     */
    public static void unZip(File srcFile, String destDirPath) throws RuntimeException {
        long start = System.currentTimeMillis();

        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile);
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();

                System.out.println("解压" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);

                    String canonicalPath = dir.getCanonicalPath();
                    if (!canonicalPath.startsWith(destDirPath)) {
                        // SecurityException
                        dir.mkdirs();
                    } else {
                        AppLog.e(TAG, "解压失败 ：", canonicalPath);
                    }
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    String canonicalPath = targetFile.getCanonicalPath();
                    if (!canonicalPath.startsWith(destDirPath)) {
                        // SecurityException
                        // 保证这个文件的父文件夹必须要存在
                        if (!targetFile.getParentFile().exists()) {
                            targetFile.getParentFile().mkdirs();
                        }
                        targetFile.createNewFile();
                        // 将压缩文件内容写入到这个文件中
                        InputStream is = zipFile.getInputStream(entry);
                        FileOutputStream fos = new FileOutputStream(targetFile);

                        int len;
                        byte[] buf = new byte[BUFFER_SIZE];
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        // 关流顺序，先打开的后关闭
                        fos.close();
                        is.close();
                    } else {
                        AppLog.e(TAG, "解压失败 ：", canonicalPath);
                    }
                }
            }
            long end = System.currentTimeMillis();
            AppLog.i(TAG, "解压完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
