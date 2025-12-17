package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.user.dto.UserResponse;
import com.dailydiscover.user.entity.User;
import com.dailydiscover.user.entity.UserLevel;
import com.dailydiscover.user.mapper.UserLevelMapper;
import com.dailydiscover.user.mapper.UserMapper;
import com.dailydiscover.user.service.UserService;
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
        
        // 检查手机号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", user.getPhone());
        User existingUser = userMapper.selectOne(queryWrapper);
        
        if (existingUser != null) {
            LogTracer.traceException("UserServiceImpl.register", user, new RuntimeException("手机号已被注册"));
            throw new RuntimeException("手机号已被注册");
        }
        
        // 设置用户默认属性
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPoints(0);
        user.setLevelId(1L); // 默认等级
        
        int insertResult = userMapper.insert(user);
        LogTracer.traceDatabaseQuery("INSERT INTO user", new Object[]{user}, insertResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        
        LogTracer.traceMethod("UserServiceImpl.register", user, response);
        LogTracer.tracePerformance("UserServiceImpl.register", startTime, System.currentTimeMillis());
        return response;
    }

    @Override
    public UserResponse login(String phone, String password) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.login", phone, null);
        
        // 根据手机号查询用户
        User user = userMapper.selectByPhone(phone);
        
        if (user == null) {
            LogTracer.traceException("UserServiceImpl.login", phone, new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!user.getPassword().equals(password)) {
            LogTracer.traceException("UserServiceImpl.login", phone, new RuntimeException("密码错误"));
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        int updateResult = userMapper.updateById(user);
        LogTracer.traceDatabaseQuery("UPDATE user SET last_login_at", new Object[]{user}, updateResult);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        
        LogTracer.traceMethod("UserServiceImpl.login", phone, response);
        LogTracer.tracePerformance("UserServiceImpl.login", startTime, System.currentTimeMillis());
        return response;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.getUserById", userId, null);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{userId}, user);
        
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
    public UserResponse getUserByPhone(String phone) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.getUserByPhone", phone, null);
        
        // 根据手机号查询用户
        User user = userMapper.selectByPhone(phone);
        
        if (user == null) {
            // 用户不存在时返回null，而不是抛出异常
            return null;
        }
        
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setNickname(user.getNickname());
        response.setPhone(user.getPhone());
        
        LogTracer.traceMethod("UserServiceImpl.getUserByPhone", phone, response);
        LogTracer.tracePerformance("UserServiceImpl.getUserByPhone", startTime, System.currentTimeMillis());
        return response;
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(User user) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.updateUserProfile", user, null);
        
        // 检查手机号是否已被其他用户使用
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", user.getPhone());
        queryWrapper.ne("id", user.getId());
        User otherUser = userMapper.selectOne(queryWrapper);
        
        if (otherUser != null) {
            LogTracer.traceException("UserServiceImpl.updateUserProfile", user, new RuntimeException("手机号已被其他用户使用"));
            throw new RuntimeException("手机号已被其他用户使用");
        }
        
        User existingUser = userMapper.selectById(user.getId());
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{user.getId()}, existingUser);
        
        if (existingUser == null) {
            LogTracer.traceException("UserServiceImpl.updateUserProfile", user, new RuntimeException("用户不存在"));
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
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{userId}, user);
        
        if (user == null) {
            LogTracer.traceException("UserServiceImpl.deleteUser", userId, new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }

        int result = userMapper.deleteById(userId);
        LogTracer.traceDatabaseQuery("DELETE FROM user WHERE id = ?", new Object[]{userId}, result);
        
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

    @Override
    public UserResponse getCurrentUser(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("UserServiceImpl.getCurrentUser", userId, null);
        
        User user = userMapper.selectById(userId);
        LogTracer.traceDatabaseQuery("SELECT * FROM user WHERE id = ?", new Object[]{userId}, user);
        
        if (user == null) {
            LogTracer.traceException("UserServiceImpl.getCurrentUser", userId, new RuntimeException("用户不存在"));
            throw new RuntimeException("用户不存在");
        }
        
        UserResponse response = convertToResponse(user);
        LogTracer.traceMethod("UserServiceImpl.getCurrentUser", userId, response);
        LogTracer.tracePerformance("UserServiceImpl.getCurrentUser", startTime, System.currentTimeMillis());
        return response;
    }
}