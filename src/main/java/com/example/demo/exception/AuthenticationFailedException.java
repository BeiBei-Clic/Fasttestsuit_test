package com.example.demo.exception;

/**
 * 认证失败异常
 * 当用户登录认证失败时抛出
 */
public class AuthenticationFailedException extends RuntimeException {

    private String username;

    public AuthenticationFailedException(String username) {
        super("认证失败，用户名或密码错误: " + username);
        this.username = username;
    }

    public AuthenticationFailedException(String message, String username) {
        super(message);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
