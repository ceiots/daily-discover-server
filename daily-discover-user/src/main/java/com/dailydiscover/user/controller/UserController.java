package com.dailydiscover.user.controller;

import com.dailydiscover.common.result.Result;
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
    public ResponseEntity<Result<UserResponse>> register(@RequestBody User user) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.register", user, null);
            UserResponse userResponse = userService.register(user);
            LogTracer.traceMethod("UserController.register", user, userResponse);
            LogTracer.tracePerformance("UserController.register", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(Result.success("注册成功", userResponse));
        } catch (Exception e) {
            LogTracer.traceException("UserController.register", user, e);
            Result<UserResponse> error = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody Map<String, String> loginRequest) {
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
            
            Result<Map<String, Object>> result = Result.success("登录成功", data);
            LogTracer.traceMethod("UserController.login", phone, result);
            LogTracer.tracePerformance("UserController.login", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("UserController.login", loginRequest, e);
            Result<Map<String, Object>> result = Result.failure(e.getMessage());
            
            // 根据错误类型返回不同的HTTP状态码
            if (e.getMessage().contains("用户不存在")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            } else if (e.getMessage().contains("密码错误")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            } else {
                return ResponseEntity.badRequest().body(result);
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
    public ResponseEntity<Result<UserResponse>> getUserInfo() {
        long startTime = System.currentTimeMillis();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();
            
            LogTracer.traceMethod("UserController.getUserInfo", phone, null);
            UserResponse userResponse = userService.getUserByPhone(phone);
            
            Result<UserResponse> result = Result.success("获取用户信息成功", userResponse);
            LogTracer.traceMethod("UserController.getUserInfo", phone, result);
            LogTracer.tracePerformance("UserController.getUserInfo", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("UserController.getUserInfo", null, e);
            Result<UserResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    /**
     * 根据手机号获取用户信息
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<Result<UserResponse>> getUserByPhone(@PathVariable String phone) {
        try {
            UserResponse userResponse = userService.getUserByPhone(phone);
            Result<UserResponse> result = Result.success("获取用户信息成功", userResponse);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("根据手机号获取用户信息失败: {}", e.getMessage());
            Result<UserResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<Result<UserResponse>> updateUserProfile(@PathVariable Long id, @RequestBody User user) {
        long startTime = System.currentTimeMillis();
        try {
            user.setId(id);
            LogTracer.traceMethod("UserController.updateUserProfile", user, null);
            UserResponse userResponse = userService.updateUserProfile(user);
            Result<UserResponse> result = Result.success("资料更新成功", userResponse);
            LogTracer.traceMethod("UserController.updateUserProfile", user, result);
            LogTracer.tracePerformance("UserController.updateUserProfile", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("UserController.updateUserProfile", user, e);
            Result<UserResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 更新用户积分
     */
    @PutMapping("/{id}/points")
    public ResponseEntity<Result<UserResponse>> updateUserPoints(@PathVariable Long id, @RequestParam Integer points) {
        try {
            UserResponse userResponse = userService.updateUserPoints(id, points);
            Result<UserResponse> result = Result.success("积分更新成功", userResponse);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("更新用户积分失败: {}", e.getMessage());
            Result<UserResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Boolean>> deleteUser(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.deleteUser", id, null);
            boolean result = userService.deleteUser(id);
            Result<Boolean> response = result ? Result.success("用户删除成功", true) : Result.failure("用户删除失败");
            LogTracer.traceMethod("UserController.deleteUser", id, response);
            LogTracer.tracePerformance("UserController.deleteUser", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserController.deleteUser", id, e);
            Result<Boolean> response = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<Result<UserResponse>> getCurrentUser() {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.getCurrentUser", null, null);
            
            // 从SecurityContext中获取认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                Result<UserResponse> result = Result.failure("用户未认证");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }
            
            // 获取用户名（即phone）
            String phone = authentication.getName();
            
            UserResponse userResponse = userService.getUserByPhone(phone);
            if (userResponse == null) {
                Result<UserResponse> result = Result.failure("用户不存在");
                LogTracer.traceMethod("UserController.getCurrentUser", phone, result);
                LogTracer.tracePerformance("UserController.getCurrentUser", startTime, System.currentTimeMillis());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }
            
            Result<UserResponse> result = Result.success("获取用户信息成功", userResponse);
            LogTracer.traceMethod("UserController.getCurrentUser", phone, result);
            LogTracer.tracePerformance("UserController.getCurrentUser", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LogTracer.traceException("UserController.getCurrentUser", null, e);
            Result<UserResponse> result = Result.failure(e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

}