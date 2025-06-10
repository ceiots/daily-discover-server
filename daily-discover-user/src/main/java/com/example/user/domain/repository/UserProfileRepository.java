package com.example.user.domain.repository;

import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserProfile;

import java.util.Optional;

/**
 * 用户资料仓储接口
 */
public interface UserProfileRepository {

    /**
     * 根据用户ID查询用户资料
     *
     * @param userId 用户ID
     * @return 用户资料
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * 根据ID查询用户资料
     *
     * @param id 资料ID
     * @return 用户资料
     */
    Optional<UserProfile> findById(Long id);

    /**
     * 保存用户资料
     *
     * @param userProfile 用户资料
     * @return 保存后的用户资料
     */
    UserProfile save(UserProfile userProfile);

    /**
     * 更新用户资料
     *
     * @param userProfile 用户资料
     * @return 更新后的用户资料
     */
    UserProfile update(UserProfile userProfile);

    /**
     * 删除用户资料
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean delete(Long userId);
} 