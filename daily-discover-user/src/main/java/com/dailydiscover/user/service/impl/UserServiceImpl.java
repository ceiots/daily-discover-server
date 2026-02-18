package com.dailydiscover.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.user.dto.UserResponse;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.mapper.UserMapper;
import com.dailydiscover.user.service.UserService;
import com.dailydiscover.common.util.LogTracer;
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

    @Override
    @Transactional
    public UserResponse register(User user) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(user, null);
        
        // 检查手机号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", user.getPhone());
        User existingUser = userMapper.selectOne(queryWrapper);
        
        if (existingUser != null) {
            LogTracer.traceBusinessException(new RuntimeException("手机号已被注册"));
            throw new RuntimeException("手机号已被注册");
        }
        
        // 设置用户默认属性
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setStatus(User.Status.ACTIVE.getValue());
        user.setMembership("普通会员");
        
        int insertResult = userMapper.insert(user);
        LogTracer.traceDatabaseQuery("INSERT INTO user", new Object[]{user}, insertResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        
        LogTracer.traceBusinessMethod(user, response);
        LogTracer.traceBusinessPerformance(startTime);
        return response;
    }

    @Override
    public UserResponse login(String phone, String password) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(phone, null);
        
        // 根据手机号查询用户
        User user = userMapper.selectByPhone(phone);
        
        if (user == null) {
            LogTracer.traceBusinessException(new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码（这里需要密码加密验证，暂时简化处理）
        if (!user.getPassword().equals(password)) {
            LogTracer.traceBusinessException(new RuntimeException("密码错误"));
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间（新表结构中没有last_login_at字段，暂时跳过）
        // user.setLastLoginAt(LocalDateTime.now());
        // int updateResult = userMapper.updateById(user);
        // LogTracer.traceDatabaseQuery("UPDATE user SET last_login_at", new Object[]{user}, updateResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        
        LogTracer.traceBusinessMethod(phone, response);
        LogTracer.traceBusinessPerformance(startTime);
        return response;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{userId}, user);
        
        if (user == null) {
            LogTracer.traceBusinessException(new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        UserResponse response = convertToResponse(user);
        LogTracer.traceBusinessMethod(userId, response);
        LogTracer.traceBusinessPerformance(startTime);
        return response;
    }

    @Override
    public UserResponse getUserByPhone(String phone) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(phone, null);
        
        // 根据手机号查询用户
        User user = userMapper.selectByPhone(phone);
        
        if (user == null) {
            // 用户不存在时返回null，而不是抛出异常
            return null;
        }
        
        UserResponse response = convertToResponse(user);
        
        LogTracer.traceBusinessMethod(phone, response);
        LogTracer.traceBusinessPerformance(startTime);
        return response;
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(User user) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(user, null);
        
        // 检查手机号是否已被其他用户使用
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", user.getPhone());
        queryWrapper.ne("id", user.getId());
        User otherUser = userMapper.selectOne(queryWrapper);
        
        if (otherUser != null) {
            LogTracer.traceBusinessException(new RuntimeException("手机号已被其他用户使用"));
            throw new RuntimeException("手机号已被其他用户使用");
        }
        
        User existingUser = userMapper.selectById(user.getId());
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{user.getId()}, existingUser);
        
        if (existingUser == null) {
            LogTracer.traceBusinessException(new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        BeanUtils.copyProperties(user, existingUser, "id", "phone", "password", "createdAt");
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        int updateResult = userMapper.updateById(existingUser);
        LogTracer.traceDatabaseQuery("UPDATE user SET ... WHERE id = ?", new Object[]{existingUser}, updateResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(existingUser, response);
        
        LogTracer.traceBusinessMethod(user, response);
        LogTracer.traceBusinessPerformance(startTime);
        return response;
    }

    // 积分功能已移到独立的积分模块，此处暂时移除

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{userId}, user);
        
        if (user == null) {
            LogTracer.traceBusinessException(new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }

        int result = userMapper.deleteById(userId);
        LogTracer.traceDatabaseQuery("DELETE FROM user WHERE id = ?", new Object[]{userId}, result);
        
        boolean deleteResult = result > 0;
        LogTracer.traceBusinessMethod(userId, deleteResult);
        LogTracer.traceBusinessPerformance(startTime);
        return deleteResult;
    }

    /**
     * 将User实体转换为UserResponse DTO
     */
    private UserResponse convertToResponse(User user) {
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    @Override
    public UserResponse getCurrentUser(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{userId}, user);
        
        if (user == null) {
            LogTracer.traceBusinessException(new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        UserResponse response = convertToResponse(user);
        LogTracer.traceBusinessMethod(userId, response);
        LogTracer.traceBusinessPerformance(startTime);
        return response;
    }
}