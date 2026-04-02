package com.example.travelguide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.travelguide.api.ApiClient;
import com.example.travelguide.api.ApiService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 用户行为追踪工具类
 * 用于记录用户在应用中的行为
 */
public class BehaviorTracker {

    private static final String TAG = "BehaviorTracker";
    private static final String PREF_NAME = "behavior_tracker";
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_USER_ID = "user_id";

    private final Context context;
    private final SharedPreferences preferences;
    private final ApiService apiService;
    private String sessionId;
    private Long userId;

    // 行为类型常量
    public static final String ACTION_VIEW = "VIEW";
    public static final String ACTION_CLICK = "CLICK";
    public static final String ACTION_SEARCH = "SEARCH";
    public static final String ACTION_SHARE = "SHARE";
    public static final String ACTION_LIKE = "LIKE";
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_PAGE_ENTER = "PAGE_ENTER";
    public static final String ACTION_PAGE_EXIT = "PAGE_EXIT";

    private static BehaviorTracker instance;

    private BehaviorTracker(Context context) {
        this.context = context.getApplicationContext();
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.apiService = ApiClient.getInstance().getApiService();
        initSession();
    }

    /**
     * 获取单例实例
     */
    public static synchronized BehaviorTracker getInstance(Context context) {
        if (instance == null) {
            instance = new BehaviorTracker(context);
        }
        return instance;
    }

    /**
     * 初始化会话
     */
    private void initSession() {
        sessionId = preferences.getString(KEY_SESSION_ID, null);
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            preferences.edit().putString(KEY_SESSION_ID, sessionId).apply();
        }

        long savedUserId = preferences.getLong(KEY_USER_ID, -1);
        if (savedUserId != -1) {
            userId = savedUserId;
        }
    }

    /**
     * 设置用户ID（登录后调用）
     */
    public void setUserId(Long userId) {
        this.userId = userId;
        if (userId != null) {
            preferences.edit().putLong(KEY_USER_ID, userId).apply();
        } else {
            preferences.edit().remove(KEY_USER_ID).apply();
        }
    }

    /**
     * 获取设备信息
     */
    private String getDeviceInfo() {
        return "Android " + Build.VERSION.RELEASE +
                ", " + Build.MANUFACTURER +
                " " + Build.MODEL;
    }

    /**
     * 记录页面访问
     */
    public void trackPageView(String pageName) {
        trackBehavior(ACTION_PAGE_ENTER, pageName, pageName);
    }

    /**
     * 记录点击行为
     */
    public void trackClick(String target, String pageName) {
        trackBehavior(ACTION_CLICK, target, pageName);
    }

    /**
     * 记录搜索行为
     */
    public void trackSearch(String keyword) {
        trackBehavior(ACTION_SEARCH, keyword, "搜索页面");
    }

    /**
     * 记录分享行为
     */
    public void trackShare(String content, String pageName) {
        trackBehavior(ACTION_SHARE, content, pageName);
    }

    /**
     * 记录点赞行为
     */
    public void trackLike(String content, String pageName) {
        trackBehavior(ACTION_LIKE, content, pageName);
    }

    /**
     * 记录用户行为（通用方法）
     */
    public void trackBehavior(String actionType, String actionDetail, String pageName) {
        Map<String, Object> behaviorData = new HashMap<>();
        behaviorData.put("userId", userId);
        behaviorData.put("actionType", actionType);
        behaviorData.put("actionDetail", actionDetail);
        behaviorData.put("pageName", pageName);
        behaviorData.put("sessionId", sessionId);
        behaviorData.put("deviceInfo", getDeviceInfo());

        // 异步发送到服务器
        apiService.recordBehavior(behaviorData).enqueue(new Callback<ApiService.ApiResponse<UserBehavior>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<UserBehavior>> call,
                                   Response<ApiService.ApiResponse<UserBehavior>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "行为记录成功: " + actionType + " - " + actionDetail);
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<UserBehavior>> call, Throwable t) {
                Log.e(TAG, "行为记录失败: " + t.getMessage());
            }
        });

        // 本地日志
        Log.i(TAG, String.format("[%s] %s on %s (Session: %s)",
                actionType, actionDetail, pageName, sessionId));
    }

    /**
     * 开始新会话
     */
    public void startNewSession() {
        sessionId = UUID.randomUUID().toString();
        preferences.edit().putString(KEY_SESSION_ID, sessionId).apply();
        Log.d(TAG, "新会话开始: " + sessionId);
    }

    /**
     * 清除用户信息（登出时调用）
     */
    public void clearUserInfo() {
        setUserId(null);
        startNewSession();
    }

    /**
     * 获取当前会话ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 获取当前用户ID
     */
    public Long getUserId() {
        return userId;
    }
}

// 引入UserBehavior类
import com.example.travelguide.model.UserBehavior;
