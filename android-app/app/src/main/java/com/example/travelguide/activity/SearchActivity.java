package com.example.travelguide.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.R;
import com.example.travelguide.adapter.DestinationAdapter;
import com.example.travelguide.api.ApiClient;
import com.example.travelguide.api.ApiService;
import com.example.travelguide.model.Destination;
import com.example.travelguide.utils.BehaviorTracker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 搜索页面Activity
 */
public class SearchActivity extends AppCompatActivity implements DestinationAdapter.OnItemClickListener {

    private static final String TAG = "SearchActivity";

    private EditText etSearch;
    private ImageButton btnSearch;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private DestinationAdapter adapter;
    private List<Destination> resultList;
    private ApiService apiService;
    private BehaviorTracker behaviorTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        initData();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultList = new ArrayList<>();
        adapter = new DestinationAdapter(resultList, this);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        // 设置搜索按钮
        btnSearch.setOnClickListener(v -> performSearch());

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("搜索目的地");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initData() {
        apiService = ApiClient.getInstance().getApiService();
        behaviorTracker = BehaviorTracker.getInstance(this);
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        String keyword = etSearch.getText().toString().trim();

        if (TextUtils.isEmpty(keyword)) {
            etSearch.setError("请输入搜索关键字");
            return;
        }

        // 记录搜索行为
        behaviorTracker.trackSearch(keyword);

        progressBar.setVisibility(View.VISIBLE);

        apiService.searchDestinations(keyword).enqueue(new Callback<List<Destination>>() {
            @Override
            public void onResponse(Call<List<Destination>> call, Response<List<Destination>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    resultList.clear();
                    resultList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    Log.d(TAG, "搜索结果: " + resultList.size() + " 条");

                    if (resultList.isEmpty()) {
                        Toast.makeText(SearchActivity.this, "没有找到相关结果", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Destination>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "搜索失败: " + t.getMessage());
                Toast.makeText(SearchActivity.this, "搜索失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Destination destination, int position) {
        // 记录点击行为
        behaviorTracker.trackClick("搜索结果-" + destination.getName(), "搜索页面");

        // 跳转到详情页面
        android.content.Intent intent = new android.content.Intent(this, DestinationDetailActivity.class);
        intent.putExtra("destination_id", destination.getId());
        intent.putExtra("destination_name", destination.getName());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        behaviorTracker.trackPageView("搜索页面");
    }
}
