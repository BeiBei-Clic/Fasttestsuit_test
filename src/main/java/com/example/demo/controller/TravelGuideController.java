package com.example.demo.controller;

import com.example.demo.model.TravelGuide;
import com.example.demo.service.TravelGuideService;
import com.example.demo.service.UserBehaviorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 旅行攻略控制器
 * 提供攻略的REST API接口
 */
@RestController
@RequestMapping("/api/guides")
@CrossOrigin(origins = "*")
public class TravelGuideController {

    @Autowired
    private TravelGuideService guideService;

    @Autowired
    private UserBehaviorService behaviorService;

    /**
     * 获取所有攻略
     * GET /api/guides
     */
    @GetMapping
    public ResponseEntity<List<TravelGuide>> getAllGuides(HttpServletRequest request) {
        List<TravelGuide> guides = guideService.getAllGuides();

        // 记录用户行为
        String sessionId = request.getSession().getId();
        behaviorService.recordPageView(null, "攻略列表", sessionId);

        return ResponseEntity.ok(guides);
    }

    /**
     * 根据ID获取攻略详情
     * GET /api/guides/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGuideById(@PathVariable Long id, HttpServletRequest request) {
        TravelGuide guide = guideService.getGuideById(id);

        // 记录浏览
        guideService.recordView(id);

        // 记录用户行为
        String sessionId = request.getSession().getId();
        behaviorService.recordPageView(null, "攻略详情-" + guide.getTitle(), sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", guide);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取某个目的地的攻略
     * GET /api/guides/destination/{destinationId}
     */
    @GetMapping("/destination/{destinationId}")
    public ResponseEntity<List<TravelGuide>> getGuidesByDestination(
            @PathVariable Long destinationId,
            HttpServletRequest request) {

        List<TravelGuide> guides = guideService.getGuidesByDestination(destinationId);

        // 记录用户行为
        String sessionId = request.getSession().getId();
        behaviorService.recordClick(null, "查看目的地攻略", "目的地详情", sessionId);

        return ResponseEntity.ok(guides);
    }

    /**
     * 搜索攻略
     * GET /api/guides/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<TravelGuide>> searchGuides(
            @RequestParam String keyword,
            HttpServletRequest request) {

        List<TravelGuide> guides = guideService.searchGuides(keyword);

        // 记录搜索行为
        String sessionId = request.getSession().getId();
        behaviorService.recordSearch(null, keyword, sessionId);

        return ResponseEntity.ok(guides);
    }

    /**
     * 获取热门攻略
     * GET /api/guides/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<List<TravelGuide>> getPopularGuides() {
        List<TravelGuide> guides = guideService.getPopularGuides();
        return ResponseEntity.ok(guides);
    }

    /**
     * 创建攻略
     * POST /api/guides
     */
    @PostMapping
    public ResponseEntity<TravelGuide> createGuide(
            @Valid @RequestBody TravelGuide guide,
            @RequestParam(required = false) Long destinationId) {

        TravelGuide created = guideService.createGuide(guide, destinationId);
        return ResponseEntity.ok(created);
    }

    /**
     * 更新攻略
     * PUT /api/guides/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TravelGuide> updateGuide(
            @PathVariable Long id,
            @RequestBody TravelGuide guide) {

        TravelGuide updated = guideService.updateGuide(id, guide);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除攻略
     * DELETE /api/guides/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteGuide(@PathVariable Long id) {
        guideService.deleteGuide(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "攻略已删除");

        return ResponseEntity.ok(response);
    }

    /**
     * 点赞攻略
     * POST /api/guides/{id}/like
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> likeGuide(
            @PathVariable Long id,
            HttpServletRequest request) {

        guideService.likeGuide(id);

        // 记录用户行为
        String sessionId = request.getSession().getId();
        behaviorService.recordClick(null, "点赞攻略", "攻略详情", sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "点赞成功");

        return ResponseEntity.ok(response);
    }
}
