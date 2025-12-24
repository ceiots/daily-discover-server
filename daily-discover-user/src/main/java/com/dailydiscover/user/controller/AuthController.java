package com.dailydiscover.user.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.dailydiscover.user.dto.*;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.service.AuthService;
import com.dailydiscover.common.result.Result;

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
    public ResponseEntity<Result<AuthResponse>> login(@Validated @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            
            // 检查登录是否成功，如果不成功则返回错误状态码
            if (!response.isSuccess()) {
                // 统一使用业务错误码，HTTP状态码统一为422（Unprocessable Entity）
                Result<AuthResponse> result = Result.failure(response.getMessage());
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(result);
            }
            
            // 登录成功，使用Result包装
            Result<AuthResponse> result = Result.success("登录成功", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 系统异常使用500
            Result<AuthResponse> result = Result.failure("系统异常，请稍后重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 根据错误消息获取错误码
     */
    private String getErrorCode(String message) {
        if (message == null) {
            return "GENERAL_ERROR";
        }
        
        if (message.contains("用户不存在")) {
            return "USER_NOT_FOUND";
        } else if (message.contains("密码")) {
            return "INVALID_PASSWORD";
        } else if (message.contains("锁定")) {
            return "ACCOUNT_LOCKED";
        }
        
        return "GENERAL_ERROR";
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Result<AuthResponse>> register(@Validated @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            
            // 检查注册是否成功
            if (!response.isSuccess()) {
                Result<AuthResponse> result = Result.failure(response.getMessage());
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(result);
            }
            
            // 注册成功，使用Result包装
            Result<AuthResponse> result = Result.success("注册成功", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Result<AuthResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Result<AuthResponse>> refreshToken(@Validated @RequestBody TokenRefreshRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request);
            
            // 检查刷新是否成功
            if (!response.isSuccess()) {
                Result<AuthResponse> result = Result.failure(response.getMessage());
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(result);
            }
            
            // 刷新成功，使用Result包装
            Result<AuthResponse> result = Result.success("Token刷新成功", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Result<AuthResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Result<Boolean>> resetPassword(@Validated @RequestBody PasswordResetRequest request) {
        try {
            authService.resetPassword(request);
            
            // 重置成功，使用Result包装
            Result<Boolean> result = Result.success("密码重置成功", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Result<Boolean> result = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }
    
    /**
     * 验证Token
     */
    @PostMapping("/verify")
    public ResponseEntity<Result<User>> verifyToken(@RequestBody String token) {
        try {
            User user = authService.verifyToken(token);
            
            // 验证成功，使用Result包装
            Result<User> result = Result.success("Token验证成功", user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Result<User> result = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Result<User>> getCurrentUser(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            User user = authService.getCurrentUser(token);
            
            // 获取成功，使用Result包装
            Result<User> result = Result.success("获取用户信息成功", user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Result<User> result = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<Boolean>> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            authService.logout(token);
            
            // 登出成功，使用Result包装
            Result<Boolean> result = Result.success("登出成功", true);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Result<Boolean> result = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
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