package com.dailydiscover.controller;

import com.dailydiscover.common.Result;
import com.dailydiscover.dto.*;
import com.dailydiscover.entity.User;
import com.dailydiscover.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<AuthResponse> login(@Validated @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return Result.success(response);
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<AuthResponse> register(@Validated @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return Result.success(response);
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public Result<AuthResponse> refreshToken(@Validated @RequestBody TokenRefreshRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return Result.success(response);
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Validated @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return Result.success();
    }
    
    /**
     * 验证Token
     */
    @PostMapping("/verify")
    public Result<User> verifyToken(@RequestBody String token) {
        User user = authService.verifyToken(token);
        return Result.success(user);
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public Result<User> getCurrentUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        User user = authService.getCurrentUser(token);
        return Result.success(user);
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        authService.logout(token);
        return Result.success();
    }
    
    /**
     * 从请求中提取Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}