package com.example.demo.controller;

import com.example.demo.model.Destination;
import com.example.demo.service.DestinationService;
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
 * 目的地控制器
 * 提供目的地的REST API接口
 */
@RestController
@RequestMapping("/api/destinations")
@CrossOrigin(origins = "*")
public class DestinationController {

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private UserBehaviorService behaviorService;

    /**
     * 获取所有目的地
     * GET /api/destinations
     */
    @GetMapping
    public ResponseEntity<List<Destination>> getAllDestinations(HttpServletRequest request) {
        List<Destination> destinations = destinationService.getAllDestinations();

        // 记录用户行为
        String sessionId = request.getSession().getId();
        behaviorService.recordPageView(null, "目的地列表", sessionId);

        return ResponseEntity.ok(destinations);
    }

    /**
     * 根据ID获取目的地详情
     * GET /api/destinations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDestinationById(@PathVariable Long id, HttpServletRequest request) {
        Destination destination = destinationService.getDestinationById(id);

        // 记录访问
        destinationService.recordVisit(id);

        // 记录用户行为
        String sessionId = request.getSession().getId();
        behaviorService.recordPageView(null, "目的地详情-" + destination.getName(), sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", destination);

        return ResponseEntity.ok(response);
    }

    /**
     * 搜索目的地
     * GET /api/destinations/search?location=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<Destination>> searchDestinations(
            @RequestParam String location,
            HttpServletRequest request) {

        List<Destination> destinations = destinationService.searchByLocation(location);

        // 记录搜索行为
        String sessionId = request.getSession().getId();
        behaviorService.recordSearch(null, location, sessionId);

        return ResponseEntity.ok(destinations);
    }

    /**
     * 获取高评分目的地
     * GET /api/destinations/high-rated?minRating=4.0
     */
    @GetMapping("/high-rated")
    public ResponseEntity<List<Destination>> getHighRatedDestinations(
            @RequestParam(defaultValue = "4.0") Double minRating) {
        List<Destination> destinations = destinationService.getHighRatedDestinations(minRating);
        return ResponseEntity.ok(destinations);
    }

    /**
     * 获取热门目的地
     * GET /api/destinations/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Destination>> getPopularDestinations() {
        List<Destination> destinations = destinationService.getPopularDestinations();
        return ResponseEntity.ok(destinations);
    }

    /**
     * 创建目的地
     * POST /api/destinations
     */
    @PostMapping
    public ResponseEntity<Destination> createDestination(@Valid @RequestBody Destination destination) {
        Destination created = destinationService.createDestination(destination);
        return ResponseEntity.ok(created);
    }

    /**
     * 更新目的地
     * PUT /api/destinations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Destination> updateDestination(
            @PathVariable Long id,
            @RequestBody Destination destination) {
        Destination updated = destinationService.updateDestination(id, destination);
        return ResponseEntity.ok(updated);
    }

    /**
     * 删除目的地
     * DELETE /api/destinations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDestination(@PathVariable Long id) {
        destinationService.deleteDestination(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "目的地已删除");

        return ResponseEntity.ok(response);
    }
}
