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
    public ResponseEntity<Map<String, Object>> login(@Validated @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            
            // 检查登录是否成功，如果不成功则返回错误状态码
            if (!response.isSuccess()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", response.getMessage());
                
                // 根据错误消息推断错误类型
                String message = response.getMessage();
                String errorCode = "GENERAL_ERROR"; // 默认错误码
                
                // 根据错误消息推断错误类型
                if (message != null) {
                    if (message.contains("用户不存在")) {
                        errorCode = "USER_NOT_FOUND";
                    } else if (message.contains("密码")) {
                        errorCode = "INVALID_PASSWORD";
                    } else if (message.contains("锁定")) {
                        errorCode = "ACCOUNT_LOCKED";
                    }
                }
                
                result.put("errorCode", errorCode);
                
                // 更灵活的错误码匹配，支持多种可能的错误码格式
                if (errorCode != null) {
                    if (errorCode.contains("NOT_FOUND") || errorCode.contains("USER_NOT_FOUND") || 
                        errorCode.contains("NOT_EXIST") || (message != null && message.contains("用户不存在"))) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
                    } else if (errorCode.contains("INVALID_PASSWORD") || errorCode.contains("PASSWORD_ERROR") || 
                               (message != null && message.contains("密码"))) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
                    } else if (errorCode.contains("ACCOUNT_LOCKED") || errorCode.contains("LOCKED")) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
                    }
                }
                
                // 默认返回400
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "系统异常，请稍后重试");
            result.put("errorCode", "SYSTEM_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Validated @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@Validated @RequestBody TokenRefreshRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(@Validated @RequestBody PasswordResetRequest request) {
        try {
            authService.resetPassword(request);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "密码重置成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 验证Token
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestBody String token) {
        try {
            User user = authService.verifyToken(token);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            User user = authService.getCurrentUser(token);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("data", user);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            authService.logout(token);
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "登出成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(result);
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