package com.example.basecodelibrary.util;


import android.content.Context;
import android.net.Uri;

public class UItools {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int re = ( int ) (dipValue * scale + 0.5f);
        return re;
    }

    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        float ret=  (pxValue / scale + 0.5f);
        return ret;
    }

    public static float px2sp(Context context, int pxValue){
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        float ret=  (pxValue / scale + 0.5f);
        return ret;
    }

    public static float dip2sp(Context context , float dipValue){
        float value = px2sp(context, dip2px(context, dipValue) );
        return value ;
    }

    public static void printLog(String TAG, String str) {
        if (CustomLog.isPrintLog) {
            if (str == null)
            {
                return;
            }
            try
            {
                int presize = 3 * 1024;
                for (int i = 0; i < (str.length() / presize) + 1; i++)
                {
                    int startpos = presize * i;
                    int endpos = Math.min(startpos + presize, str.length());
                    CustomLog.v(TAG, str.substring(startpos, endpos));
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

}
