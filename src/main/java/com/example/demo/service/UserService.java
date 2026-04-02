package com.example.demo.service;

import com.example.demo.exception.AuthenticationFailedException;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务类
 * 提供用户注册、登录、信息管理等功能
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户注册
     */
    @Transactional
    public User register(User user) {
        // 校验用户名
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        // 校验密码
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new IllegalArgumentException("密码长度不能少于6位");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new DuplicateResourceException("用户", "用户名", user.getUsername());
        }

        // 检查邮箱是否已被使用
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                throw new DuplicateResourceException("用户", "邮箱", user.getEmail());
            }
        }

        // 设置默认昵称
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }

        return userRepository.save(user);
    }

    /**
     * 用户登录
     */
    @Transactional
    public User login(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationFailedException(username));

        // 简单的密码验证（实际项目中应该使用加密）
        if (!user.getPassword().equals(password)) {
            throw new AuthenticationFailedException(username);
        }

        // 记录登录信息
        user.recordLogin();
        userRepository.save(user);

        return user;
    }

    /**
     * 获取用户信息
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", id));
    }

    /**
     * 根据用户名获取用户
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("用户", "用户名", username));
    }

    /**
     * 获取所有用户
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);

        if (userDetails.getNickname() != null) {
            user.setNickname(userDetails.getNickname());
        }
        if (userDetails.getAvatar() != null) {
            user.setAvatar(userDetails.getAvatar());
        }
        if (userDetails.getEmail() != null) {
            // 检查邮箱是否被其他用户使用
            Optional<User> existingUser = userRepository.findByEmail(userDetails.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new DuplicateResourceException("用户", "邮箱", userDetails.getEmail());
            }
            user.setEmail(userDetails.getEmail());
        }

        return userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = getUserById(id);

        // 验证旧密码
        if (!user.getPassword().equals(oldPassword)) {
            throw new AuthenticationFailedException("旧密码错误", user.getUsername());
        }

        // 验证新密码
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("新密码长度不能少于6位");
        }

        user.setPassword(newPassword);
        userRepository.save(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
}
