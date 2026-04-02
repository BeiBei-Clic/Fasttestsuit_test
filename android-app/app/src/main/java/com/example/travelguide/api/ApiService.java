package com.example.travelguide.api;

import com.example.travelguide.model.Destination;
import com.example.travelguide.model.TravelGuide;
import com.example.travelguide.model.User;
import com.example.travelguide.model.UserBehavior;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * API服务接口
 * 定义所有后端API调用
 */
public interface ApiService {

    // ========== 用户相关API ==========

    /**
     * 用户注册
     */
    @POST("api/users/register")
    Call<ApiResponse<User>> register(@Body User user);

    /**
     * 用户登录
     */
    @POST("api/users/login")
    Call<ApiResponse<User>> login(@Body Map<String, String> loginForm);

    /**
     * 用户登出
     */
    @POST("api/users/logout")
    Call<ApiResponse<Void>> logout();

    /**
     * 获取当前用户信息
     */
    @GET("api/users/me")
    Call<ApiResponse<User>> getCurrentUser();

    /**
     * 更新用户信息
     */
    @PUT("api/users/{id}")
    Call<ApiResponse<User>> updateUser(@Path("id") Long id, @Body User user);

    // ========== 目的地相关API ==========

    /**
     * 获取所有目的地
     */
    @GET("api/destinations")
    Call<List<Destination>> getAllDestinations();

    /**
     * 获取目的地详情
     */
    @GET("api/destinations/{id}")
    Call<ApiResponse<Destination>> getDestinationById(@Path("id") Long id);

    /**
     * 搜索目的地
     */
    @GET("api/destinations/search")
    Call<List<Destination>> searchDestinations(@Query("location") String location);

    /**
     * 获取热门目的地
     */
    @GET("api/destinations/popular")
    Call<List<Destination>> getPopularDestinations();

    /**
     * 获取高评分目的地
     */
    @GET("api/destinations/high-rated")
    Call<List<Destination>> getHighRatedDestinations(@Query("minRating") double minRating);

    // ========== 攻略相关API ==========

    /**
     * 获取所有攻略
     */
    @GET("api/guides")
    Call<List<TravelGuide>> getAllGuides();

    /**
     * 获取攻略详情
     */
    @GET("api/guides/{id}")
    Call<ApiResponse<TravelGuide>> getGuideById(@Path("id") Long id);

    /**
     * 搜索攻略
     */
    @GET("api/guides/search")
    Call<List<TravelGuide>> searchGuides(@Query("keyword") String keyword);

    /**
     * 获取热门攻略
     */
    @GET("api/guides/popular")
    Call<List<TravelGuide>> getPopularGuides();

    /**
     * 获取某个目的地的攻略
     */
    @GET("api/guides/destination/{destinationId}")
    Call<List<TravelGuide>> getGuidesByDestination(@Path("destinationId") Long destinationId);

    /**
     * 点赞攻略
     */
    @POST("api/guides/{id}/like")
    Call<ApiResponse<Void>> likeGuide(@Path("id") Long id);

    // ========== 用户行为API ==========

    /**
     * 记录页面访问
     */
    @POST("api/behavior/page-view")
    Call<ApiResponse<UserBehavior>> recordPageView(@Body Map<String, Object> pageData);

    /**
     * 记录用户行为
     */
    @POST("api/behavior/record")
    Call<ApiResponse<UserBehavior>> recordBehavior(@Body Map<String, Object> behaviorData);

    /**
     * 获取用户行为记录
     */
    @GET("api/behavior/user/{userId}")
    Call<List<UserBehavior>> getUserBehaviors(@Path("userId") Long userId);

    /**
     * 获取会话行为记录
     */
    @GET("api/behavior/session/{sessionId}")
    Call<ApiResponse<List<UserBehavior>>> getSessionBehaviors(@Path("sessionId") String sessionId);

    // ========== 测试异常API ==========

    /**
     * 获取测试接口列表
     */
    @GET("api/test")
    Call<ApiResponse<Object>> getTestEndpoints();

    /**
     * 测试空指针异常
     */
    @GET("api/test/null-pointer")
    Call<ApiResponse<Object>> testNullPointerException(@Query("trigger") boolean trigger);

    /**
     * 测试数组越界异常
     */
    @GET("api/test/index-out-of-bounds")
    Call<ApiResponse<Object>> testIndexOutOfBoundsException(@Query("index") int index);

    /**
     * 测试算术异常
     */
    @GET("api/test/arithmetic")
    Call<ApiResponse<Object>> testArithmeticException(@Query("divisor") int divisor);

    /**
     * 测试资源未找到异常
     */
    @GET("api/test/resource-not-found")
    Call<ApiResponse<Object>> testResourceNotFoundException(@Query("id") long id);

    /**
     * 通用API响应类
     */
    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}
