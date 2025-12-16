package com.dailydiscover.common.auth.controller;

import com.dailydiscover.common.auth.dto.AuthResponse;
import com.dailydiscover.common.auth.dto.LoginRequest;
import com.dailydiscover.common.auth.dto.RegisterRequest;
import com.dailydiscover.common.auth.service.AuthService;
import com.dailydiscover.common.auth.util.JwtUtil;
import com.dailydiscover.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/common/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<Result<Map<String, Object>>> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("用户登录请求: {}", loginRequest.getUsername());
            AuthResponse authResponse = authService.login(loginRequest);
            
            // 生成JWT Token
            String token = jwtUtil.generateToken(authResponse.getUserId(), authResponse.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", authResponse);
            response.put("refreshToken", authResponse.getRefreshToken());
            
            log.info("用户登录成功: {}", loginRequest.getUsername());
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<Result<Map<String, Object>>> register(@RequestBody RegisterRequest registerRequest) {
        try {
            log.info("用户注册请求: {}", registerRequest.getUsername());
            AuthResponse authResponse = authService.register(registerRequest);
            
            // 生成JWT Token
            String token = jwtUtil.generateToken(authResponse.getUserId(), authResponse.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", authResponse);
            response.put("refreshToken", authResponse.getRefreshToken());
            
            log.info("用户注册成功: {}", registerRequest.getUsername());
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 验证Token
     */
    @PostMapping("/verify")
    public ResponseEntity<Result<Map<String, Object>>> verifyToken(@RequestParam String token) {
        try {
            log.debug("验证Token请求");
            boolean isValid = jwtUtil.validateToken(token);
            
            if (isValid) {
                String username = jwtUtil.getUsernameFromToken(token);
                Long userId = jwtUtil.getUserIdFromToken(token);
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("username", username);
                response.put("userId", userId);
                
                log.debug("Token验证成功: {}", username);
                return ResponseEntity.ok(Result.success(response));
            } else {
                log.warn("Token验证失败");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error("Token无效或已过期"));
            }
        } catch (Exception e) {
            log.error("Token验证异常: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error("Token验证失败"));
        }
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<Result<Map<String, Object>>> refreshToken(@RequestParam String refreshToken) {
        try {
            log.debug("刷新Token请求");
            AuthResponse authResponse = authService.refreshToken(refreshToken);
            
            // 生成新的JWT Token
            String newToken = jwtUtil.generateToken(authResponse.getUserId(), authResponse.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            response.put("user", authResponse);
            response.put("refreshToken", authResponse.getRefreshToken());
            
            log.debug("Token刷新成功: {}", authResponse.getUsername());
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            log.error("Token刷新失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(e.getMessage()));
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<Result<String>> logout(@RequestHeader("Authorization") String token) {
        try {
            log.debug("用户登出请求");
            authService.logout(token.replace("Bearer ", ""));
            log.debug("用户登出成功");
            return ResponseEntity.ok(Result.success("登出成功"));
        } catch (Exception e) {
            log.error("用户登出失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }
}