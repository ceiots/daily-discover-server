package com.dailydiscover.user.controller;

import com.dailydiscover.user.dto.UserResponse;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.service.UserService;
import com.dailydiscover.user.util.JwtUtil;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody User user) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.register", user, null);
            UserResponse userResponse = userService.register(user);
            LogTracer.traceMethod("UserController.register", user, userResponse);
            LogTracer.tracePerformance("UserController.register", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            LogTracer.traceException("UserController.register", user, e);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        long startTime = System.currentTimeMillis();
        try {
            String phone = loginRequest.get("phone");
            String password = loginRequest.get("password");
            
            LogTracer.traceMethod("UserController.login", phone, null);
            UserResponse userResponse = userService.login(phone, password);
            
            // 生成JWT Token
            String token = jwtUtil.generateToken(userResponse.getId(), userResponse.getPhone());
            
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", userResponse);
            
            LogTracer.traceMethod("UserController.login", phone, data);
            LogTracer.tracePerformance("UserController.login", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            LogTracer.traceException("UserController.login", loginRequest, e);
            
            // 根据错误类型返回不同的HTTP状态码
            if (e.getMessage().contains("用户不存在")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } else if (e.getMessage().contains("密码错误")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
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
     * 获取用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInfo() {
        long startTime = System.currentTimeMillis();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();
            
            LogTracer.traceMethod("UserController.getUserInfo", phone, null);
            UserResponse userResponse = userService.getUserByPhone(phone);
            
            LogTracer.traceMethod("UserController.getUserInfo", phone, userResponse);
            LogTracer.tracePerformance("UserController.getUserInfo", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            LogTracer.traceException("UserController.getUserInfo", null, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 根据手机号获取用户信息
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<UserResponse> getUserByPhone(@PathVariable String phone) {
        try {
            UserResponse userResponse = userService.getUserByPhone(phone);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("根据手机号获取用户信息失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserResponse> updateUserProfile(@PathVariable Long id, @RequestBody User user) {
        long startTime = System.currentTimeMillis();
        try {
            user.setId(id);
            LogTracer.traceMethod("UserController.updateUserProfile", user, null);
            UserResponse userResponse = userService.updateUserProfile(user);
            LogTracer.traceMethod("UserController.updateUserProfile", user, userResponse);
            LogTracer.tracePerformance("UserController.updateUserProfile", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            LogTracer.traceException("UserController.updateUserProfile", user, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户积分
     */
    @PutMapping("/{id}/points")
    public ResponseEntity<UserResponse> updateUserPoints(@PathVariable Long id, @RequestParam Integer points) {
        try {
            UserResponse userResponse = userService.updateUserPoints(id, points);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("更新用户积分失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.deleteUser", id, null);
            boolean result = userService.deleteUser(id);
            LogTracer.traceMethod("UserController.deleteUser", id, result);
            LogTracer.tracePerformance("UserController.deleteUser", startTime, System.currentTimeMillis());
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceException("UserController.deleteUser", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.getCurrentUser", null, null);
            
            // 从SecurityContext中获取认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // 获取用户名（即phone）
            String phone = authentication.getName();
            
            UserResponse userResponse = userService.getUserByPhone(phone);
            if (userResponse == null) {
                LogTracer.traceMethod("UserController.getCurrentUser", phone, null);
                LogTracer.tracePerformance("UserController.getCurrentUser", startTime, System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            LogTracer.traceMethod("UserController.getCurrentUser", phone, userResponse);
            LogTracer.tracePerformance("UserController.getCurrentUser", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            LogTracer.traceException("UserController.getCurrentUser", null, e);
            return ResponseEntity.badRequest().build();
        }
    }

}