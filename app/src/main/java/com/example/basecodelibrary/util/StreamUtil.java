package com.example.basecodelibrary.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.res.AssetManager;

public class StreamUtil {
    private final static String TAG = "StreamUtil";

    /**
     * 读掉所有数据
     *
     * @param is
     */
    public static void drainInputStream(InputStream is) {
        if (is == null) {
            return;
        }
        byte[] buff = new byte[2048];
        try {
            while (is.read(buff, 0, buff.length) > 0) {
            }
        } catch (Exception e) {
        }
    }

    /**
     * 从一个输入流中根据一定的编码方式读取出内容文本
     *
     * @param in     InputStream，输入流
     * @param encode String，编码方式
     * @return String 内容文本
     * @throws UnsupportedEncodingException
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
        } catch (UnsupportedEncodingException e) {
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

    public static InputStream openAssetFile(Context context, String assetpath) throws IOException {
        int index = assetpath.indexOf('?');
        final AssetManager am = context.getAssets();
        String path = index > 0 ? BaseUtils.stripAnchor(assetpath.substring(BaseUtils.ASSET_BASE.length(), index))
                : BaseUtils.stripAnchor(assetpath.substring(BaseUtils.ASSET_BASE.length()));
        InputStream is = am.open(path);
        return is;
    }

    public static void printStreamLog(InputStream is) {
        if (is == null || !CustomLog.isPrintLog) {
            return;
        }
        CustomLog.d(TAG, "printStreamLog");
        BufferedReader reader = null;
        try {
//	    is.reset();
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                CustomLog.d(TAG, line);
            }
//	    is.reset();
            is.skip(0);//
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
