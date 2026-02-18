package com.dailydiscover.user.controller;

import com.dailydiscover.user.dto.UserResponse;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.service.UserService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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



    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<UserResponse> getUserInfo() {
        long startTime = System.currentTimeMillis();
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String phone = authentication.getName();
            
            LogTracer.traceBusinessMethod(phone, null);
            UserResponse userResponse = userService.getUserByPhone(phone);
            
            LogTracer.traceBusinessMethod(phone, userResponse);
            LogTracer.traceBusinessPerformance(startTime);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
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
            LogTracer.traceBusinessMethod(user, null);
            UserResponse userResponse = userService.updateUserProfile(user);
            LogTracer.traceBusinessMethod(user, userResponse);
            LogTracer.traceBusinessPerformance(startTime);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 积分功能已移到独立的积分模块，此处暂时移除

    /**
     * 更新用户邮箱
     */
    @PutMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateUserEmail(@PathVariable Long id, @RequestParam String email) {
        try {
            User user = new User();
            user.setId(id);
            user.setEmail(email);
            user.setUpdatedAt(java.time.LocalDateTime.now());
            
            UserResponse userResponse = userService.updateUserProfile(user);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("更新用户邮箱失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户性别
     */
    @PutMapping("/{id}/gender")
    public ResponseEntity<UserResponse> updateUserGender(@PathVariable Long id, @RequestParam String gender) {
        try {
            User user = new User();
            user.setId(id);
            user.setGender(gender);
            user.setUpdatedAt(java.time.LocalDateTime.now());
            
            UserResponse userResponse = userService.updateUserProfile(user);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("更新用户性别失败: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户生日
     */
    @PutMapping("/{id}/birthday")
    public ResponseEntity<UserResponse> updateUserBirthday(@PathVariable Long id, @RequestParam String birthday) {
        try {
            User user = new User();
            user.setId(id);
            user.setBirthday(java.time.LocalDateTime.parse(birthday));
            user.setUpdatedAt(java.time.LocalDateTime.now());
            
            UserResponse userResponse = userService.updateUserProfile(user);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            log.error("更新用户生日失败: {}", e.getMessage());
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
            LogTracer.traceBusinessMethod(id, null);
            boolean result = userService.deleteUser(id);
            LogTracer.traceBusinessMethod(id, result);
            LogTracer.traceBusinessPerformance(startTime);
            return result ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
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
            LogTracer.traceBusinessMethod(null, null);
            
            // 从SecurityContext中获取认证信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // 获取用户名（即phone）
            String phone = authentication.getName();
            
            UserResponse userResponse = userService.getUserByPhone(phone);
            if (userResponse == null) {
                LogTracer.traceBusinessMethod(phone, null);
                LogTracer.traceBusinessPerformance(startTime);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            LogTracer.traceBusinessMethod(phone, userResponse);
            LogTracer.traceBusinessPerformance(startTime);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            return ResponseEntity.badRequest().build();
        }
    }

}