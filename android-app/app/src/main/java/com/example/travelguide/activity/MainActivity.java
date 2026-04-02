package com.example.travelguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.travelguide.R;
import com.example.travelguide.adapter.DestinationAdapter;
import com.example.travelguide.api.ApiClient;
import com.example.travelguide.api.ApiService;
import com.example.travelguide.model.Destination;
import com.example.travelguide.utils.BehaviorTracker;
import com.example.travelguide.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 主页面Activity
 * 显示目的地列表
 */
public class MainActivity extends AppCompatActivity implements DestinationAdapter.OnItemClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private DestinationAdapter adapter;
    private List<Destination> destinationList;
    private ApiService apiService;
    private BehaviorTracker behaviorTracker;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化
        initViews();
        initData();
        loadDestinations();
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        destinationList = new ArrayList<>();
        adapter = new DestinationAdapter(destinationList, this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener(this::loadDestinations);

        // 设置Toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        apiService = ApiClient.getInstance().getApiService();
        behaviorTracker = BehaviorTracker.getInstance(this);
        sessionManager = SessionManager.getInstance(this);

        // 检查登录状态
        if (!sessionManager.isLoggedIn()) {
            // 未登录，跳转到登录页面
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // 设置用户ID到行为追踪器
            behaviorTracker.setUserId(sessionManager.getUserId());
        }
    }

    /**
     * 加载目的地列表
     */
    private void loadDestinations() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getAllDestinations().enqueue(new Callback<List<Destination>>() {
            @Override
            public void onResponse(Call<List<Destination>> call, Response<List<Destination>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    destinationList.clear();
                    destinationList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    // 记录页面访问
                    behaviorTracker.trackPageView("目的地列表");

                    Log.d(TAG, "加载了 " + destinationList.size() + " 个目的地");
                } else {
                    Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Destination>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "网络错误: " + t.getMessage());
                Toast.makeText(MainActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 目的地项点击事件
     */
    @Override
    public void onItemClick(Destination destination, int position) {
        // 记录点击行为
        behaviorTracker.trackClick("目的地-" + destination.getName(), "目的地列表");

        // 跳转到详情页面
        Intent intent = new Intent(this, DestinationDetailActivity.class);
        intent.putExtra("destination_id", destination.getId());
        intent.putExtra("destination_name", destination.getName());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        behaviorTracker.trackPageView("主页面");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            // 记录搜索行为
            behaviorTracker.trackClick("搜索按钮", "主页面");

            startActivity(new Intent(this, SearchActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            // 登出
            performLogout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * 执行登出
     */
    private void performLogout() {
        // 记录登出行为
        behaviorTracker.trackBehavior(BehaviorTracker.ACTION_LOGOUT, "用户登出", "主页面");

        // 清除会话
        sessionManager.logout();
        behaviorTracker.clearUserInfo();

        // 跳转到登录页面
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
