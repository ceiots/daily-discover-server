package com.dailydiscover.controller;

import com.dailydiscover.common.Result;
import com.dailydiscover.dto.LoginRequest;
import com.dailydiscover.dto.RegisterRequest;
import com.dailydiscover.dto.UserResponse;
import com.dailydiscover.entity.User;
import com.dailydiscover.service.UserService;
import com.dailydiscover.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * 
 * @author Daily Discover Team
 * @since 2024-01-01
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            log.info("收到用户注册请求: {}", request.getUsername());
            UserResponse response = userService.register(request);
            return Result.success("注册成功", response);
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("收到用户登录请求: {}", request.getUsername());
            UserResponse response = userService.login(request);
            return Result.success("登录成功", response);
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return Result.badRequest(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public Result<UserResponse> getProfile(HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId(request);
            UserResponse response = userService.getUserById(userId);
            return Result.success(response);
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public Result<UserResponse> updateProfile(@RequestBody User user, HttpServletRequest request) {
        try {
            Long userId = getCurrentUserId(request);
            UserResponse response = userService.updateUser(userId, user);
            return Result.success("更新成功", response);
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<Void> changePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            String oldPassword = request.get("oldPassword");
            String newPassword = request.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return Result.badRequest("参数不完整");
            }
            
            userService.changePassword(userId, oldPassword, newPassword);
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            log.error("修改密码失败: {}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    /**
     * 检查用户名是否可用
     */
    @GetMapping("/check-username")
    public Result<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean exists = userService.isUsernameExists(username);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", !exists);
        return Result.success(result);
    }

    /**
     * 检查邮箱是否可用
     */
    @GetMapping("/check-email")
    public Result<Map<String, Boolean>> checkEmail(@RequestParam String email) {
        boolean exists = userService.isEmailExists(email);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", !exists);
        return Result.success(result);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        // JWT是无状态的，客户端删除token即可
        return Result.success("登出成功", null);
    }

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("未登录或token已过期");
        }
        return jwtUtil.getUserIdFromToken(token);
    }

    /**
     * 从请求头中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}