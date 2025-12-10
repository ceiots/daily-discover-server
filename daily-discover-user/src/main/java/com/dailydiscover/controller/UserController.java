package com.dailydiscover.controller;

import com.dailydiscover.dto.UserResponse;
import com.dailydiscover.entity.User;
import com.dailydiscover.service.UserService;
import com.dailydiscover.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.register", user, null);
            UserResponse userResponse = userService.register(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("data", userResponse);
            LogTracer.traceMethod("UserController.register", user, response);
            LogTracer.tracePerformance("UserController.register", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserController.register", user, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.login", user, null);
            UserResponse userResponse = userService.login(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("data", userResponse);
            LogTracer.traceMethod("UserController.login", user, response);
            LogTracer.tracePerformance("UserController.login", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserController.login", user, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.getUserById", id, null);
            UserResponse userResponse = userService.getUserById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userResponse);
            LogTracer.traceMethod("UserController.getUserById", id, response);
            LogTracer.tracePerformance("UserController.getUserById", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserController.getUserById", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 根据邮箱获取用户信息
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getUserByEmail(@PathVariable String email) {
        try {
            UserResponse userResponse = userService.getUserByEmail(email);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", userResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("根据邮箱获取用户信息失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新用户资料
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@PathVariable Long id, @RequestBody User user) {
        long startTime = System.currentTimeMillis();
        try {
            user.setId(id);
            LogTracer.traceMethod("UserController.updateUserProfile", user, null);
            UserResponse userResponse = userService.updateUserProfile(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "资料更新成功");
            response.put("data", userResponse);
            LogTracer.traceMethod("UserController.updateUserProfile", user, response);
            LogTracer.tracePerformance("UserController.updateUserProfile", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserController.updateUserProfile", user, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 更新用户积分
     */
    @PutMapping("/{id}/points")
    public ResponseEntity<Map<String, Object>> updateUserPoints(@PathVariable Long id, @RequestParam Integer points) {
        try {
            UserResponse userResponse = userService.updateUserPoints(id, points);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "积分更新成功");
            response.put("data", userResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("更新用户积分失败: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        try {
            LogTracer.traceMethod("UserController.deleteUser", id, null);
            boolean result = userService.deleteUser(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", result);
            response.put("message", result ? "用户删除成功" : "用户删除失败");
            LogTracer.traceMethod("UserController.deleteUser", id, response);
            LogTracer.tracePerformance("UserController.deleteUser", startTime, System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            LogTracer.traceException("UserController.deleteUser", id, e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}