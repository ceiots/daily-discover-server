package com.example.user.domain.repository;

import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.UserAuth;

import java.util.List;
import java.util.Optional;

/**
 * 用户授权仓储接口
 */
public interface UserAuthRepository {

    /**
     * 通过用户ID获取用户授权列表
     *
     * @param userId 用户ID
     * @return 用户授权列表
     */
    List<UserAuth> findByUserId(UserId userId);

    /**
     * 通过身份类型和标识获取用户授权
     *
     * @param identityType 身份类型
     * @param identifier  标识
     * @return 用户授权
     */
    Optional<UserAuth> findByIdentity(String identityType, String identifier);

    /**
     * 保存用户授权
     *
     * @param userAuth 用户授权
     * @return 保存后的用户授权
     */
    UserAuth save(UserAuth userAuth);

    /**
     * 更新用户授权
     *
     * @param userAuth 用户授权
     * @return 更新后的用户授权
     */
    UserAuth update(UserAuth userAuth);

    /**
     * 删除用户授权
     *
     * @param id 用户授权ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 通过用户ID删除用户授权
     *
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteByUserId(UserId userId);
} 