package com.dailydiscover.service;

import com.dailydiscover.user.dto.UserResponse;
import com.dailydiscover.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     * 
     * @param user 用户实体
     * @return 用户信息
     */
    UserResponse register(User user);

    /**
     * 用户登录
     * 
     * @param phone 手机号
     * @param password 密码
     * @return 用户信息
     */
    UserResponse login(String phone, String password);

    /**
     * 根据ID获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserResponse getUserById(Long userId);

    /**
     * 根据手机号获取用户信息
     * 
     * @param phone 手机号
     * @return 用户信息
     */
    UserResponse getUserByPhone(String phone);

    /**
     * 更新用户个人信息
     * 
     * @param user 用户实体
     * @return 更新后的用户信息
     */
    UserResponse updateUserProfile(User user);

    /**
     * 更新用户积分
     * 
     * @param userId 用户ID
     * @param points 积分
     * @return 更新后的用户信息
     */
    UserResponse updateUserPoints(Long userId, Integer points);

    /**
     * 删除用户
     * 
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);

    /**
     * 获取当前用户信息
     * 
     * @param userId 当前用户ID
     * @return 用户信息
     */
    UserResponse getCurrentUser(Long userId);
}