package com.dailydiscover.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.dailydiscover.user.dto.*;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.service.AuthService;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            
            // 检查登录是否成功，如果不成功则返回错误状态码
            if (!response.isSuccess()) {
                // 统一使用业务错误码，HTTP状态码统一为422（Unprocessable Entity）
                Map<String, Object> error = new HashMap<>();
                error.put("code", getErrorCode(response.getMessage()));
                error.put("message", response.getMessage());
                
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
            }
            
            // 登录成功，直接返回数据，无需包装success字段
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 系统异常使用500
            Map<String, Object> error = new HashMap<>();
            error.put("code", "SYSTEM_ERROR");
            error.put("message", "系统异常，请稍后重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
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
    public ResponseEntity<?> register(@Validated @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            
            // 检查注册是否成功
            if (!response.isSuccess()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", getErrorCode(response.getMessage()));
                error.put("message", response.getMessage());
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
            }
            
            // 注册成功，直接返回数据
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", "REGISTRATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Validated @RequestBody TokenRefreshRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request);
            
            // 检查刷新是否成功
            if (!response.isSuccess()) {
                Map<String, Object> error = new HashMap<>();
                error.put("code", getErrorCode(response.getMessage()));
                error.put("message", response.getMessage());
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
            }
            
            // 刷新成功，直接返回数据
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", "TOKEN_REFRESH_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Validated @RequestBody PasswordResetRequest request) {
        try {
            authService.resetPassword(request);
            
            // 重置成功，返回简单消息
            Map<String, Object> result = new HashMap<>();
            result.put("message", "密码重置成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", "PASSWORD_RESET_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * 验证Token
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody String token) {
        try {
            User user = authService.verifyToken(token);
            
            // 验证成功，直接返回用户信息
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", "TOKEN_VERIFICATION_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            User user = authService.getCurrentUser(token);
            
            // 获取成功，直接返回用户信息
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", "USER_INFO_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            authService.logout(token);
            
            // 登出成功，返回简单消息
            Map<String, Object> result = new HashMap<>();
            result.put("message", "登出成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("code", "LOGOUT_ERROR");
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
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