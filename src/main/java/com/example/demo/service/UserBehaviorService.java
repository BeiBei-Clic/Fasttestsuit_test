package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.UserBehavior;
import com.example.demo.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户行为服务类
 * 记录和分析用户行为数据
 */
@Service
public class UserBehaviorService {

    @Autowired
    private UserBehaviorRepository behaviorRepository;

    @Autowired
    private UserService userService;

    // 行为类型常量
    public static final String ACTION_VIEW = "VIEW";
    public static final String ACTION_CLICK = "CLICK";
    public static final String ACTION_SEARCH = "SEARCH";
    public static final String ACTION_SHARE = "SHARE";
    public static final String ACTION_LIKE = "LIKE";
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_PAGE_ENTER = "PAGE_ENTER";
    public static final String ACTION_PAGE_EXIT = "PAGE_EXIT";

    /**
     * 记录用户行为
     */
    @Transactional
    public UserBehavior recordBehavior(Long userId, String actionType, String actionDetail,
                                        String pageName, String sessionId, String deviceInfo) {
        UserBehavior behavior = new UserBehavior();

        if (userId != null) {
            User user = userService.getUserById(userId);
            behavior.setUser(user);
        }

        behavior.setActionType(actionType);
        behavior.setActionDetail(actionDetail);
        behavior.setPageName(pageName);
        behavior.setSessionId(sessionId);
        behavior.setDeviceInfo(deviceInfo);

        return behaviorRepository.save(behavior);
    }

    /**
     * 记录页面访问（简化方法）
     */
    @Transactional
    public UserBehavior recordPageView(Long userId, String pageName, String sessionId) {
        return recordBehavior(userId, ACTION_PAGE_ENTER, pageName, pageName, sessionId, null);
    }

    /**
     * 记录搜索行为
     */
    @Transactional
    public UserBehavior recordSearch(Long userId, String keyword, String sessionId) {
        return recordBehavior(userId, ACTION_SEARCH, keyword, "搜索页面", sessionId, null);
    }

    /**
     * 记录点击行为
     */
    @Transactional
    public UserBehavior recordClick(Long userId, String target, String pageName, String sessionId) {
        return recordBehavior(userId, ACTION_CLICK, target, pageName, sessionId, null);
    }

    /**
     * 获取用户所有行为记录
     */
    public List<UserBehavior> getUserBehaviors(Long userId) {
        return behaviorRepository.findByUserId(userId);
    }

    /**
     * 获取某类行为记录
     */
    public List<UserBehavior> getBehaviorsByType(String actionType) {
        return behaviorRepository.findByActionType(actionType);
    }

    /**
     * 获取会话行为记录
     */
    public List<UserBehavior> getSessionBehaviors(String sessionId) {
        return behaviorRepository.findBySessionId(sessionId);
    }

    /**
     * 获取时间范围内的行为记录
     */
    public List<UserBehavior> getBehaviorsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return behaviorRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * 统计用户行为类型分布
     */
    public Map<String, Long> getBehaviorStatistics(Long userId) {
        List<UserBehavior> behaviors = getUserBehaviors(userId);
        Map<String, Long> statistics = new HashMap<>();

        for (UserBehavior behavior : behaviors) {
            String actionType = behavior.getActionType();
            statistics.put(actionType, statistics.getOrDefault(actionType, 0L) + 1);
        }

        return statistics;
    }

    /**
     * 获取用户访问页面顺序
     */
    public String getUserPageSequence(String sessionId) {
        List<UserBehavior> behaviors = getSessionBehaviors(sessionId);
        StringBuilder sequence = new StringBuilder();

        for (int i = 0; i < behaviors.size(); i++) {
            UserBehavior behavior = behaviors.get(i);
            if (ACTION_PAGE_ENTER.equals(behavior.getActionType())) {
                if (sequence.length() > 0) {
                    sequence.append(" -> ");
                }
                sequence.append(behavior.getPageName());
            }
        }

        return sequence.toString();
    }
}
