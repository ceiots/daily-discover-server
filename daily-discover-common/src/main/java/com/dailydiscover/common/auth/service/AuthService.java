package com.dailydiscover.common.auth.service;

import com.dailydiscover.common.auth.dto.AuthResponse;
import com.dailydiscover.common.auth.dto.LoginRequest;
import com.dailydiscover.common.auth.dto.RegisterRequest;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * 刷新Token
     */
    AuthResponse refreshToken(String refreshToken);

    /**
     * 用户登出
     */
    void logout(String token);

    /**
     * 验证用户凭证
     */
    boolean validateCredentials(String username, String password);
}