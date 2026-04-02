package com.example.travelguide;

import android.app.Application;
import android.util.Log;

import com.luluteam.itestlib.apm.APMInstance;

/**
 * 应用程序入口类
 * 在这里初始化Fasttestsuit SDK
 */
public class TravelGuideApplication extends Application {

    private static final String TAG = "TravelGuideApp";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            APMInstance.getInstance().start(this);
            Log.i(TAG, "Fasttestsuit SDK initialized");
        } catch (Throwable t) {
            Log.e(TAG, "Fasttestsuit SDK initialization failed", t);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
