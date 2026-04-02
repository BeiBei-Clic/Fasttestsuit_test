package com.example.travelguide.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * API客户端
 * 单例模式管理Retrofit实例
 */
public class ApiClient {

    // 后端服务器地址（请根据实际情况修改）
    private static final String BASE_URL = "http://10.0.2.2:8080/"; // Android模拟器访问本机

    private static ApiClient instance;
    private final ApiService apiService;
    private final OkHttpClient okHttpClient;

    private ApiClient() {
        // 配置日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 配置OkHttpClient
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        // 配置Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取单例实例
     */
    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    /**
     * 获取API服务
     */
    public ApiService getApiService() {
        return apiService;
    }

    /**
     * 获取OkHttpClient（用于配置Cookie等）
     */
    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }
}
