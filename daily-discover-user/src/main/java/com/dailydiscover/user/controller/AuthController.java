package com.dailydiscover.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.dailydiscover.user.dto.*;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.service.AuthService;
import com.dailydiscover.common.util.LogTracer;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证控制器 - RESTful风格
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    
    /**
     * 用户登录 - RESTful风格
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        long startTime = System.currentTimeMillis();
        try {
            AuthResponse response = authService.login(request);
            
            // 使用LogTracer记录业务API调用
            LogTracer.traceBusinessApiCall(response);
            // RESTful风格：直接返回数据，HTTP状态码表示结果
            if (response.isSuccess()) {
                
                // 登录成功，返回200 OK
                return ResponseEntity.ok(response);
            } else {
                
                
                // 业务错误：用户不存在、密码错误等，返回422 Unprocessable Entity
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
            }
        } catch (Exception e) {
            
            // 系统异常，返回500 Internal Server Error
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("系统异常，请稍后重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    

    
    /**
     * 用户注册 - RESTful风格
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody RegisterRequest request) {
        long startTime = System.currentTimeMillis();
        try {
            AuthResponse response = authService.register(request);
            
            // 使用LogTracer记录业务API调用
            LogTracer.traceBusinessApiCall(response);
            // RESTful风格：直接返回数据，HTTP状态码表示结果
            if (response.isSuccess()) {
                // 注册成功，返回201 Created
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                // 业务错误：用户已存在、验证失败等，返回422 Unprocessable Entity
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
            }
        } catch (Exception e) {
            // 系统异常，返回500 Internal Server Error
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("系统异常，请稍后重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 刷新Token - RESTful风格
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Validated @RequestBody TokenRefreshRequest request) {
        try {
            AuthResponse response = authService.refreshToken(request);
            
            // RESTful风格：直接返回数据
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
            }
        } catch (Exception e) {
            AuthResponse errorResponse = new AuthResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * 重置密码 - RESTful风格
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Validated @RequestBody PasswordResetRequest request) {
        try {
            authService.resetPassword(request);
            
            // RESTful风格：操作成功，返回204 No Content
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // 操作失败，返回400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    /**
     * 验证Token - RESTful风格
     */
    @PostMapping("/verify")
    public ResponseEntity<User> verifyToken(@RequestBody String token) {
        try {
            User user = authService.verifyToken(token);
            
            // RESTful风格：直接返回用户数据
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // 认证失败，返回401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * 获取当前用户信息 - RESTful风格
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            User user = authService.getCurrentUser(token);
            
            // RESTful风格：直接返回用户数据
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // 认证失败，返回401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    /**
     * 用户登出 - RESTful风格
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            authService.logout(token);
            
            // RESTful风格：操作成功，返回204 No Content
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            // 操作失败，返回400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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