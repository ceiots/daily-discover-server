package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.dto.LoginRequest;
import com.dailydiscover.dto.RegisterRequest;
import com.dailydiscover.dto.UserResponse;
import com.dailydiscover.entity.User;
import com.dailydiscover.mapper.UserMapper;
import com.dailydiscover.service.UserService;
import com.dailydiscover.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 * 
 * @author Daily Discover Team
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse register(RegisterRequest request) {
        log.info("用户注册请求: {}", request.getUsername());

        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (isUsernameExists(request.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (isEmailExists(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 检查手机号是否已存在（如果提供了手机号）
        if (request.getPhone() != null && !request.getPhone().isEmpty() && isPhoneExists(request.getPhone())) {
            throw new RuntimeException("手机号已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(0); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 保存用户
        userMapper.insert(user);

        log.info("用户注册成功: {} (ID: {})", user.getUsername(), user.getId());

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setToken(token);
        response.setTokenExpireTime(LocalDateTime.now().plusDays(1));

        return response;
    }

    @Override
    public UserResponse login(LoginRequest request) {
        log.info("用户登录请求: {}", request.getUsername());

        // 查找用户
        User user = getUserByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != 0) {
            throw new RuntimeException("账户已被禁用，请联系管理员");
        }

        // 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 更新最后登录信息
        updateLastLoginInfo(user.getId(), "127.0.0.1"); // 实际项目中应获取真实IP

        log.info("用户登录成功: {} (ID: {})", user.getUsername(), user.getId());

        // 构建响应
        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        response.setToken(token);
        response.setTokenExpireTime(LocalDateTime.now().plusDays(1));

        return response;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserResponse updateUser(Long userId, User updateUser) {
        User existingUser = userMapper.selectById(userId);
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新允许修改的字段
        if (updateUser.getNickname() != null) {
            existingUser.setNickname(updateUser.getNickname());
        }
        if (updateUser.getAvatar() != null) {
            existingUser.setAvatar(updateUser.getAvatar());
        }
        if (updateUser.getGender() != null) {
            existingUser.setGender(updateUser.getGender());
        }
        if (updateUser.getBirthday() != null) {
            existingUser.setBirthday(updateUser.getBirthday());
        }
        if (updateUser.getBio() != null) {
            existingUser.setBio(updateUser.getBio());
        }

        existingUser.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(existingUser);

        log.info("用户信息更新成功: {} (ID: {})", existingUser.getUsername(), userId);

        UserResponse response = new UserResponse();
        BeanUtils.copyProperties(existingUser, response);
        return response;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        log.info("用户密码修改成功: {} (ID: {})", user.getUsername(), userId);
        return true;
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    @Override
    public boolean isEmailExists(String email) {
        return userMapper.countByEmail(email) > 0;
    }

    @Override
    public boolean isPhoneExists(String phone) {
        return userMapper.countByPhone(phone) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginInfo(Long userId, String ip) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        user.setLastLoginIp(ip);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}