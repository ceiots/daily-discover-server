package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.dto.*;
import com.dailydiscover.entity.User;
import com.dailydiscover.mapper.UserMapper;
import com.dailydiscover.service.AuthService;
import com.dailydiscover.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 认证服务实现类
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public AuthResponse login(LoginRequest request) {
        // 根据用户名或邮箱查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", request.getUsername())
                   .or()
                   .eq("email", request.getUsername());
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        return response;
    }
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("密码和确认密码不一致");
        }
        
        // 检查用户名是否已存在
        QueryWrapper<User> usernameQuery = new QueryWrapper<>();
        usernameQuery.eq("username", request.getUsername());
        if (userMapper.selectOne(usernameQuery) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        QueryWrapper<User> emailQuery = new QueryWrapper<>();
        emailQuery.eq("email", request.getEmail());
        if (userMapper.selectOne(emailQuery) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 设置昵称（如果未提供则使用用户名）
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        } else {
            user.setNickname(request.getUsername());
        }
        
        user.setPhone(request.getPhone());
        user.setPoints(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        return response;
    }
    
    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        // 验证刷新令牌
        if (!jwtUtil.validateRefreshToken(request.getRefreshToken())) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }
        
        // 从刷新令牌中获取用户ID
        Long userId = jwtUtil.getUserIdFromRefreshToken(request.getRefreshToken());
        User user = userMapper.selectById(userId);
        
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 生成新的Token
        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        return response;
    }
    
    @Override
    public void resetPassword(PasswordResetRequest request) {
        // 验证密码一致性
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("新密码和确认密码不一致");
        }
        
        // 根据邮箱查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", request.getEmail());
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("邮箱未注册");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.updateById(user);
    }
    
    @Override
    public User verifyToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("令牌无效或已过期");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        return userMapper.selectById(userId);
    }
    
    @Override
    public User getCurrentUser(String token) {
        return verifyToken(token);
    }
    
    @Override
    public void logout(String token) {
        // 在实际应用中，可以将令牌加入黑名单
        // 这里简单实现，直接验证令牌有效性
        if (StringUtils.hasText(token)) {
            jwtUtil.validateToken(token);
        }
    }
}