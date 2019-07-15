package com.example.basecodelibrary;

import android.app.Application;
import android.graphics.Color;

import com.example.basecodelibrary.anrwatchdog.AnrError;
import com.example.basecodelibrary.anrwatchdog.AnrWatchDog;
import com.example.basecodelibrary.util.CustomLog;
import com.example.basecodelibrary.watermark.WaterMarkHelper;

/**
 * @author: robin
 * @description:
 * @date: 2017/7/1
 **/
public class BaseApplication extends Application {
    private static final String TAG = "BaseApplication";
    private WaterMarkHelper mWaterMarkHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        //AnrWatchDog使用
        AnrWatchDog watchDog = new AnrWatchDog(5000);
        watchDog.setReportMainThreadOnly();
        watchDog.setReportThreadNamePrefix("ANR");
        watchDog.setANRListener(new AnrWatchDog.ANRListener() {
            @Override
            public void onAppNotResponding(AnrError error) {
                error.printStackTrace();
                CustomLog.i(TAG, "get ANR ", error);
            }
        });
        watchDog.start();
        if (BuildConfig.DEBUG) {
            mWaterMarkHelper = new WaterMarkHelper();
            mWaterMarkHelper.installWaterMark(this, 0, "Debug测试版", 25 * 3f, Color.BLUE & 0x19ffffff);
        }
    }

    @Override
    public void onTerminate() {
        if (mWaterMarkHelper != null) {
            mWaterMarkHelper.uninstallWaterMark(this);
        }
        super.onTerminate();
    }
}
