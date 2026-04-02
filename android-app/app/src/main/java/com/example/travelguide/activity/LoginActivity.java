package com.example.travelguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguide.R;
import com.example.travelguide.api.ApiClient;
import com.example.travelguide.api.ApiService;
import com.example.travelguide.model.User;
import com.example.travelguide.utils.BehaviorTracker;
import com.example.travelguide.utils.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 登录页面Activity
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SessionManager sessionManager;
    private BehaviorTracker behaviorTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        initData();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> attemptLogin());
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void initData() {
        apiService = ApiClient.getInstance().getApiService();
        sessionManager = SessionManager.getInstance(this);
        behaviorTracker = BehaviorTracker.getInstance(this);
    }

    /**
     * 尝试登录
     */
    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            return;
        }

        // 显示加载
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // 构建登录请求
        Map<String, String> loginForm = new HashMap<>();
        loginForm.put("username", username);
        loginForm.put("password", password);

        // 发送登录请求
        apiService.login(loginForm).enqueue(new Callback<ApiService.ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<User>> call,
                                   Response<ApiService.ApiResponse<User>> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<User> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        User user = apiResponse.getData();

                        // 保存会话
                        sessionManager.createLoginSession(
                                user.getId(),
                                user.getUsername(),
                                user.getNickname(),
                                user.getEmail()
                        );

                        // 设置用户ID到行为追踪器
                        behaviorTracker.setUserId(user.getId());

                        // 记录登录行为
                        behaviorTracker.trackBehavior(
                                BehaviorTracker.ACTION_LOGIN,
                                "用户登录成功",
                                "登录页面"
                        );

                        Log.d(TAG, "登录成功: " + user.getUsername());
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                        // 跳转到主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<User>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Log.e(TAG, "登录失败: " + t.getMessage());
                Toast.makeText(LoginActivity.this,
                        "网络错误: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        behaviorTracker.trackPageView("登录页面");
    }
}
