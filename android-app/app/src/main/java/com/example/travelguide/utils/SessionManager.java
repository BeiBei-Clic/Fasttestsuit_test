package com.example.travelguide.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 会话管理工具类
 * 管理用户登录状态和会话信息
 */
public class SessionManager {

    private static final String PREF_NAME = "user_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_EMAIL = "email";

    private final SharedPreferences preferences;
    private final SharedPreferences.Editor editor;
    private static SessionManager instance;

    private SessionManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * 获取单例实例
     */
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * 创建登录会话
     */
    public void createLoginSession(Long userId, String username, String nickname, String email) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NICKNAME, nickname);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * 获取用户ID
     */
    public Long getUserId() {
        long id = preferences.getLong(KEY_USER_ID, -1);
        return id == -1 ? null : id;
    }

    /**
     * 获取用户名
     */
    public String getUsername() {
        return preferences.getString(KEY_USERNAME, null);
    }

    /**
     * 获取昵称
     */
    public String getNickname() {
        return preferences.getString(KEY_NICKNAME, null);
    }

    /**
     * 获取邮箱
     */
    public String getEmail() {
        return preferences.getString(KEY_EMAIL, null);
    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo(String nickname, String email) {
        editor.putString(KEY_NICKNAME, nickname);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    /**
     * 清除会话（登出）
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
