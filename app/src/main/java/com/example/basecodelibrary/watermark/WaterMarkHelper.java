package com.example.basecodelibrary.watermark;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.basecodelibrary.lifecycle.AbsActivityLifecycleCallbacks;
import com.example.basecodelibrary.util.BaseUtils;


/**
 * 水印助手，app全局添加
 */
public final class WaterMarkHelper extends AbsActivityLifecycleCallbacks {
    private final int VID_WATERMARK = (int)(System.identityHashCode(WaterMarkHelper.class) & 0x00ffffff) | 0x7f000000 ;
    private final int VID_HINT = VID_WATERMARK + 1;
    private final int VID_VER  = VID_HINT + 1 ;
    private int         mMarkRes ;
    private String mText;
    private float       mFontSize ;
    private int         mTextColor ;
    public void installWaterMark(Context context, int markRes, String text, float fontSize, int textColor){
        mMarkRes = markRes;
        mText = text;
        mFontSize = fontSize;
        mTextColor = textColor;
        Application app = (Application)context.getApplicationContext();
        app.registerActivityLifecycleCallbacks(this);
    }

    public void uninstallWaterMark(Context context){
        Application app = (Application)context.getApplicationContext();
        app.unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        super.onActivityResumed(activity);
        View waterMarkView = activity.findViewById(VID_WATERMARK);
        if (waterMarkView == null){
            FrameLayout decorView = (FrameLayout)activity.getWindow().getDecorView();
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            WaterMarkPannel waterMarkPannel = new WaterMarkPannel(activity);
            waterMarkPannel.setId(VID_WATERMARK);
            waterMarkPannel.setBackgroundResource(mMarkRes);
            decorView.addView(waterMarkPannel, lp);

            TextView markView = new TextView(activity);
            markView.setId(VID_HINT);
            markView.setText(mText);
            markView.setTextSize(mFontSize);
            markView.setTextColor(mTextColor);

            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            waterMarkPannel.addView(markView, rlp);

            TextView verView = new TextView(activity);
            verView.setId(VID_VER);
            verView.setText("V"+ String.valueOf(getAppVersion(activity)));
            verView.setTextSize(mFontSize/3);
            verView.setTextColor(mTextColor);
            rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.addRule(RelativeLayout.RIGHT_OF, VID_HINT);
            rlp.addRule(RelativeLayout.ALIGN_BASELINE, VID_HINT);
            waterMarkPannel.addView(verView, rlp);

        }else{
            waterMarkView.bringToFront();
        }
    }

    private int getAppVersion(Activity activity){
        PackageManager pm = activity.getPackageManager() ;
        PackageInfo packageInfo = BaseUtils.getPackageInfo(pm, activity.getPackageName(), 0);
        if (packageInfo == null){
            return -1;
        }else{
            return packageInfo.versionCode;
        }
    }

    private static class WaterMarkPannel extends RelativeLayout {
        //文字旋转角度
        private final int ROTATE_DEGREE = 10 ;
        private final double PI = 3.1415;

        public WaterMarkPannel(Context context) {
            super(context);
        }

        public WaterMarkPannel(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
        }

        public WaterMarkPannel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public WaterMarkPannel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = getMeasuredWidth();
            int height = getMeasuredHeight();
            double degree = ROTATE_DEGREE * PI /180 ;
            double adjHeight = width * Math.sin(degree) + height * Math.cos(degree);
            height = (int)adjHeight;
            setMeasuredDimension(getMeasuredWidth(), height);
        }

        @Override
        protected void dispatchDraw(Canvas canvas){
            canvas.save();
            canvas.rotate(-ROTATE_DEGREE, getWidth()/2f, getHeight()/2.0f);
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override
        protected void onDraw(Canvas canvas){
            canvas.save();
            canvas.rotate(-ROTATE_DEGREE, getWidth()/2f, getHeight()/2.0f);
            super.onDraw(canvas);
            canvas.restore();
        }
    }
}
