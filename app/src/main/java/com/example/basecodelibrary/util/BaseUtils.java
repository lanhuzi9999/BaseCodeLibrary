package com.example.basecodelibrary.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.basecodelibrary.reflect.ReflectHelper;
import com.example.basecodelibrary.storage.StorageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * @author: robin
 * @description:
 * @date: 2015/7/01
 **/
public class BaseUtils {
    private static final String TAG = "BaseUtils";
    private static final String RAW_BASE = "file:///android_raw/";
    public static final String ASSET_BASE = "file:///android_asset/";
    public static final String FILE_BASE = "file://";

    public static PackageInfo getPackageInfo(PackageManager pm, String packageName, int flags){
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(packageName, flags);
            //2017.7.21 Mic:360Q5读取到的versionCode=2147482647=0x7FFFFC17，占大部分，显然是故意的，针对这个问题进行处理
            if ((pkgInfo.versionCode & 0x7FFF0000) == 0x7FFF0000){ //把它判为360Q5，用getPackageArchiveInfo
                ApplicationInfo ai = pkgInfo.applicationInfo;
                PackageInfo pkgsrc = pm.getPackageArchiveInfo(ai.publicSourceDir, PackageManager.GET_ACTIVITIES);
                if (pkgsrc != null){
                    pkgInfo.versionCode = pkgsrc.versionCode;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            CustomLog.w("", "getPackageInfo fail, reason="+e);
        }
        return pkgInfo ;
    }

    /**
     * 是否ui线程
     *
     * @param context
     * @return
     */
    public static boolean isUIThread(Context context) {
        Thread curthread = Thread.currentThread();
        Looper curloop = context.getMainLooper();
        Thread loopthread = curloop.getThread();
        return curthread.getId() == loopthread.getId();
    }

    public static Activity getRootActivity(Activity activity) {
        Activity root = activity;
        while (root != null && root.getParent() != null) {
            root = root.getParent();
        }
        return root;
    }

    public static String getSystemProperty(String key, String def) {
        String val = (String) ReflectHelper.callStaticMethod("android.os.SystemProperties", "get",
                new Class<?>[]{String.class}, new Object[]{key});
        if (val != null) {
            return val;
        } else {
            return def;
        }
    }

    public static String byte2hex(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder();
        for (int k = 0; k < b.length; k++) {
            stmp = Integer.toHexString(b[k] & 0xff);
            if (stmp.length() == 1) {
                sb.append("0");
            }
            sb.append(stmp);
        }
        return sb.toString();
    }

    public static byte[] hex2byte(String hex) {
        int len = hex.length() / 2;
        int offset = 0;
        byte[] b = new byte[len];
        String stemp = "";
        for (int k = 0; k < len; k++) {
            offset = k << 1;
            stemp = hex.substring(offset, offset + 2);
            try {
                b[k] = (byte) (Integer.parseInt(stemp, 16) & 0xff);
            } catch (NumberFormatException e) {
                b[k] = 0;
            }
        }
        return b;
    }

    public static boolean isFilenameSafe(File file) {
        Boolean val = (Boolean) ReflectHelper.callStaticMethod("android.os.FileUtils", "isFilenameSafe",
                new Class<?>[]{File.class}, new Object[]{file});
        if (val != null) {
            return val.booleanValue();
        } else {
            return true;
        }
    }

    public static boolean copyFile(String srcFileName, String destFileName, boolean overlay) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists() || !srcFile.isFile()) {
            return false;
        }

        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (overlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                destFile.delete();
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            File parentFile = destFile.getParentFile();
            if (!parentFile.exists()) {
                // 目标文件所在目录不存在
                if (!parentFile.mkdirs()) {
                    // 创建目标文件所在目录失败
                    return false;
                }
            }
        }

        // 复制文件
        // 读取的字节数
        int byteread = 0;
        InputStream in = null;
        OutputStream out = null;

        try {
            in = new FileInputStream(srcFile);
            out = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];

