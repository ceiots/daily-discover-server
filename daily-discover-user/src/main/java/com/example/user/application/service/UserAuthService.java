package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserAuthDTO;

import java.util.List;

/**
 * 用户授权应用服务接口
 */
public interface UserAuthService extends BaseApplicationService {

    /**
     * 获取用户授权信息
     *
     * @param userId 用户ID
     * @return 用户授权DTO列表
     */
    List<UserAuthDTO> getUserAuths(Long userId);

    /**
     * 获取用户授权信息
     *
     * @param identityType 身份类型
     * @param identifier 标识
     * @return 用户授权DTO
     */
    UserAuthDTO getUserAuth(String identityType, String identifier);

    /**
     * 添加用户授权
     *
     * @param userAuthDTO 用户授权DTO
     * @return 用户授权DTO
     */
    UserAuthDTO addUserAuth(UserAuthDTO userAuthDTO);

    /**
     * 更新用户授权
     *
     * @param userAuthDTO 用户授权DTO
     * @return 用户授权DTO
     */
    UserAuthDTO updateUserAuth(UserAuthDTO userAuthDTO);

    /**
     * 删除用户授权
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否删除成功
     */
    boolean deleteUserAuth(Long userId, String identityType);

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
    boolean updateCredential(Long userId, String identityType, String credential);

    /**
     * 标记授权已验证
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否更新成功
     */
    boolean markVerified(Long userId, String identityType);

    /**
     * 禁用授权
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否禁用成功
     */
    boolean disableAuth(Long userId, String identityType);

    /**
     * 启用授权
     *
     * @param userId 用户ID
     * @param identityType 身份类型
     * @return 是否启用成功
     */
    boolean enableAuth(Long userId, String identityType);

    /**
     * 检查授权是否存在
     *
     * @param identityType 身份类型
     * @param identifier 标识
     * @return 是否存在
     */
    boolean existsAuth(String identityType, String identifier);
}