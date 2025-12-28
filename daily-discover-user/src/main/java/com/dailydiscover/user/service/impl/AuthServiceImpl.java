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
import com.dailydiscover.common.util.LogTracer;
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
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessOperation("开始用户登录流程，手机号: " + request.getPhone());
        
        // 记录登录尝试
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setIdentifier(request.getPhone());
        // IP地址和设备信息在实际应用中应从请求中获取
        // 这里暂时留空，待后续从请求上下文中获取
        loginAttempt.setCreatedAt(LocalDateTime.now());
        
        // 根据手机号查找用户
        LogTracer.traceBusinessOperation("根据手机号查找用户: " + request.getPhone());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        
        User user = userMapper.selectOne(queryWrapper);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE phone = ?", request.getPhone(), user);
        
        if (user == null) {
            // 记录失败的登录尝试
            loginAttempt.setResult("失败");
            loginAttempt.setFailureReason("用户不存在");
            loginAttemptMapper.insert(loginAttempt);
            LogTracer.traceDatabaseQuery("INSERT INTO login_attempt", loginAttempt, 1);
            
            LogTracer.traceBusinessException(new RuntimeException("用户不存在，手机号: " + request.getPhone()));
            
            // 返回认证失败的响应而不是抛出异常
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("用户不存在");
            LogTracer.traceBusinessMethod(request, response);
            return response;
        }
        
        // 验证密码
        LogTracer.traceBusinessOperation("验证用户密码");
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 记录失败的登录尝试
            loginAttempt.setUserId(user.getId());
            loginAttempt.setResult("失败");
            loginAttempt.setFailureReason("密码错误");
            loginAttemptMapper.insert(loginAttempt);
            LogTracer.traceDatabaseQuery("INSERT INTO login_attempt", loginAttempt, 1);
            
            LogTracer.traceBusinessException(new RuntimeException("密码错误，用户ID: " + user.getId()));
            
            // 返回认证失败的响应而不是抛出异常
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("密码错误");
            LogTracer.traceBusinessMethod(request, response);
            return response;
        }
        
        // 记录成功的登录尝试
        LogTracer.traceBusinessOperation("密码验证成功，记录登录尝试");
        loginAttempt.setUserId(user.getId());
        loginAttempt.setResult("成功");
        loginAttemptMapper.insert(loginAttempt);
        LogTracer.traceDatabaseQuery("INSERT INTO login_attempt", loginAttempt, 1);
        
        // 更新最后登录时间
        LogTracer.traceBusinessOperation("更新用户最后登录时间");
        user.setLastLoginAt(LocalDateTime.now());
        int updateResult = userMapper.updateById(user);
        LogTracer.traceDatabaseQuery("UPDATE user SET last_login_at", user, updateResult);
        
        // 生成Token
        LogTracer.traceBusinessOperation("生成JWT Token和刷新令牌");
        String token = jwtUtil.generateToken(user);
        String refreshTokenValue = jwtUtil.generateRefreshToken(user);
        
        // 创建并保存刷新令牌到数据库
        LogTracer.traceBusinessOperation("创建并保存刷新令牌");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenValue);
        // 设备信息和IP地址在实际应用中应从请求中获取
        // 这里暂时留空，待后续从请求上下文中获取
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7天有效期
        refreshToken.setCreatedAt(LocalDateTime.now());
        
        int refreshTokenResult = refreshTokenMapper.insert(refreshToken);
        LogTracer.traceDatabaseQuery("INSERT INTO refresh_token", refreshToken, refreshTokenResult);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(refreshTokenValue);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        LogTracer.traceBusinessOperation("用户登录流程完成，用户ID: " + user.getId());
        LogTracer.traceBusinessPerformance(startTime);
        LogTracer.traceBusinessMethod(request, response);
        return response;
    }
    
    @Override
    public AuthResponse register(RegisterRequest request) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessOperation("开始用户注册流程，手机号: " + request.getPhone());
        
        // 验证密码一致性
        LogTracer.traceBusinessOperation("验证密码一致性");
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            LogTracer.traceBusinessException(new RuntimeException("密码验证失败: 密码和确认密码不一致"));
            throw new RuntimeException("密码和确认密码不一致");
        }
        LogTracer.traceBusinessOperation("密码验证通过");
        
        // 检查手机号是否已存在
        LogTracer.traceBusinessOperation("检查手机号是否已存在: " + request.getPhone());
        QueryWrapper<User> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("phone", request.getPhone());
        User existingUser = userMapper.selectOne(phoneQuery);
        if (existingUser != null) {
            LogTracer.traceBusinessException(new RuntimeException("手机号已存在，用户ID: " + existingUser.getId()));
            throw new RuntimeException("手机号已被注册");
        }
        LogTracer.traceBusinessOperation("手机号检查通过，可以注册新用户");
        
        // 创建新用户
        LogTracer.traceBusinessOperation("开始创建新用户");
        User user = new User();
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 设置昵称（如果未提供则使用手机号）
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
            LogTracer.traceBusinessOperation("使用用户提供的昵称: " + request.getNickname());
        } else {
            user.setNickname(request.getPhone());
            LogTracer.traceBusinessOperation("使用手机号作为默认昵称: " + request.getPhone());
        }
        
        user.setPoints(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        LogTracer.traceBusinessOperation("准备插入用户数据到数据库");
        int insertResult = userMapper.insert(user);
        LogTracer.traceDatabaseQuery("INSERT INTO user", user, insertResult);
        
        // 生成Token
        LogTracer.traceBusinessOperation("开始生成JWT Token");
        String token = jwtUtil.generateToken(user);
        String refreshTokenValue = jwtUtil.generateRefreshToken(user);
        LogTracer.traceBusinessOperation("Token生成完成");
        
        // 创建并保存刷新令牌到数据库
        LogTracer.traceBusinessOperation("创建刷新令牌");
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setToken(refreshTokenValue);
        // 设备信息和IP地址在实际应用中应从请求中获取
        // 这里暂时留空，待后续从请求上下文中获取
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 7天有效期
        refreshToken.setCreatedAt(LocalDateTime.now());
        
        LogTracer.traceBusinessOperation("准备插入刷新令牌到数据库");
        int refreshTokenResult = refreshTokenMapper.insert(refreshToken);
        LogTracer.traceDatabaseQuery("INSERT INTO refresh_token", refreshToken, refreshTokenResult);
        
        // 构建响应
        LogTracer.traceBusinessOperation("构建认证响应");
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(refreshTokenValue);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        LogTracer.traceBusinessOperation("用户注册流程完成，用户ID: " + user.getId() + ", 手机号: " + user.getPhone());
        LogTracer.traceBusinessPerformance(startTime);
        return response;
    }
    
    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessOperation("开始刷新令牌流程");
        
        // 从数据库中查找刷新令牌
        LogTracer.traceBusinessOperation("查找有效的刷新令牌");
        QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("token", request.getRefreshToken())
                   .gt("expires_at", LocalDateTime.now());
        
        RefreshToken refreshToken = refreshTokenMapper.selectOne(queryWrapper);
        LogTracer.traceDatabaseQuery("SELECT * FROM refresh_token WHERE token = ? AND expires_at > NOW()", request.getRefreshToken(), refreshToken);
        
        if (refreshToken == null) {
            LogTracer.traceBusinessException(new RuntimeException("刷新令牌无效或已过期"));
            throw new RuntimeException("刷新令牌无效或已过期");
        }
        
        // 获取用户信息
        LogTracer.traceBusinessOperation("获取用户信息，用户ID: " + refreshToken.getUserId());
        User user = userMapper.selectById(refreshToken.getUserId());
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", refreshToken.getUserId(), user);
        
        if (user == null) {
            LogTracer.traceBusinessException(new RuntimeException("用户不存在，用户ID: " + refreshToken.getUserId()));
            throw new RuntimeException("用户不存在");
        }
        
        // 生成新的Token和新的刷新令牌
        LogTracer.traceBusinessOperation("生成新的JWT Token和刷新令牌");
        String token = jwtUtil.generateToken(user);
        String newRefreshTokenValue = jwtUtil.generateRefreshToken(user);
        
        // 更新数据库中的刷新令牌
        LogTracer.traceBusinessOperation("更新刷新令牌");
        refreshToken.setToken(newRefreshTokenValue);
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(7)); // 重新设置7天有效期
        int updateResult = refreshTokenMapper.updateById(refreshToken);
        LogTracer.traceDatabaseQuery("UPDATE refresh_token SET token = ?, expires_at = ?", refreshToken, updateResult);
        
        AuthResponse response = new AuthResponse();
        response.setUser(user);
        response.setToken(token);
        response.setRefreshToken(newRefreshTokenValue);
        response.setExpiresIn(jwtUtil.getExpiration());
        
        LogTracer.traceBusinessOperation("刷新令牌流程完成，用户ID: " + user.getId());
        LogTracer.traceBusinessPerformance(startTime);
        LogTracer.traceBusinessMethod(request, response);
        return response;
    }
    
    @Override
    public void resetPassword(PasswordResetRequest request) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessOperation("开始重置密码流程，手机号: " + request.getPhone());
        
        // 验证密码一致性
        LogTracer.traceBusinessOperation("验证新密码和确认密码一致性");
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            LogTracer.traceBusinessException(new RuntimeException("新密码和确认密码不一致"));
            throw new RuntimeException("新密码和确认密码不一致");
        }
        
        // 根据手机号查找用户
        LogTracer.traceBusinessOperation("根据手机号查找用户: " + request.getPhone());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", request.getPhone());
        
        User user = userMapper.selectOne(queryWrapper);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE phone = ?", request.getPhone(), user);
        
        if (user == null) {
            LogTracer.traceBusinessException(new RuntimeException("手机号未注册: " + request.getPhone()));
            throw new RuntimeException("手机号未注册");
        }
        
        // 更新密码
        LogTracer.traceBusinessOperation("更新用户密码，用户ID: " + user.getId());
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        
        int updateResult = userMapper.updateById(user);
        LogTracer.traceDatabaseQuery("UPDATE user SET password = ?, updated_at = ?", user, updateResult);
        
        LogTracer.traceBusinessOperation("重置密码流程完成，用户ID: " + user.getId());
        LogTracer.traceBusinessPerformance(startTime);
    }
    
    @Override
    public User verifyToken(String token) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessOperation("开始验证令牌");
        
        if (!jwtUtil.validateToken(token)) {
            LogTracer.traceBusinessException(new RuntimeException("令牌无效或已过期"));
            throw new RuntimeException("令牌无效或已过期");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(token);
        LogTracer.traceBusinessOperation("从令牌中提取用户ID: " + userId);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", userId, user);
        
        LogTracer.traceBusinessOperation("令牌验证完成，用户ID: " + userId);
        LogTracer.traceBusinessPerformance(startTime);
        return user;
    }
    
    @Override
    public User getCurrentUser(String token) {
        LogTracer.traceBusinessOperation("获取当前用户信息");
        return verifyToken(token);
    }
    
    @Override
    public void logout(String token) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessOperation("开始用户登出流程");
        
        if (StringUtils.hasText(token)) {
            try {
                // 从令牌中获取用户ID
                Long userId = jwtUtil.getUserIdFromToken(token);
                LogTracer.traceBusinessOperation("从令牌中提取用户ID: " + userId);
                
                // 删除该用户的所有刷新令牌
                LogTracer.traceBusinessOperation("删除用户所有刷新令牌，用户ID: " + userId);
                QueryWrapper<RefreshToken> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("user_id", userId);
                int deleteResult = refreshTokenMapper.delete(queryWrapper);
                LogTracer.traceDatabaseQuery("DELETE FROM refresh_token WHERE user_id = ?", userId, deleteResult);
                
                LogTracer.traceBusinessOperation("用户登出完成，删除刷新令牌数量: " + deleteResult);
                
            } catch (Exception e) {
                // 如果令牌无效，忽略错误，直接完成登出
                LogTracer.traceBusinessException(new RuntimeException("令牌无效，直接完成登出"));
            }
        } else {
            LogTracer.traceBusinessOperation("令牌为空，直接完成登出");
        }
        
        LogTracer.traceBusinessPerformance(startTime);
    }
}