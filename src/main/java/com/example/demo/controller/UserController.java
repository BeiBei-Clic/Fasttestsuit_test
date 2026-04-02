package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserBehaviorService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 提供用户注册、登录、信息管理等REST API接口
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserBehaviorService behaviorService;

    /**
     * 用户注册
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody User user,
            HttpServletRequest request) {

        User registered = userService.register(user);

        // 记录注册行为
        String sessionId = request.getSession().getId();
        behaviorService.recordBehavior(
                registered.getId(),
                UserBehaviorService.ACTION_REGISTER,
                "用户注册",
                "注册页面",
                sessionId,
                request.getHeader("User-Agent")
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "注册成功");
        response.put("data", sanitizeUser(registered));

        return ResponseEntity.ok(response);
    }

    /**
     * 用户登录
     * POST /api/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, String> loginForm,
            HttpServletRequest request) {

        String username = loginForm.get("username");
        String password = loginForm.get("password");

        User user = userService.login(username, password);

        // 记录登录行为
        String sessionId = request.getSession().getId();
        behaviorService.recordBehavior(
                user.getId(),
                UserBehaviorService.ACTION_LOGIN,
                "用户登录",
                "登录页面",
                sessionId,
                request.getHeader("User-Agent")
        );

        // 保存用户ID到session
        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登录成功");
        response.put("data", sanitizeUser(user));

        return ResponseEntity.ok(response);
    }

    /**
     * 用户登出
     * POST /api/users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Long userId = null;

        if (session != null) {
            userId = (Long) session.getAttribute("userId");
            session.invalidate();
        }

        // 记录登出行为
        if (userId != null) {
            String sessionId = request.getSession().getId();
            behaviorService.recordBehavior(
                    userId,
                    UserBehaviorService.ACTION_LOGOUT,
                    "用户登出",
                    "首页",
                    sessionId,
                    null
            );
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "登出成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前登录用户信息
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "未登录");
            return ResponseEntity.status(401).body(response);
        }

        Long userId = (Long) session.getAttribute("userId");
        User user = userService.getUserById(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", sanitizeUser(user));

        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户信息
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", sanitizeUser(user));

        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有用户（管理员功能）
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        users.forEach(this::sanitizeUser);
        return ResponseEntity.ok(users);
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody User userDetails,
            HttpServletRequest request) {

        User updated = userService.updateUser(id, userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "更新成功");
        response.put("data", sanitizeUser(updated));

        return ResponseEntity.ok(response);
    }

    /**
     * 修改密码
     * PUT /api/users/{id}/password
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> passwordForm) {

        String oldPassword = passwordForm.get("oldPassword");
        String newPassword = passwordForm.get("newPassword");

        userService.changePassword(id, oldPassword, newPassword);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "密码修改成功");

        return ResponseEntity.ok(response);
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "用户已删除");

        return ResponseEntity.ok(response);
    }

    /**
     * 清除敏感信息
     */
    private User sanitizeUser(User user) {
        user.setPassword(null);
        return user;
    }
}
