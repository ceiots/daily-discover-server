package com.example.domain.model.user;

/**
 * 密码加密器接口
 */
public interface PasswordEncoder {
    /**
     * 加密密码
     */
    String encode(String rawPassword);

    /**
     * 验证密码
     */
    boolean matches(String rawPassword, String encodedPassword);
} 