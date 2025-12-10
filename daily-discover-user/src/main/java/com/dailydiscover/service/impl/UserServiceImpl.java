package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.dto.UserResponse;
import com.dailydiscover.entity.User;
import com.dailydiscover.entity.UserLevel;
import com.dailydiscover.mapper.UserLevelMapper;
import com.dailydiscover.mapper.UserMapper;
import com.dailydiscover.service.UserService;
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
        log.info("用户注册: {}", user.getEmail());
        
        // 检查邮箱是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());
        if (userMapper.selectOne(queryWrapper) != null) {
            throw new RuntimeException("邮箱已被注册");
        }
        
        // 设置用户默认属性
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPoints(0);
        user.setLevelId(1L); // 默认等级
        
        userMapper.insert(user);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        
        return response;
    }

    @Override
    public UserResponse login(User user) {
        log.info("用户登录: {}", user.getEmail());
        
        // 根据邮箱查找用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());
        User existingUser = userMapper.selectOne(queryWrapper);
        
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证密码
        if (!existingUser.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        
        // 更新最后登录时间
        existingUser.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(existingUser);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(existingUser, response);
        
        return response;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToResponse(user);
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
        log.info("更新用户信息: {}", user.getId());
        
        User existingUser = userMapper.selectById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 更新用户信息
        BeanUtils.copyProperties(user, existingUser, "id", "email", "password", "createdAt");
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        userMapper.updateById(existingUser);
        
        // 创建用户响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(existingUser, response);
        
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
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        int result = userMapper.deleteById(userId);
        return result > 0;
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