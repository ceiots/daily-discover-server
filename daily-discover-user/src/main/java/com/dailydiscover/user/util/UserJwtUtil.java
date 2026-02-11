package com.dailydiscover.user.util;

import com.dailydiscover.common.security.JwtUtil;
import com.dailydiscover.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 用户服务JWT工具类 - 专注于用户相关的Token生成
 * 验证和解析功能使用common项目的通用JWT工具
 */
@Component
@RequiredArgsConstructor
public class UserJwtUtil {

    private final JwtUtil jwtUtil;

    /**
     * 为用户生成JWT Token
     */
    public String generateToken(User user) {
        return jwtUtil.generateToken(user.getId(), user.getPhone());
    }

    /**
     * 为用户生成刷新Token
     */
    public String generateRefreshToken(User user) {
        return jwtUtil.generateRefreshToken(user.getId(), user.getPhone());
    }

    /**
     * 验证Token有效性（使用通用验证）
     */
    public Boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    /**
     * 验证Token（带用户手机号）
     */
    public Boolean validateToken(String token, String phone) {
        return jwtUtil.validateToken(token, phone);
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }

    /**
     * 从Token中获取手机号
     */
    public String getPhoneFromToken(String token) {
        return jwtUtil.getPhoneFromToken(token);
    }

    /**
     * 验证刷新Token
     */
    public Boolean validateRefreshToken(String refreshToken) {
        return jwtUtil.validateRefreshToken(refreshToken);
    }

    /**
     * 从刷新Token中获取用户ID
     */
    public Long getUserIdFromRefreshToken(String refreshToken) {
        return jwtUtil.getUserIdFromToken(refreshToken);
    }

    /**
     * 刷新Token
     */
    public String refreshToken(String token) {
        return jwtUtil.refreshToken(token);
    }

    /**
     * 获取Token过期时间
     */
    public Long getExpiration() {
        return jwtUtil.getExpiration();
    }
}