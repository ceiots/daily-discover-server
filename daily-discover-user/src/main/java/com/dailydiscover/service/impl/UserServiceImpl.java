package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.dto.UserResponse;
import com.dailydiscover.entity.User;
import com.dailydiscover.entity.UserLevel;
import com.dailydiscover.mapper.UserLevelMapper;
import com.dailydiscover.mapper.UserMapper;
import com.dailydiscover.service.UserService;
import com.dailydiscover.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserLevelMapper userLevelMapper;

    @Override
    @Transactional
    public UserResponse register(User user) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.register", user, null);
        
        // 检查邮箱是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());
        User existingUser = userMapper.selectOne(queryWrapper);
        
        if (existingUser != null) {
            LogTracer.traceException("UserServiceImpl.register", user, new RuntimeException("邮箱已被注册"));
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 设置用户默认属性
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPoints(0);
        user.setLevelId(1L); // 默认等级
        
        int insertResult = userMapper.insert(user);
        LogTracer.traceDatabaseQuery("INSERT INTO user", user, insertResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        
        LogTracer.traceMethod("UserServiceImpl.register", user, response);
        LogTracer.tracePerformance("UserServiceImpl.register", startTime, System.currentTimeMillis());
        return response;
    }

    @Override
    public UserResponse login(User user) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.login", user, null);
        
        // 根据邮箱查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());
        User existingUser = userMapper.selectOne(queryWrapper);
        
        if (existingUser == null) {
            LogTracer.traceException("UserServiceImpl.login", user, new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!existingUser.getPassword().equals(user.getPassword())) {
            LogTracer.traceException("UserServiceImpl.login", user, new RuntimeException("密码错误"));
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        existingUser.setLastLoginAt(LocalDateTime.now());
        int updateResult = userMapper.updateById(existingUser);
        LogTracer.traceDatabaseQuery("UPDATE user SET last_login_at", existingUser, updateResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(existingUser, response);
        
        LogTracer.traceMethod("UserServiceImpl.login", user, response);
        LogTracer.tracePerformance("UserServiceImpl.login", startTime, System.currentTimeMillis());
        return response;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.getUserById", userId, null);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", userId, user);
        
        if (user == null) {
            LogTracer.traceException("UserServiceImpl.getUserById", userId, new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        UserResponse response = convertToResponse(user);
        LogTracer.traceMethod("UserServiceImpl.getUserById", userId, response);
        LogTracer.tracePerformance("UserServiceImpl.getUserById", startTime, System.currentTimeMillis());
        return response;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userMapper.selectByEmail(email);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(User user) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.updateUserProfile", user, null);
        
        User existingUser = userMapper.selectById(user.getId());
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", user.getId(), existingUser);
        
        if (existingUser == null) {
            LogTracer.traceException("UserServiceImpl.updateUserProfile", user, new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        BeanUtils.copyProperties(user, existingUser, "id", "email", "password", "createdAt");
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        int updateResult = userMapper.updateById(existingUser);
        LogTracer.traceDatabaseQuery("UPDATE user SET ... WHERE id = ?", existingUser, updateResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(existingUser, response);
        
        LogTracer.traceMethod("UserServiceImpl.updateUserProfile", user, response);
        LogTracer.tracePerformance("UserServiceImpl.updateUserProfile", startTime, System.currentTimeMillis());
        return response;
    }

    @Override
    @Transactional
    public UserResponse updateUserPoints(Long userId, Integer points) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        int result = userMapper.updateUserPoints(userId, points);
        if (result > 0) {
            // 更新用户等级
            updateUserLevel(userId, points);
            // 重新获取更新后的用户信息
            User updatedUser = userMapper.selectById(userId);
            return convertToResponse(updatedUser);
        }
        throw new RuntimeException("积分更新失败");
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.deleteUser", userId, null);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", userId, user);
        
        if (user == null) {
            LogTracer.traceException("UserServiceImpl.deleteUser", userId, new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }

        int result = userMapper.deleteById(userId);
        LogTracer.traceDatabaseQuery("DELETE FROM user WHERE id = ?", userId, result);
        
        boolean deleteResult = result > 0;
        LogTracer.traceMethod("UserServiceImpl.deleteUser", userId, deleteResult);
        LogTracer.tracePerformance("UserServiceImpl.deleteUser", startTime, System.currentTimeMillis());
        return deleteResult;
    }

    /**
     * 更新用户等级
     */
    private void updateUserLevel(Long userId, Integer points) {
        UserLevel userLevel = userLevelMapper.selectLevelByPoints(points);
        if (userLevel != null) {
            userMapper.updateUserLevel(userId, userLevel.getId());
        }
    }

    /**
     * 将User实体转换为UserResponse DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        
        // 设置等级名称
        UserLevel userLevel = userLevelMapper.selectById(user.getLevelId());
        if (userLevel != null) {
            response.setLevelName(userLevel.getLevelName());
        }
        
        return response;
    }
}