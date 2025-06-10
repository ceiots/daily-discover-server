package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserRelationshipDTO;
import com.example.user.application.dto.UserDTO;

import java.util.List;

/**
 * 用户关系应用服务接口
 */
public interface UserRelationshipService extends BaseApplicationService {

    /**
     * 添加用户关系
     *
     * @param userRelationshipDTO 用户关系DTO
     * @return 用户关系DTO
     */
    UserRelationshipDTO addRelationship(UserRelationshipDTO userRelationshipDTO);

    /**
     * 移除用户关系
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @return 是否移除成功
     */
    boolean removeRelationship(Long userId, Long relatedUserId, Integer relationType);

    /**
     * 获取用户关系列表
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param limit 限制数量
     * @return 用户关系DTO列表
     */
    List<UserRelationshipDTO> getUserRelationships(Long userId, Integer relationType, Integer limit);

    /**
     * 分页获取用户关系
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param pageRequest 分页请求
     * @return 用户关系分页结果
     */
    PageResult<UserRelationshipDTO> getUserRelationshipPage(Long userId, Integer relationType, PageRequest pageRequest);

    /**
     * 检查用户关系是否存在
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @return 是否存在关系
     */
    boolean hasRelationship(Long userId, Long relatedUserId, Integer relationType);

    /**
     * 统计用户关系数量
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @return 关系数量
     */
    Long countRelationships(Long userId, Integer relationType);

    /**
     * 更新用户关系备注
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @param remark 备注
     * @return 是否更新成功
     */
    boolean updateRelationshipRemark(Long userId, Long relatedUserId, Integer relationType, String remark);
    
    /**
     * 获取关系用户信息列表
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param limit 限制数量
     * @return 用户DTO列表
     */
    List<UserDTO> getRelatedUsers(Long userId, Integer relationType, Integer limit);
    
    /**
     * 添加好友
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @param remark 备注
     * @return 用户关系DTO
     */
    UserRelationshipDTO addFriend(Long userId, Long friendUserId, String remark);
    
    /**
     * 添加到黑名单
     *
     * @param userId 用户ID
     * @param blockedUserId 被拉黑用户ID
     * @param remark 备注
     * @return 用户关系DTO
     */
    UserRelationshipDTO addToBlacklist(Long userId, Long blockedUserId, String remark);
    
    /**
     * 添加特别关注
     *
     * @param userId 用户ID
     * @param specialUserId 特别关注用户ID
     * @param remark 备注
     * @return 用户关系DTO
     */
    UserRelationshipDTO addSpecialAttention(Long userId, Long specialUserId, String remark);
}