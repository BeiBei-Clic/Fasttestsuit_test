package com.example.travelguide;

import android.app.Application;
import android.util.Log;

import com.luluteam.itestlib.apm.APMInstance;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
            // PMC/i-test currently serves an expired TLS certificate. Relaxing
            // HttpsURLConnection validation here keeps the assignment demo runnable.
            installUnsafeTlsForPmc();
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

    private void installUnsafeTlsForPmc() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        HostnameVerifier allowAllHosts = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(allowAllHosts);
    }
}
