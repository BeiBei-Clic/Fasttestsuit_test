package com.example.travelguide.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.R;
import com.example.travelguide.adapter.GuideAdapter;
import com.example.travelguide.api.ApiClient;
import com.example.travelguide.api.ApiService;
import com.example.travelguide.model.Destination;
import com.example.travelguide.model.TravelGuide;
import com.example.travelguide.utils.BehaviorTracker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 目的地详情页面Activity
 */
public class DestinationDetailActivity extends AppCompatActivity implements GuideAdapter.OnItemClickListener {

    private static final String TAG = "DestinationDetail";

    private ImageView ivDestination;
    private TextView tvName, tvLocation, tvRating, tvDescription, tvVisitCount;
    private RecyclerView recyclerViewGuides;
    private ProgressBar progressBar;

    private GuideAdapter guideAdapter;
    private List<TravelGuide> guideList;
    private ApiService apiService;
    private BehaviorTracker behaviorTracker;
    private Long destinationId;
    private String destinationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_detail);

        // 获取传递的数据
        destinationId = getIntent().getLongExtra("destination_id", -1);
        destinationName = getIntent().getStringExtra("destination_name");

        initViews();
        initData();
        loadDestinationDetail();
        loadGuides();
    }

    private void initViews() {
        ivDestination = findViewById(R.id.ivDestination);
        tvName = findViewById(R.id.tvName);
        tvLocation = findViewById(R.id.tvLocation);
        tvRating = findViewById(R.id.tvRating);
        tvDescription = findViewById(R.id.tvDescription);
        tvVisitCount = findViewById(R.id.tvVisitCount);
        recyclerViewGuides = findViewById(R.id.recyclerViewGuides);
        progressBar = findViewById(R.id.progressBar);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(destinationName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 设置攻略列表
        recyclerViewGuides.setLayoutManager(new LinearLayoutManager(this));
        guideList = new ArrayList<>();
        guideAdapter = new GuideAdapter(guideList, this);
        guideAdapter.setOnItemClickListener(this);
        recyclerViewGuides.setAdapter(guideAdapter);
    }

    private void initData() {
        apiService = ApiClient.getInstance().getApiService();
        behaviorTracker = BehaviorTracker.getInstance(this);
    }

    /**
     * 加载目的地详情
     */
    private void loadDestinationDetail() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getDestinationById(destinationId).enqueue(new Callback<ApiService.ApiResponse<Destination>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<Destination>> call,
                                   Response<ApiService.ApiResponse<Destination>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<Destination> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Destination destination = apiResponse.getData();
                        displayDestination(destination);

                        // 记录页面访问
                        behaviorTracker.trackPageView("目的地详情-" + destination.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<Destination>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "加载失败: " + t.getMessage());
            }
        });
    }

    /**
     * 显示目的地信息
     */
    private void displayDestination(Destination destination) {
        tvName.setText(destination.getName());
        tvLocation.setText(destination.getLocation());
        tvRating.setText(String.format("评分: %.1f", destination.getRating()));
        tvDescription.setText(destination.getDescription());
        tvVisitCount.setText("访问次数: " + destination.getVisitCount());
    }

    /**
     * 加载攻略列表
     */
    private void loadGuides() {
        apiService.getGuidesByDestination(destinationId).enqueue(new Callback<List<TravelGuide>>() {
            @Override
            public void onResponse(Call<List<TravelGuide>> call, Response<List<TravelGuide>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    guideList.clear();
                    guideList.addAll(response.body());
                    guideAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<TravelGuide>> call, Throwable t) {
                Log.e(TAG, "加载攻略失败: " + t.getMessage());
            }
        });
    }

    /**
     * 攻略项点击事件
     */
    @Override
    public void onItemClick(TravelGuide guide, int position) {
        // 记录点击行为
        behaviorTracker.trackClick("攻略-" + guide.getTitle(), "目的地详情");

        // 跳转到攻略详情
        Intent intent = new Intent(this, GuideDetailActivity.class);
        intent.putExtra("guide_id", guide.getId());
        intent.putExtra("guide_title", guide.getTitle());
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
        behaviorTracker.trackPageView("目的地详情");
    }
}