            while ((byteread = in.read(buffer)) != -1) {
                out.write(buffer, 0, byteread);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean copyToFile(InputStream inputStream, File destFile) {
        boolean succ = false;
        if (ReflectHelper.methodSupported("android.os.FileUtils", "copyToFile",
                new Class<?>[]{InputStream.class, File.class})) {
            Boolean val = (Boolean) ReflectHelper.callStaticMethod("android.os.FileUtils", "copyToFile",
                    new Class<?>[]{InputStream.class, File.class}, new Object[]{inputStream, destFile});
            if (val != null)
                succ = val.booleanValue();
        }
        if (!succ) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(destFile, false);
                byte[] data = new byte[4096];
                int len = 0;
                len = inputStream.read(data);
                while (len > 0) {
                    fos.write(data, 0, len);
                }
                fos.flush();
                succ = true;
            } catch (Exception e) {
                CustomLog.e(TAG, "copyToFile fail, file=" + (destFile != null ? destFile.getAbsolutePath() : "null")
                        + ",reason=" + e);
            } finally {
                if (fos != null) {
                    IoUtils.closeQuietly(fos);
                }
            }
        }
        CustomLog.d(TAG, "copyToFile,succ:" + succ + ",file=" + (destFile != null ? destFile.getAbsolutePath() : "null"));
        return succ;
    }

    public static int setPermissions(String file, int mode, int uid, int gid) {
        Integer val = (Integer) ReflectHelper.callStaticMethod("android.os.FileUtils", "setPermissions",
                new Class<?>[]{String.class, int.class, int.class, int.class},
                new Object[]{file, mode, uid, gid});
        CustomLog.w(TAG, "setPermissions file=" + file + ",mode=" + Integer.toOctalString(mode) + ",ret="
                + ((val != null && val.intValue() == 0) ? "success" : "fail") + ",code=" + val);
        if (val != null) {
            return val.intValue();
        } else
            return -1;
    }


    public static String getLegalFileName(String filename) {
        if (TextUtils.isEmpty(filename)) {
            return filename;
        }
        char[] illegal = new char[]{'?', '&', '='};
        filename = filename.trim();
        int index = -1, k;
        for (k = 0; k < filename.length(); k++) {
            for (char c : illegal) {
                if (c == filename.charAt(k)) {
                    index = k;
                }
            }
            if (index > 0) {
                break;
            }
        }
        if (index > 0) {
            filename = filename.substring(0, index);
        }
        return filename;
    }

    public static String getFileNameFromUrl(String url) {
        if (url == null) {
            return null;
        }
        int index;
        try {
            Uri uri = Uri.parse(url);
            url = uri.getPath();
            index = url.lastIndexOf('/');
            if (index > 0) {
                url = url.substring(index + 1);
            }
        } catch (Exception e) {
            index = url.lastIndexOf('/');
            if (index > 0) {
                url = url.substring(index + 1);
            }
        }
        return url;
    }

    /**
     * 从一个输入流中根据一定的编码方式读取出内容文本
     *
     * @param in     InputStream，输入流
     * @param encode String，编码方式
     * @return String 内容文本
     * @throws IOException
     */
    public static String getInputStreamText(InputStream in, String encode) {
        byte[] in2b = getInputStreamBytes(in);
        return getInputStreamText(in2b, encode);
    }

    public static String getInputStreamText(byte[] data, String encode) {
        try {
            if (data == null) {
                return null;
            }
            return new String(data, encode);
        } catch (Exception e) {
            return new String(data);
        }
    }

    public static byte[] getInputStreamBytes(InputStream in) {

        ByteArrayOutputStream swapStream = null;
        try {
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[2048];
            int rc = 0;
            while ((rc = in.read(buff, 0, buff.length)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            return in2b;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (swapStream != null) {
                try {
                    swapStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int fillBytes(InputStream is, byte[] data) {
        int length = data.length;
        int readbytes, offset;
        offset = 0;
        try {
            while (length > offset) {
                readbytes = is.read(data, offset, length - offset);
                if (readbytes < 0) {
                    return offset == 0 ? -1 : offset;
                }
                offset += readbytes;
            }
            return offset;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return offset == 0 ? -1 : offset;
        }
    }

    public static boolean isRawUrl(String url) {
        if (url != null && url.startsWith(" ")) {
            url = url.trim();
        }
        return (null != url) && url.startsWith(RAW_BASE);
    }

    public static boolean isAssetUrl(String url) {
        if (url != null && url.startsWith(" ")) {
            url = url.trim();
        }
        return (null != url) && url.startsWith(ASSET_BASE);
    }

    public static boolean isFileUrl(String url) {
        if (url != null && url.startsWith(" ")) {
            url = url.trim();
        }
        return (null != url) && (url.startsWith(FILE_BASE) && !url.startsWith(ASSET_BASE) && !url.startsWith(RAW_BASE));
    }

    public static boolean isHttpUrl(String url) {
        if (url != null && url.startsWith(" ")) {
            url = url.trim();
        }
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

    public static boolean isHttpsUrl(String url) {
        if (url != null && url.startsWith(" ")) {
            url = url.trim();
        }
        return url != null && url.startsWith("https://");
    }


    public static void saveInputStreamToFile(InputStream is, File file) {
        FileOutputStream os = null;
        try {
            CustomLog.i(TAG, "saveInputStreamToFile to file=" + file.getPath());
            os = new FileOutputStream(file);
            byte[] buff = new byte[8192];
            int rc = 0;
            while ((rc = is.read(buff, 0, buff.length)) > 0) {
                os.write(buff, 0, rc);
            }
            os.flush();
        } catch (Exception e) {
            CustomLog.e(TAG, "saveInputStreamToFile to file=" + file.getPath() + " fail,reason=" + e.getMessage());
            e.printStackTrace();
            file.delete();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    CustomLog.i(TAG,
                            "saveInputStreamToFile to file=" + file.getPath() + " close fail,reason=" + e.getMessage());
                }
            }
        }
    }

    public static void spider2File(String str, String fileName) {
        String writeFilePathAndName = StorageUtil.getInstance().getWorkingDirectory() + File.separator + "spider"
                + File.separator + fileName;
        saveString2File(str, writeFilePathAndName, true);
    }

    public static void saveString2File(String str, String sFileName, boolean deleteExitsFile) {
        OutputStreamWriter writer = null;
        FileOutputStream fos = null;
        try {
            File file = new File(sFileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (deleteExitsFile && file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file, true);
            writer = new OutputStreamWriter(fos);
            writer.write(str);
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static void animateActivity(Activity activity, int inanima, int outanima) {
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) > 4) {
                CustomLog.v("animeteActivity:", "overridePendingTransition");
                // overridePendingTransition(R.anim.push_left_in,R.anim.push_left_out);
                ReflectHelper.callMethod(BaseUtils.getRootActivity(activity), "overridePendingTransition",
                        new Class<?>[]{int.class, int.class}, new Object[]{inanima, outanima});
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
        }
    }

    public static String stripAnchor(String url) {
        int anchorIndex = url.indexOf('#');
        if (anchorIndex != -1) {
            return url.substring(0, anchorIndex);
        }
        anchorIndex = url.indexOf('&');
        if (anchorIndex != -1) {
            return url.substring(0, anchorIndex);
        }
        return url;
    }

    public static byte[] shortToBytes2(short s) {
        byte[] mybytes = new byte[2];
        mybytes[1] = (byte) (0xff & s);
        mybytes[0] = (byte) ((0xff00 & s) >> 8);

        return mybytes;
    }

    public static short byte2ToShort(byte mybytes[], int nOff) {
        return (short) (((short) (0xff & mybytes[nOff + 0]) << 8) | ((short) (0xff & mybytes[nOff + 1])));
    }

    public static byte[] intToBytes4(int i) {
        byte[] mybytes = new byte[4];
        mybytes[3] = (byte) (0xff & i);
        mybytes[2] = (byte) ((0xff00 & i) >> 8);
        mybytes[1] = (byte) ((0xff0000 & i) >> 16);
        mybytes[0] = (byte) (int) (((long) 0xff000000 & (long) i) >> 24);
        return mybytes;
    }

    public static int bytes4ToInt(byte mybytes[], int nOff) {
        return (0xff & mybytes[nOff + 0]) << 24 | (0xff & mybytes[nOff + 1]) << 16 | (0xff & mybytes[nOff + 2]) << 8
                | 0xff & mybytes[nOff + 3];
    }

    /**
     * 判断一段字符串是否为邮箱地址
     *
     * @param strEmail
     * @return
     */
    public static boolean isEmail(String strEmail) {
        if (strEmail.indexOf('@') != -1) {
            return true;
        } else {
            return false;
        }
        // String strPattern =
        // "^[a-z]([a-z0-9]*[-_]?[a-z0-9]+)*@([a-z0-9]*[-_]?[a-z0-9]+)+.[a-z]{2,3}.[a-z]{2}$";
        // "^([a-z0-9]*[-_]?[a-z0-9]+)+@(([a-z0-9]*[-_]?[a-z0-9]+)[.])+[a-z]{2,4}$";
        // 邮箱判断 修改 by lixj
        // Pattern p = Pattern.compile(strPattern);
        // Matcher m = p.matcher(strEmail);
        // return m.matches();

    }

    /**
     * 是否为电话号码
     *
     * @param strPhoneNumber
     * @return
     */
    public static boolean isPhoneNumber(String strPhoneNumber) {
        if (TextUtils.isEmpty(strPhoneNumber)) {
            return false;
        }
        /*
         * cmcc-中国移动手机号码规则cucc-中国联通手机号码规则cnc--中国网通3G手机号码规则
         */
        // String cmcc = "^[1]{1}(([3]{1}[4-9]{1})|([5]{1}[89]{1}))[0-9]{8}$";
        // String cucc = "^[1]{1}(([3]{1}[0-3]{1})|([5]{1}[3]{1}))[0-9]{8}$";
        // String cnc = "^[1]{1}[8]{1}[89]{1}[0-9]{8}$";
        // Pattern p = Pattern.compile(cmcc);
        // Matcher m = p.matcher(strPhoneNumber);
        // return m.matches();

        //
        boolean isValid = false;

        /*
         * 可接受的电话格式有:
         *
         * ^//(? : 可以使用 "(" 作为开头
         *
         * (//d{3}): 紧接着三个数字
         *
         * //)? : 可以使用")"接续
         *
         * [- ]? : 在上述格式后可以使用具选择性的 "-".
         *
         * (//d{4}) : 再紧接着三个数字
         *
         * [- ]? : 可以使用具选择性的 "-" 接续.
         *
         * (//d{4})$: 以四个数字结束.
         *
         * 可以比较下列数字格式:
         *
         * (123)456-78900, 123-4560-7890, 12345678900, (123)-4560-7890
         * "^//(?(//d{3})//)?[- ]?(//d{3})[- ]?(//d{5})$";
         */
        try {
            // 手机(中国移动手机号码)：regexp="^((\(\d{3}\))|(\d{3}\-))?13[456789]\d{8}|15[89]\d{8}"

            // 所有手机号码：regexp="^((\(\d{3}\))|(\d{3}\-))?13[0-9]\d{8}|15[89]\d{8}"(新添加了158,159两个号段)
            if (strPhoneNumber.length() == 11) {// 长度只改成长度为11位

                // Pattern pattern = Pattern.compile("^13/d{9}||15[8,9]/d{8}$");
                // Pattern pattern = Pattern.compile("^1/d{10}$");
                // Matcher matcher = pattern.matcher(strPhoneNumber);
                // if (matcher.matches()) {
                isValid = true;
                // }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            isValid = false;
        }
        return isValid;

    }

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0 || str.equalsIgnoreCase("null")) {
            return true;
        } else {
            return false;
        }
    }


    public static void setProcessForeground(IBinder apptoken, int pid, boolean isforeground) {
        if (Integer.parseInt(Build.VERSION.SDK) >= 14) {
            return;
        }
        Object am = ReflectHelper.callStaticMethod("android.app.ActivityManagerNative", "getDefault", null, null);
        if (am == null) {
            return;
        }
        if (apptoken == null) {
            apptoken = new Binder();
        }
        CustomLog.d(TAG, "call setProcessForeground pid=" + android.os.Process.myPid() + ",isforeground=" + isforeground);
        ReflectHelper.callMethod(am, "setProcessForeground", new Class<?>[]{IBinder.class, int.class, boolean.class},
                new Object[]{apptoken, pid, isforeground});
    }

    public static void recycleAllImageView(ViewGroup parent) {
        if (parent == null) {
            return;
        }
        final int N = parent.getChildCount();
        View child = null;
        for (int k = 0; k < N; k++) {
            child = parent.getChildAt(k);
            if (child instanceof ImageView) {
                ((ImageView) child).setImageResource(0);
                child.setTag(null);
            } else if (child instanceof ViewGroup) {
                recycleAllImageView((ViewGroup) child);
            }
        }
    }

    public static void recycleImage(View v) {
        if (v instanceof ImageView) {
            Drawable dw = ((ImageView) v).getDrawable();
            if (dw != null && dw instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) dw).getBitmap();
                if (bmp != null) {
                    v.setTag(null);
                    ((ImageView) v).setImageResource(0);
                }
            }
        }
    }

    public static Bitmap getBitmapFrom(Drawable drawable) {
        if (drawable != null && drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            return null;
        }
    }

    /***
     * 字符串的前后去掉换行符或空格
     */
    public static String stringDeleteNoUsedChar(String infomation) {
        CustomLog.v(TAG, "stringDeleteNoUsedChar_info:|" + infomation + "|");
        if (isEmpty(infomation)) {
            return infomation;
        }
        if (infomation != null) {
            int start = 0;
            int end = infomation.length();
            for (int i = 0; ; i++) {
                String str = infomation.substring(i, i + 1);
                if (str.endsWith(" ") || str.endsWith("\r") || str.endsWith("\n")) {
                    start = i + 1;
                } else {
                    break;
                }
            }

            for (int i = end - 1; i <= 0; i--) {
                String str = infomation.substring(i, i + 1);
                if (str.endsWith(" ") || str.endsWith("\r") || str.endsWith("\n")) {
                    end = i + 1;
                } else {
                    break;
                }
            }
            infomation = infomation.substring(start, end).trim();
            CustomLog.v(TAG, "stringDeleteNoUsedChar_end info:" + start + "," + end + "|" + infomation + "|");
        }

        return infomation;
    }

    public static void saveBytesToFile(byte[] bytes, File file) {
        FileOutputStream os = null;
        try {
            CustomLog.i(TAG, "saveInputStreamToFile to file=" + file.getPath());
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            os = new FileOutputStream(file);
            // byte[] buff = new byte[8192];
            int rc = 0;
            if ((rc = bytes.length) > 0) {
                os.write(bytes, 0, rc);
            }
            os.flush();
        } catch (Exception e) {
            CustomLog.e(TAG, "saveInputStreamToFile to file=" + file.getPath() + " fail,reason=" + e.getMessage(), e);
            e.printStackTrace();
            file.delete();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    CustomLog.i(TAG,
                            "saveInputStreamToFile to file=" + file.getPath() + " close fail,reason=" + e.getMessage());
                }
            }
        }
    }


    public static String getHexMD5Str(byte[] arrIn) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] arrB = md.digest(arrIn);
        StringBuffer sb = new StringBuffer(32);
        for (int i = 0; i < arrB.length; i++) {
            int intTmp = arrB[i];
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            if (intTmp < 16) {
                sb.append('0');
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString().toUpperCase();
    }

    public static void zeroFileLength(String filepath) {
        CustomLog.i(TAG, "zeroFileLength file=" + filepath);
        File file = new File(filepath);
        if (file.exists() && file.isFile()) {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                FileChannel fc = fos.getChannel();
                fc.truncate(0);
            } catch (Exception e) {
                file.delete();
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     * 获取当前屏幕分辨率 宽度×高度
     *
     * @param activity
     * @return
     */
    public static int[] getDefaultDisplay(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕宽度（像素）
        int width = metric.widthPixels;
        // 屏幕高度（像素）
        int height = metric.heightPixels;
        int[] defaultDisplay = new int[]{width, height};
        return defaultDisplay;
    }

    /**
     * 删除某路径下的文件夹，包括所有子文件夹和子文件
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            char endChar = filePath.charAt(filePath.length() - 1);
            String endStr = String.valueOf(endChar);
            if (!"/".equals(endStr) && !(File.separator).equals(endStr)) {
                filePath = filePath + File.separator;
            }
            String[] childFileNames = file.list();
            for (String childFileName : childFileNames) {
                String childFilePathAndName = filePath + childFileName;
                File childFile = new File(childFilePathAndName);
                if (childFile.exists()) {
                    if (childFile.isDirectory()) {
                        childFilePathAndName = childFilePathAndName + File.separator;
                    }
                    deleteFile(childFilePathAndName);
                }
            }

            file.delete();
            CustomLog.d(TAG, "delete directory is " + file);
        }

        if (file.isFile()) {
            file.delete();
            CustomLog.d(TAG, "delete file is " + file);
        }

    }

    public static boolean compareString(String a, String b) {
        if (a == b) {
            return true;
        } else if ((a != null && !a.equals(b)) || (a == null && b != null)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean compareStringNull(String a, String b) {
        if (a == b) {
            return true;
        } else if ((TextUtils.isEmpty(a) && TextUtils.isEmpty(b)
                || (a == null && TextUtils.isEmpty(b))
                || (b == null && TextUtils.isEmpty(a)))) {
            return true;
        } else if ((a != null && !a.equals(b)) || (a == null && b != null)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean compareObject(Object a, Object b) {
        if (a == b) {
            return true;
        }
        if (a == null && b != null) {
            return false;
        }
        if (a != null && b == null) {
            return false;
        }
        if (!a.equals(b)) {
            return false;
        }
        return true;
    }

    public static boolean compareObjectArray(Object[] a, Object[] b) {
        if (a == b) {
            return true;
        }
        int N1 = a != null ? a.length : 0;
        int N2 = b != null ? b.length : 0;
        if (N1 != N2) {
            return false;
        }
        int k = 0;
        Object o1, o2;
        for (k = 0; k < N1; k++) {
            o1 = a[k];
            o2 = b[k];
            if (o1 == null && o2 != null) {
                return false;
            }
            if (o1 != null && o2 == null) {
                return false;
            }
            if (!o1.getClass().equals(o2.getClass())) {
                return false;
            }
            if (!o1.equals(o2)) {
                return false;
            }
        }
        return true;
    }


    public static boolean compareNetworkInfo(NetworkInfo a, NetworkInfo b) {
        if (a == b) {
            return true;
        }
        if (a == null && b != null || a != null && b == null) {
            return false;
        }
        if (a.getType() != b.getType()) {
            return false;
        }
        if (a.getSubtype() != b.getSubtype()) {
            return false;
        }
        if (a.getState() != b.getState()) {
            return false;
        }
        if (!compareString(a.getTypeName(), b.getTypeName())) {
            return false;
        }
        if (!compareString(a.getSubtypeName(), b.getSubtypeName())) {
            return false;
        }
        if (!compareString(a.getExtraInfo(), b.getExtraInfo())) {
            return false;
        }
        return true;
    }

    public static boolean compareDrawable(Drawable dw1, Drawable dw2) {
        if (dw1 != null && dw2 != null && dw1 instanceof BitmapDrawable && dw2 instanceof BitmapDrawable) {
            Bitmap b1 = ((BitmapDrawable) dw1).getBitmap();
            Bitmap b2 = ((BitmapDrawable) dw2).getBitmap();
            return b1 == b2;
        } else {
            return dw1 == dw2;
        }
    }

    /**
     * 判断某应用是否有安装
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static boolean isInstalledApp(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return false;
        }
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        CustomLog.i(TAG, pkgName + ", ai is installed? " + (ai != null ? "true" : "false"));
        return (ai != null);
    }


    public static int daysBetween(long time1, long time2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date smdate = new Date(time1);
        Date bdate = new Date(time2);
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }


    /**
     * 将日期格式化为 yyyy-MM-dd hh:mm:ss
     */
    public static String formatDate(long mill) {
        return mill < 1 ? String.valueOf(mill) : String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", mill);
    }

    /**
     * 将日期格式化为 yyyy-MM-dd hh:mm:ss
     */
    public static String formatDate(Date d) {
        return d == null ? "" : String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS", d);
    }

    public static boolean isToday(long milliseconds) {
        Date date = new Date(milliseconds);
        Date today = new Date();
        if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth()
                && date.getDate() == today.getDate())
            return true;
        return false;
    }

    /**
     * 采用了新的办法获取APK图标，之前的失败是因为android中存在的一个BUG,通过 appInfo.publicSourceDir =
     * apkPath;来修正这个问题，详情参见:
     * http://code.google.com/p/android/issues/detail?id=9151
     **/
    public static Drawable getApkIcon(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return null;
        }
        File file = new File(apkPath);
        if (!file.exists()) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadIcon(pm);
            } catch (Exception e) {
                CustomLog.e(TAG, "ApkIconLoader", e);
            }
        }
        return null;
    }

    public static CharSequence getApkName(Context context, String apkPath) {
        if (TextUtils.isEmpty(apkPath)) {
            return null;
        }
        File file = new File(apkPath);
        if (!file.exists()) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = apkPath;
            appInfo.publicSourceDir = apkPath;
            try {
                return appInfo.loadLabel(pm);
            } catch (Exception e) {
                CustomLog.e(TAG, "ApkIconLoader", e);
            }
        }
        return null;
    }

    public static Bitmap zoomBitmap(Bitmap bitmap, int scaleWidth) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scale = height / (width * 1.0f);
        float scaleHeight = scaleWidth * scale;
        Bitmap newbmp = Bitmap.createScaledBitmap(bitmap, scaleWidth, (int) scaleHeight, false);
        return newbmp;
    }

    /**
     * 系统分享文本
     *
     * @param context
     * @param title
     * @param text
     */
    public static void shareTextMessage(Context context, String title, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        // 分享发送到数据类型
        intent.setType("text/plain");
        // 分享的主题
        intent.putExtra(Intent.EXTRA_SUBJECT, "" + title);
        // 分享的内容
        intent.putExtra(Intent.EXTRA_TEXT, "" + text);
        // 允许intent启动新的activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //目标应用选择对话框的标题
        context.startActivity(Intent.createChooser(intent, title));
    }

    /**
     * 是否为系统app
     * @param applicationInfo
     * @return
     */
    public static boolean isUserApp(ApplicationInfo applicationInfo) {
        return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
    }

    public static boolean isUserApp(Context context, int uid) {
        PackageManager packageManager = context.getPackageManager();
        String packageName = packageManager.getNameForUid(uid);
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (applicationInfo == null) {
            return false;
        } else {
            return ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0);
        }
    }

    /**
     * 将文本复制到剪贴板
     *
     * @param context
     * @param content
     */
    public static void copyClipData(Context context, String content) {
        copyClipData2(context, content);
        ToastUtil.showToast(context, "页面链接已经复制到剪贴板");
    }

    public static void copyClipData2(Context context, String content) {
        if (context == null) {
            return;
        }
        if (Integer.parseInt(Build.VERSION.SDK) > 10) {
            android.content.ClipboardManager clipmgr = (android.content.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText(null, content);
            clipmgr.setPrimaryClip(clip);
        } else {
            android.text.ClipboardManager clipmgr = (android.text.ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE);
            clipmgr.setText(content);
        }
    }


    /**
     * 获取距离当前日期之前或之后days天的指定时间, days为正数则表示当前日期之后，days为负数则表示当前日期之前
     *
     * @param days
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static long getRelativeDayEndingTime(int days, int hour, int minute, int second) {
        Date nowday = new Date();
        // 得到日历
        Calendar calendar = Calendar.getInstance();
        // 把当前时间赋给日历
        calendar.setTime(nowday);
        // 设置为当前日期之前或之后days天
        calendar.add(Calendar.DATE, days);
        // 设置时间为hour点
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        // 设置时间为minute分
        calendar.set(Calendar.MINUTE, minute);
        // 设置时间为second秒
        calendar.set(Calendar.SECOND, second);
        // 得到当前日期之前或之后days天的指定时间
        long lativedayEndingTime = calendar.getTimeInMillis();
        CustomLog.d(TAG, "getRelativeDayEndingTime, " + lativedayEndingTime);
        return lativedayEndingTime;
    }

    /**
     * 文字超出 渐隐
     *
     * @param textViews
     */
    public static void setTextMarquee(TextView... textViews) {
        if (textViews.length > 0) {
            for (TextView textView : textViews) {
                if (textView != null) {
                    textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    textView.setSingleLine(true);
                    textView.setSelected(true);
                    textView.setFocusable(true);
                    textView.setFocusableInTouchMode(true);
                    textView.setMarqueeRepeatLimit(0);
                }
            }
        }
    }


    /**
     * 获取随机数
     *
     * @param time
     * @return
     */
    public static String getRandomChar(long time) {
        Random random = new Random(time);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; i++) {
            char c = (char) (random.nextLong() % 26 + 97);
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * 获取电量信息
     * @param context
     * @return
     */
    public static int getBatteryInfo(Context context) {
        int vol = -1;
        final int SDK_VER = android.os.Build.VERSION.SDK_INT;
        BatteryManager manager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
        if (SDK_VER >= 21) {
            vol = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else if (SDK_VER < 21) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            vol = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        return vol;
    }

}
