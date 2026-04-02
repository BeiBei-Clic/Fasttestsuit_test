package com.example.travelguide;

import android.app.Application;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * 应用程序入口类
 *
 * 在这里初始化腾讯 Bugly SDK（用于移动端崩溃 / 异常 / ANR 采集与分析）。
 *
 * 历史说明：本作业原计划使用 Fasttestsuit (i-test.com.cn) 作为移动端 APM 工具，
 * 但实测发现该平台 TLS 证书已过期（2025-12-06 到期）且核心接口 loadSDKConfig
 * 对任意 appId 均返回 HTTP 500（服务端 NullPointerException），SDK 无法上报数据，
 * 故改用国内主流的同类工具腾讯 Bugly 完成移动端崩溃采集体验。
 */
public class TravelGuideApplication extends Application {

    private static final String TAG = "TravelGuideApp";

    /**
     * Bugly App ID（在 bugly.qq.com 创建应用后获得）
     */
    private static final String BUGLY_APP_ID = "3f957d9605";

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            // 初始化 Bugly：第三个参数为是否处于调试模式，true 时输出详细日志并加速上报
            CrashReport.initCrashReport(this, BUGLY_APP_ID, true);
            Log.i(TAG, "Bugly SDK initialized, appId=" + BUGLY_APP_ID);
        } catch (Throwable t) {
            Log.e(TAG, "Bugly SDK initialization failed", t);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
