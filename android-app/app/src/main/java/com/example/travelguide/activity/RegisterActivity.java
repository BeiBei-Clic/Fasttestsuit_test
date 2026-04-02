package com.example.travelguide.activity;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册页面Activity
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText etUsername, etPassword, etConfirmPassword, etEmail;
    private Button btnRegister;
    private ProgressBar progressBar;
    private ApiService apiService;
    private BehaviorTracker behaviorTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        initData();
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void initData() {
        apiService = ApiClient.getInstance().getApiService();
        behaviorTracker = BehaviorTracker.getInstance(this);
    }

    /**
     * 尝试注册
     */
    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // 输入验证
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("请输入用户名");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("请输入密码");
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("密码长度不能少于6位");
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("两次密码输入不一致");
            return;
        }

        // 显示加载
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // 构建注册请求
        User user = new User(username, password, email);

        // 发送注册请求
        apiService.register(user).enqueue(new Callback<ApiService.ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<User>> call,
                                   Response<ApiService.ApiResponse<User>> response) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<User> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // 记录注册行为
                        behaviorTracker.trackBehavior(
                                BehaviorTracker.ACTION_REGISTER,
                                "用户注册成功: " + username,
                                "注册页面"
                        );

                        Log.d(TAG, "注册成功: " + username);
                        Toast.makeText(RegisterActivity.this, "注册成功，请登录", Toast.LENGTH_SHORT).show();

                        // 返回登录页面
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<User>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);
                Log.e(TAG, "注册失败: " + t.getMessage());
                Toast.makeText(RegisterActivity.this,
                        "网络错误: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        behaviorTracker.trackPageView("注册页面");
    }
}
