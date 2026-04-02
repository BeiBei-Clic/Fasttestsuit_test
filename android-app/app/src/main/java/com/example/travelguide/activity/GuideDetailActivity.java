package com.example.travelguide.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.travelguide.R;
import com.example.travelguide.api.ApiClient;
import com.example.travelguide.api.ApiService;
import com.example.travelguide.model.TravelGuide;
import com.example.travelguide.utils.BehaviorTracker;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 攻略详情页面Activity
 */
public class GuideDetailActivity extends AppCompatActivity {

    private static final String TAG = "GuideDetail";

    private TextView tvTitle, tvAuthor, tvViews, tvLikes;
    private TextView tvDuration, tvBudget, tvSeason, tvContent;
    private Button btnLike;
    private ProgressBar progressBar;

    private ApiService apiService;
    private BehaviorTracker behaviorTracker;
    private Long guideId;
    private TravelGuide currentGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_detail);

        // 获取传递的数据
        guideId = getIntent().getLongExtra("guide_id", -1);
        String guideTitle = getIntent().getStringExtra("guide_title");

        initViews(guideTitle);
        initData();
        loadGuideDetail();
    }

    private void initViews(String title) {
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvViews = findViewById(R.id.tvViews);
        tvLikes = findViewById(R.id.tvLikes);
        tvDuration = findViewById(R.id.tvDuration);
        tvBudget = findViewById(R.id.tvBudget);
        tvSeason = findViewById(R.id.tvSeason);
        tvContent = findViewById(R.id.tvContent);
        btnLike = findViewById(R.id.btnLike);
        progressBar = findViewById(R.id.progressBar);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnLike.setOnClickListener(v -> likeGuide());
    }

    private void initData() {
        apiService = ApiClient.getInstance().getApiService();
        behaviorTracker = BehaviorTracker.getInstance(this);
    }

    /**
     * 加载攻略详情
     */
    private void loadGuideDetail() {
        progressBar.setVisibility(View.VISIBLE);

        apiService.getGuideById(guideId).enqueue(new Callback<ApiService.ApiResponse<TravelGuide>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<TravelGuide>> call,
                                   Response<ApiService.ApiResponse<TravelGuide>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiService.ApiResponse<TravelGuide> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        currentGuide = apiResponse.getData();
                        displayGuide(currentGuide);

                        // 记录页面访问
                        behaviorTracker.trackPageView("攻略详情-" + currentGuide.getTitle());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<TravelGuide>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "加载失败: " + t.getMessage());
                Toast.makeText(GuideDetailActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示攻略信息
     */
    private void displayGuide(TravelGuide guide) {
        tvTitle.setText(guide.getTitle());
        tvAuthor.setText("作者: " + guide.getAuthor());
        tvViews.setText("浏览: " + guide.getViews());
        tvLikes.setText("点赞: " + guide.getLikes());
        tvDuration.setText("推荐行程: " + (guide.getTravelDuration() != null ? guide.getTravelDuration() : "未知"));
        tvBudget.setText("预算: " + (guide.getEstimatedBudget() != null ? "¥" + guide.getEstimatedBudget() : "未知"));
        tvSeason.setText("最佳季节: " + (guide.getBestSeason() != null ? guide.getBestSeason() : "未知"));
        tvContent.setText(guide.getContent());
    }

    /**
     * 点赞攻略
     */
    private void likeGuide() {
        apiService.likeGuide(guideId).enqueue(new Callback<ApiService.ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiService.ApiResponse<Void>> call,
                                   Response<ApiService.ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // 更新点赞数
                    if (currentGuide != null) {
                        currentGuide.setLikes(currentGuide.getLikes() + 1);
                        tvLikes.setText("点赞: " + currentGuide.getLikes());
                    }

                    // 记录点赞行为
                    behaviorTracker.trackLike("攻略-" + currentGuide.getTitle(), "攻略详情");

                    Toast.makeText(GuideDetailActivity.this, "点赞成功", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiService.ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "点赞失败: " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        behaviorTracker.trackPageView("攻略详情");
    }
}
