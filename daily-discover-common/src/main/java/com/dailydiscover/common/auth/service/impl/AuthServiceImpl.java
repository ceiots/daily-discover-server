package com.dailydiscover.common.auth.service.impl;

import com.dailydiscover.common.auth.dto.AuthResponse;
import com.dailydiscover.common.auth.dto.LoginRequest;
import com.dailydiscover.common.auth.dto.RegisterRequest;
import com.dailydiscover.common.auth.entity.AuthUser;
import com.dailydiscover.common.auth.service.AuthService;
import com.dailydiscover.common.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;

    public AuthServiceImpl(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    // 模拟用户存储（实际项目中应使用数据库）
    private final Map<String, AuthUser> userStore = new ConcurrentHashMap<>();
    private final Map<String, String> refreshTokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> tokenBlacklist = new ConcurrentHashMap<>();

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        log.info("处理用户登录: {}", username);

        // 验证用户凭证
        if (!validateCredentials(username, password)) {
            throw new RuntimeException("用户名或密码错误");
        }

        AuthUser user = userStore.get(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成refresh token
        String refreshToken = generateRefreshToken();
        refreshTokenStore.put(refreshToken, username);

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .bio(user.getBio())
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String password = registerRequest.getPassword();
        String nickname = registerRequest.getNickname();

        log.info("处理用户注册: {}", username);

        // 检查用户名是否已存在
        if (userStore.containsKey(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建新用户
        AuthUser newUser = AuthUser.builder()
                .id(System.currentTimeMillis())
                .username(username)
                .password(password) // 实际项目中应对密码进行加密
                .nickname(nickname != null ? nickname : username)
                .phone(registerRequest.getPhone())
                .bio(registerRequest.getBio())
                .build();

        userStore.put(username, newUser);

        // 生成refresh token
        String refreshToken = generateRefreshToken();
        refreshTokenStore.put(refreshToken, username);

        return AuthResponse.builder()
                .userId(newUser.getId())
                .username(newUser.getUsername())
                .nickname(newUser.getNickname())
                .phone(newUser.getPhone())
                .bio(newUser.getBio())
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.debug("刷新Token: {}", refreshToken);

        String username = refreshTokenStore.get(refreshToken);
        if (username == null) {
            throw new RuntimeException("无效的refresh token");
        }

        AuthUser user = userStore.get(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 生成新的refresh token
        String newRefreshToken = generateRefreshToken();
        refreshTokenStore.remove(refreshToken);
        refreshTokenStore.put(newRefreshToken, username);

        return AuthResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .bio(user.getBio())
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    public void logout(String token) {
        log.debug("用户登出: {}", token);
        
        // 将token加入黑名单
        tokenBlacklist.put(token, "logout");
        
        // 清理refresh token
        String username = jwtUtil.getUsernameFromToken(token);
        if (username != null) {
            refreshTokenStore.entrySet().removeIf(entry -> entry.getValue().equals(username));
        }
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        AuthUser user = userStore.get(username);
        
        // 模拟用户数据（实际项目中应从数据库查询）
        if (user == null) {
            // 如果是第一次登录，创建默认用户
            if ("admin".equals(username) && "admin123".equals(password)) {
                AuthUser defaultUser = AuthUser.builder()
                        .id(1L)
                        .username("admin")
                        .password("admin123")
                        .nickname("管理员")
                        .phone("13800138000")
                        .bio("系统管理员")
                        .build();
                userStore.put("admin", defaultUser);
                return true;
            }
            return false;
        }
        
        return user.getPassword().equals(password);
    }

    /**
     * 检查token是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }

    private String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}