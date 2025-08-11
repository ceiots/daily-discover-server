package com.dailydiscover.service;

import com.dailydiscover.dto.LoginRequest;
import com.dailydiscover.dto.RegisterRequest;
import com.dailydiscover.dto.UserResponse;
import com.dailydiscover.entity.User;

/**
 * 用户服务接口
 * 
 * @author Daily Discover Team
 * @since 2024-01-01
 */
public interface UserService {

    /**
     * 用户注册
     */
    UserResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    UserResponse login(LoginRequest request);

    /**
     * 根据用户名获取用户信息
     */
    User getUserByUsername(String username);

    /**
     * 根据用户ID获取用户信息
     */
    UserResponse getUserById(Long userId);

    /**
     * 更新用户信息
     */
    UserResponse updateUser(Long userId, User user);

    /**
     * 修改密码
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 检查用户名是否存在
     */
    boolean isUsernameExists(String username);

    /**
     * 检查邮箱是否存在
     */
    boolean isEmailExists(String email);

    /**
     * 检查手机号是否存在
     */
    boolean isPhoneExists(String phone);

    /**
     * 更新最后登录信息
     */
    void updateLastLoginInfo(Long userId, String ip);
}