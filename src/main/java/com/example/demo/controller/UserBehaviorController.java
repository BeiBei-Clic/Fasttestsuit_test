package com.example.demo.controller;

import com.example.demo.model.UserBehavior;
import com.example.demo.service.UserBehaviorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户行为分析控制器
 * 提供用户行为记录和分析的REST API接口
 * 供测试工具分析用户行为数据
 */
@RestController
@RequestMapping("/api/behavior")
@CrossOrigin(origins = "*")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService behaviorService;

    /**
     * 记录用户行为
     * POST /api/behavior/record
     */
    @PostMapping("/record")
    public ResponseEntity<Map<String, Object>> recordBehavior(
            @RequestBody Map<String, Object> behaviorData,
            HttpServletRequest request) {

        Long userId = behaviorData.get("userId") != null ?
                Long.valueOf(behaviorData.get("userId").toString()) : null;
        String actionType = (String) behaviorData.get("actionType");
        String actionDetail = (String) behaviorData.get("actionDetail");
        String pageName = (String) behaviorData.get("pageName");

        String sessionId = request.getSession().getId();
        String deviceInfo = request.getHeader("User-Agent");

        UserBehavior behavior = behaviorService.recordBehavior(
                userId, actionType, actionDetail, pageName, sessionId, deviceInfo);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", behavior);

        return ResponseEntity.ok(response);
    }

    /**
     * 记录页面访问
     * POST /api/behavior/page-view
     */
    @PostMapping("/page-view")
    public ResponseEntity<Map<String, Object>> recordPageView(
            @RequestBody Map<String, Object> pageData,
            HttpServletRequest request) {

        Long userId = pageData.get("userId") != null ?
                Long.valueOf(pageData.get("userId").toString()) : null;
        String pageName = (String) pageData.get("pageName");

        String sessionId = request.getSession().getId();

        UserBehavior behavior = behaviorService.recordPageView(userId, pageName, sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", behavior);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户行为记录
     * GET /api/behavior/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBehavior>> getUserBehaviors(@PathVariable Long userId) {
        List<UserBehavior> behaviors = behaviorService.getUserBehaviors(userId);
        return ResponseEntity.ok(behaviors);
    }

    /**
     * 获取会话行为记录（页面访问顺序）
     * GET /api/behavior/session/{sessionId}
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSessionBehaviors(@PathVariable String sessionId) {
        List<UserBehavior> behaviors = behaviorService.getSessionBehaviors(sessionId);
        String pageSequence = behaviorService.getUserPageSequence(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("behaviors", behaviors);
        response.put("pageSequence", pageSequence);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取某类行为记录
     * GET /api/behavior/type/{actionType}
     */
    @GetMapping("/type/{actionType}")
    public ResponseEntity<List<UserBehavior>> getBehaviorsByType(@PathVariable String actionType) {
        List<UserBehavior> behaviors = behaviorService.getBehaviorsByType(actionType);
        return ResponseEntity.ok(behaviors);
    }

    /**
     * 获取时间范围内的行为记录
     * GET /api/behavior/timerange?start=xxx&end=xxx
     */
    @GetMapping("/timerange")
    public ResponseEntity<List<UserBehavior>> getBehaviorsByTimeRange(
            @RequestParam String start,
            @RequestParam String end) {

        LocalDateTime startTime = LocalDateTime.parse(start);
        LocalDateTime endTime = LocalDateTime.parse(end);

        List<UserBehavior> behaviors = behaviorService.getBehaviorsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(behaviors);
    }

    /**
     * 获取用户行为统计
     * GET /api/behavior/statistics/{userId}
     */
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<Map<String, Object>> getBehaviorStatistics(@PathVariable Long userId) {
        Map<String, Long> statistics = behaviorService.getBehaviorStatistics(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("statistics", statistics);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有行为记录
     * GET /api/behavior/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserBehavior>> getAllBehaviors() {
        List<UserBehavior> behaviors = behaviorService.getBehaviorsByTimeRange(
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );
        return ResponseEntity.ok(behaviors);
    }
}
