package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserRelationship;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;

import java.util.List;

/**
 * 用户关系领域服务接口
 */
public interface UserRelationshipDomainService extends BaseDomainService {

    /**
     * 添加用户关系
     *
     * @param userRelationship 用户关系
     * @return 保存后的用户关系
     */
    UserRelationship addRelationship(UserRelationship userRelationship);

    /**
     * 移除用户关系
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @return 是否移除成功
     */
    boolean removeRelationship(UserId userId, UserId relatedUserId, Integer relationType);

    /**
     * 获取用户关系列表
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param limit 限制数量
     * @return 用户关系列表
     */
    List<UserRelationship> getUserRelationships(UserId userId, Integer relationType, Integer limit);

    /**
     * 分页获取用户关系
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param pageRequest 分页请求
     * @return 用户关系分页结果
     */
    PageResult<UserRelationship> getUserRelationshipPage(UserId userId, Integer relationType, PageRequest pageRequest);

    /**
     * 检查用户关系是否存在
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @return 是否存在关系
     */
    boolean hasRelationship(UserId userId, UserId relatedUserId, Integer relationType);

    /**
     * 统计用户关系数量
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @return 关系数量
     */
    Long countRelationships(UserId userId, Integer relationType);

    /**
     * 更新用户关系备注
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @param remark 备注
     * @return 是否更新成功
     */
    boolean updateRelationshipRemark(UserId userId, UserId relatedUserId, Integer relationType, String remark);
    
    /**
     * 获取关系用户信息列表
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param limit 限制数量
     * @return 用户列表
     */
    List<User> getRelatedUsers(UserId userId, Integer relationType, Integer limit);
    
    /**
     * 添加好友
     *
     * @param userId 用户ID
     * @param friendUserId 好友用户ID
     * @param remark 备注
     * @return 用户关系
     */
    UserRelationship addFriend(UserId userId, UserId friendUserId, String remark);
    
    /**
     * 添加到黑名单
     *
     * @param userId 用户ID
     * @param blockedUserId 被拉黑用户ID
     * @param remark 备注
     * @return 用户关系
     */
    UserRelationship addToBlacklist(UserId userId, UserId blockedUserId, String remark);
    
    /**
     * 添加特别关注
     *
     * @param userId 用户ID
     * @param specialUserId 特别关注用户ID
     * @param remark 备注
     * @return 用户关系
     */
    UserRelationship addSpecialAttention(UserId userId, UserId specialUserId, String remark);
}