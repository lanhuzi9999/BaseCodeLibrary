package com.example.basecodelibrary.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;


import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.example.basecodelibrary.reflect.ReflectHelper;

public class FileUtil {
    static private final String TAG = "FileUtil";
    private static final char s_separator = '/';

    /**
     * sdcard是否存在
     *
     * @return
     */
    public static boolean isExternalStorageMounted() {
        String state = Environment.getExternalStorageState();
        CustomLog.d(TAG, "getExternalStorageState = " + state);
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    public static boolean isFilenameSafe(File file) {
        Boolean val = (Boolean) ReflectHelper.callStaticMethod("android.os.FileUtils", "isFilenameSafe",
                new Class<?>[]{File.class}, new Object[]{file});
        if (val != null) {
            return val.booleanValue();
        } else {
            return false;
        }
    }

    public static boolean copyToFile(InputStream inputStream, File destFile) {
        Boolean val = (Boolean) ReflectHelper.callStaticMethod("android.os.FileUtils", "copyToFile",
                new Class<?>[]{InputStream.class, File.class}, new Object[]{inputStream, destFile});
        if (val != null) {
            return val.booleanValue();
        } else {
            return false;
        }
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

    public static String getFileContent(String filename) {
        InputStream is = null;
        try {
            is = new FileInputStream(filename);
            String content = StreamUtil.getInputStreamText(is, "utf-8");
            return content;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
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

    public static String illegal_chars = '\\' + "/:?*<>|" + '"';

    public static String clearIllegalChar(String value) {
        if (value == null) {
            return null;
        }
        int length = value.length();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (illegal_chars.indexOf(c) != -1) {
                continue;
            }
            result.append(c);
        }
        return result.toString();
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

    public static void saveFile(String path, String name, String data) {
        try {
            File file = new File(path + name);
            file.createNewFile(); // Michael liu added
            if (file != null) {
                // 写文件
                FileOutputStream out;
                out = new FileOutputStream(file);
                out.write(data.getBytes("UTF-8"));
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static void deleteFile(String filename, String defaultPath) {
        int index = 0;
        index = filename.indexOf('/');
        File file = null;
        if (defaultPath == null) {
            defaultPath = "";
        }
        try {
            if (index == 0) {// lhy:2011.5.5 解决自定义路径的问题
                file = new File(filename);
            } else {
                file = new File(defaultPath + filename);
            }
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getStatFsAvailableSize(String path) {
        try {
            File file = new File(path);
            if (!file.isDirectory()) {
                path = file.getParent();
            }
            CustomLog.i(TAG, "getStatFsAvailableSize: path = " + path);
            StatFs statfs = new StatFs(path);
            int blockSize = statfs.getBlockSize();
            // int totalBlocks = statfs.getBlockCount();
            int availableBlocks = statfs.getAvailableBlocks();
            long availableSize = (long) blockSize * (long) availableBlocks;
            CustomLog.d(TAG, "getStatFsAvailableSize: " + availableSize);
            return availableSize;
        } catch (Exception e) {
            CustomLog.i(TAG, "getStatFsAvailableSize fail, reason=" + e);
            return 0.0f;
        }
    }

    /**
     * 合并两个路径字符串
     *
     * @param _path1 第一个路径
     * @param _path2 第二个路径
     * @return 返回新后路径
     */
    public static String combine(String _path1, String _path2) {
        if (_path1 == null || _path2 == null)
            return _path1;
        int len1 = _path1.length();
        int len2 = _path2.length();
        if (len2 == 0) {
            return _path1;
        }
        if (len1 == 0) {
            return _path2;
        }
        StringBuffer strRet = new StringBuffer(_path1);
        boolean tFlag = (_path1.charAt(len1 - 1) == s_separator);
        boolean hFlag = (_path2.charAt(0) == s_separator);
        if (tFlag && hFlag) {
            strRet.append(_path2.substring(1));
        } else if ((tFlag && !hFlag) || (!tFlag && hFlag)) {
            strRet.append(_path2);
        } else {
            strRet.append(s_separator);
            strRet.append(_path2);
        }
        return strRet.toString();
    }

    public static String getFileNameWithoutExtension(File file) {
        return getFileNameWithoutExtension(file.getPath());
    }

    public static String getFileNameWithoutExtension(String path) {
        int index = path.lastIndexOf("/");
        String filename = path;
        if (index != -1) {
            filename = filename.substring(index + 1);
        }
        index = filename.lastIndexOf(".");
        if (index != -1) {
            filename = filename.substring(0, index);
        }
        return filename;
    }

    public static File getFileByExtension(String searchpath, final String extension) {
        File searchfile = new File(searchpath);
        if (searchfile.exists() && searchfile.isDirectory()) {
            File resultfiles[] = searchfile.listFiles();
            if (resultfiles != null) {
                for (int i = 0; i < resultfiles.length; i++) {
                    if (resultfiles[i].isFile() && resultfiles[i].getName().endsWith(extension)) {
                        return resultfiles[i];
                    } else if (resultfiles[i].isDirectory()) {
                        File temp = getFileByExtension(resultfiles[i].getAbsolutePath(), extension);
                        if (temp != null) {
                            return temp;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean writeFile(String path, InputStream is) {
        boolean result = false;
        FileOutputStream os = null;
        BufferedOutputStream bos = null;
        try {
            File file = new File(path);
            os = new FileOutputStream(file, false);
            bos = new BufferedOutputStream(os);
            int readLen = 0;
            byte[] buf = new byte[1024];
            while ((readLen = is.read(buf)) != -1) {
                bos.write(buf, 0, readLen);
            }
            bos.flush();
            bos.close();
            os.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static boolean writeTextFile(String path, String data) {
        boolean result = false;
        FileWriter fw = null;
        try {
            File file = new File(path);
            fw = new FileWriter(file);
            fw.write(data);
            fw.close();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static String getFileExtension(String filename) {
        int index = filename.lastIndexOf(".");
        if (index != -1) {
            return filename.substring(index + 1);
        }
        return "default";
    }

    public static void deleteFileOrEntireDir(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File f : files) {
                        deleteFileOrEntireDir(f);
                    }
                }
            }
            file.delete();
        }
    }

}
