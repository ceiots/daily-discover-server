package com.example.user.domain.service;

import com.example.user.domain.model.UserAuth;
import com.example.user.domain.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户授权领域服务接口
 */
public interface UserAuthDomainService extends BaseDomainService {

    /**
     * 获取用户授权信息列表
     *
     * @param userId 用户ID
     * @return 用户授权列表
     */
    List<UserAuth> getUserAuths(UserId userId);

    /**
     * 获取用户授权信息
     *
     * @param identityType 身份类型
     * @param identifier 标识
     * @return 用户授权可选值
     */
    Optional<UserAuth> getUserAuth(String identityType, String identifier);

    /**
     * 添加用户授权
     *
     * @param userAuth 用户授权
     * @return 保存后的用户授权
     */
    UserAuth addUserAuth(UserAuth userAuth);

    /**
     * 更新用户授权
     *
     * @param userAuth 用户授权
     * @return 更新后的用户授权
     */
    UserAuth updateUserAuth(UserAuth userAuth);

    /**
     * 删除用户授权
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否删除成功
     */
    boolean deleteUserAuth(UserId userId, String identityType);

    /**
     * 验证用户授权
     *
     * @param identityType 身份类型
     * @param identifier 标识
     * @param credential 凭据
     * @return 是否验证成功
     */
    boolean verifyUserAuth(String identityType, String identifier, String credential);

    /**
     * 更新授权凭据
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @param credential 凭据
     * @return 是否更新成功
     */
    boolean updateCredential(UserId userId, String identityType, String credential);

    /**
     * 标记授权已验证
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否更新成功
     */
    boolean markVerified(UserId userId, String identityType);

    /**
     * 禁用授权
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否禁用成功
     */
    boolean disableAuth(UserId userId, String identityType);

    /**
     * 启用授权
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否启用成功
     */
    boolean enableAuth(UserId userId, String identityType);

    /**
     * 检查授权是否存在
     *
     * @param identityType 身份类型
     * @param identifier 标识
     * @return 是否存在
     */
    boolean existsAuth(String identityType, String identifier);
}