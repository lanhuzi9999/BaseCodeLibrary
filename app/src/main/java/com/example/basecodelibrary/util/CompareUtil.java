package com.example.basecodelibrary.util;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.text.TextUtils;

public class CompareUtil {
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
        } else if ((TextUtils.isEmpty(a) && TextUtils.isEmpty(b) || (a == null && TextUtils.isEmpty(b)) || (b == null && TextUtils
                .isEmpty(a)))) {
            return true;
        } else if ((a != null && !a.equals(b)) || (a == null && b != null)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean compareStringArray(String[] a, String[] b) {
        if (a == b) {
            return true;
        }
        int N1 = a != null ? a.length : 0;
        int N2 = b != null ? b.length : 0;
        if (N1 != N2) {
            return false;
        }
        int k = 0;
        String s;
        for (k = 0; k < N1; k++) {
            s = a[k];
            if (s != null && !s.equals(b[k]) || s == null && b[k] != null) {
                return false;
            }
        }
        return true;
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

}
