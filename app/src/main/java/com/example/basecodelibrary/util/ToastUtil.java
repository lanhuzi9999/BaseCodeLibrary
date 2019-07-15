package com.example.basecodelibrary.util;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;

import com.example.basecodelibrary.R;

/**
 * @author: robin
 * @description:
 * @date: 2015/7/15
 **/
public class ToastUtil {

    public static void showToast(final Context context, final CharSequence text){
        showToast(context, text, Gravity.CENTER);
    }

    public static void showToast(final Context context, final CharSequence text , final int gravity) {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity){
                    Activity activity = BaseUtils.getRootActivity((Activity)context);
                    if (activity.isFinishing()){
                        return ;
                    }
                }
                View view = LayoutInflater.from(context).inflate(R.layout.toast_layout, null);
                TextView textview = (TextView) view.findViewById(R.id.toasttext);
                RelativeLayout toastcontent = (RelativeLayout) view;
                ViewGroup.LayoutParams lp = toastcontent.getLayoutParams();
                if (lp != null) {
                    lp.width = LayoutParams.FILL_PARENT;
                } else {
                    toastcontent.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                }
                textview.setText(text);
                final Toast toast = new Toast(context);
                toast.setView(view);
                toast.setGravity(gravity, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            }
        };
        ThreadUtil.runOnUIThread(context, action);
    }

    public static void showCommonToast(final Context context, final int textid, final int duration) {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity){
                    Activity activity = BaseUtils.getRootActivity((Activity)context);
                    if (activity.isFinishing()){
                        return ;
                    }
                }
                Toast.makeText(context, context.getString(textid), duration).show();
            }
        };
        ThreadUtil.runOnUIThread(context, action);
    }

    public static void showCommonToast(final Context context, final String text, final int duration) {
        Runnable action = new Runnable() {
            @Override
            public void run() {
                if (context instanceof Activity){
                    Activity activity = BaseUtils.getRootActivity((Activity)context);
                    if (activity.isFinishing()){
                        return ;
                    }
                }
                Toast.makeText(context, text, duration).show();
            }
        };
        ThreadUtil.runOnUIThread(context, action);
    }
}
