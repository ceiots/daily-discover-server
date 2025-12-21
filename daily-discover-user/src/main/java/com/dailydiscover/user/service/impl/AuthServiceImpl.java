package com.dailydiscover.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.user.dto.*;
import com.dailydiscover.user.entity.LoginAttempt;
import com.dailydiscover.user.entity.RefreshToken;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.mapper.LoginAttemptMapper;
import com.dailydiscover.user.mapper.RefreshTokenMapper;
import com.dailydiscover.user.mapper.UserMapper;
import com.dailydiscover.user.service.AuthService;
import com.dailydiscover.user.util.JwtUtil;
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
    private final RefreshTokenMapper refreshTokenMapper;
    private final LoginAttemptMapper loginAttemptMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    @Override
    public AuthResponse login(LoginRequest request) {
        // 记录登录尝试
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setIdentifier(request.getPhone());
        // IP地址和设备信息在实际应用中应从请求中获取
        // 这里暂时留空，待后续从请求上下文中获取
        loginAttempt.setCreatedAt(LocalDateTime.now());
        
        // 根据手机号或邮箱查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone())
                   .or()
                   .eq("email", request.getPhone());
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            // 记录失败的登录尝试
            loginAttempt.setResult("失败");
            loginAttempt.setFailureReason("用户不存在");
            loginAttemptMapper.insert(loginAttempt);
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 记录失败的登录尝试
            loginAttempt.setUserId(user.getId());
            loginAttempt.setResult("失败");
            loginAttempt.setFailureReason("密码错误");
            loginAttemptMapper.insert(loginAttempt);
            throw new RuntimeException("密码错误");
        }
        
        // 记录成功的登录尝试
        loginAttempt.setUserId(user.getId());
        loginAttempt.setResult("成功");
        loginAttemptMapper.insert(loginAttempt);
        
        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user);
        String refreshTokenValue = jwtUtil.generateRefreshToken(user);
        
        // 创建并保存刷新令牌到数据库
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenValue);
        // 设备信息和IP地址在实际应用中应从请求中获取
        // 这里暂时留空，待后续从请求上下文中获取
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7天有效期
        refreshToken.setCreatedAt(LocalDateTime.now());
        
        refreshTokenMapper.insert(refreshToken);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(refreshTokenValue);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        return response;
    }
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("密码和确认密码不一致");
        }
        
        // 检查手机号是否已存在
        QueryWrapper<User> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("phone", request.getPhone());
        if (userMapper.selectOne(phoneQuery) != null) {
            throw new RuntimeException("手机号已被注册");
        }
        
        // 创建新用户
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 设置昵称（如果未提供则使用手机号）
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        } else {
            user.setNickname(request.getPhone());
        }
        
        user.setPhone(request.getPhone());
        user.setPoints(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userMapper.insert(user);
        
        // 生成Token
        String token = jwtUtil.generateToken(user);
        String refreshTokenValue = jwtUtil.generateRefreshToken(user);
        
        // 创建并保存刷新令牌到数据库
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenValue);
        // 设备信息和IP地址在实际应用中应从请求中获取
        // 这里暂时留空，待后续从请求上下文中获取
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7天有效期
        refreshToken.setCreatedAt(LocalDateTime.now());
        
        refreshTokenMapper.insert(refreshToken);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(refreshTokenValue);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        return response;
    }
    
    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        // 从数据库中查找刷新令牌
        QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", request.getRefreshToken())
                   .gt("expires_at", LocalDateTime.now());
        
        RefreshToken refreshToken = refreshTokenMapper.selectOne(queryWrapper);
        if (refreshToken == null) {
            throw new RuntimeException("刷新令牌无效或已过期");
        }
        
        // 获取用户信息
        User user = userMapper.selectById(refreshToken.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 生成新的Token和新的刷新令牌
        String token = jwtUtil.generateToken(user);
        String newRefreshTokenValue = jwtUtil.generateRefreshToken(user);
        
        // 更新数据库中的刷新令牌
        refreshToken.setToken(newRefreshTokenValue);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 重新设置7天有效期
        refreshTokenMapper.updateById(refreshToken);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(newRefreshTokenValue);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        return response;
    }
    
    @Override
    public void resetPassword(PasswordResetRequest request) {
        // 验证密码一致性
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("新密码和确认密码不一致");
        }
        
        // 根据邮箱查找用户（暂时使用邮箱作为手机号输入）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getEmail());
        
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("手机号未注册");
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
        if (StringUtils.hasText(token)) {
            try {
                // 从令牌中获取用户ID
                Long userId = jwtUtil.getUserIdFromToken(token);
                
                // 删除该用户的所有刷新令牌
                QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId);
                refreshTokenMapper.delete(queryWrapper);
                
            } catch (Exception e) {
                // 如果令牌无效，忽略错误，直接完成登出
            }
        }
    }
}