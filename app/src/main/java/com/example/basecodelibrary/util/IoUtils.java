package com.example.basecodelibrary.util;

import android.database.Cursor;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipFile;

/**
 * @author: robin
 * @description:
 * @date: 2017/7/15
 **/
public class IoUtils {
    public static void resetQuietly(InputStream is) {
        try {
            if (is instanceof FileInputStream){
                FileInputStream fis = (FileInputStream)is ;
                FileChannel fileChannel = fis.getChannel();
                if (fileChannel != null){
                    fileChannel.position(0);
                }
            }else {
                is.reset();
            }
        } catch (Exception ignored) {

        }
    }

    public static void closeQuietly(ZipFile closeable_obj) {
        if (closeable_obj == null){
            return ;
        }
        try {
            closeable_obj.close();
        } catch (RuntimeException rethrown) {
        } catch (Exception ignored) {
        }
    }

    public static void closeQuietly(Closeable closeable_obj) {
        if (closeable_obj == null){
            return ;
        }
        try {
            closeable_obj.close();
        } catch (RuntimeException rethrown) {
        } catch (Exception ignored) {
        }
    }

    public static void closeQuietly(Cursor closeable_obj) {
        if (closeable_obj == null){
            return ;
        }
        try {
            closeable_obj.close();
        } catch (RuntimeException rethrown) {
        } catch (Exception ignored) {
        }
    }

    // public static void closeQuietly(InputStream reader){
    // try {
    // reader.close();
    // } catch (RuntimeException rethrown) {
    // throw rethrown;
    // } catch (Exception ignored) {
    // }
    // }
    //
    // public static void closeQuietly(OutputStream writer){
    // try {
    // writer.close();
    // } catch (RuntimeException rethrown) {
    // throw rethrown;
    // } catch (Exception ignored) {
    // }
    // }

    public static boolean sync(FileOutputStream stream) {
        try {
            if (stream != null) {
                stream.getFD().sync();
            }
            return true;
        } catch (IOException e) {
        }
        return false;
    }

    public static int copy(InputStream in, OutputStream out) throws IOException {
        int total = 0;
        byte[] buffer = new byte[8192];
        int c;
        while ((c = in.read(buffer)) != -1) {
            total += c;
            out.write(buffer, 0, c);
        }
        return total;
    }
}
