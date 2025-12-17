package com.dailydiscover.service;

import com.dailydiscover.user.dto.*;
import com.dailydiscover.user.entity.User;

/**
 * 认证服务接口
 */
public interface AuthService {
    
    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest request);
    
    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);
    
    /**
     * 刷新Token
     */
    AuthResponse refreshToken(TokenRefreshRequest request);
    
    /**
     * 重置密码
     */
    void resetPassword(PasswordResetRequest request);
    
    /**
     * 验证Token
     */
    User verifyToken(String token);
    
    /**
     * 获取当前用户信息
     */
    User getCurrentUser(String token);
    
    /**
     * 用户登出
     */
    void logout(String token);
}