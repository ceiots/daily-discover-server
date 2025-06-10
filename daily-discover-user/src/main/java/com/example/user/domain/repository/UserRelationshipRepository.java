package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserRelationship;
import com.example.user.domain.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户关系仓储接口
 */
public interface UserRelationshipRepository {

    /**
     * 根据ID查询用户关系
     *
     * @param id 关系ID
     * @return 用户关系
     */
    Optional<UserRelationship> findById(Long id);

    /**
     * 根据用户ID、关联用户ID和关系类型查询用户关系
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @return 用户关系
     */
    Optional<UserRelationship> findByUserIdAndRelatedUserIdAndType(UserId userId, UserId relatedUserId, Integer relationType);

    /**
     * 根据用户ID和关系类型查询用户关系列表
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param status 状态
     * @return 用户关系列表
     */
    List<UserRelationship> findByUserIdAndType(UserId userId, Integer relationType, Integer status);

    /**
     * 根据关联用户ID和关系类型查询用户关系列表
     *
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @param status 状态
     * @return 用户关系列表
     */
    List<UserRelationship> findByRelatedUserIdAndType(UserId relatedUserId, Integer relationType, Integer status);

    /**
     * 分页查询用户关系
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param status 状态
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<UserRelationship> findPage(UserId userId, Integer relationType, Integer status, PageRequest pageRequest);

    /**
     * 保存用户关系
     *
     * @param userRelationship 用户关系
     * @return 保存后的用户关系
     */
    UserRelationship save(UserRelationship userRelationship);

    /**
     * 更新用户关系
     *
     * @param userRelationship 用户关系
     * @return 更新后的用户关系
     */
    UserRelationship update(UserRelationship userRelationship);

    /**
     * 删除用户关系
     *
     * @param id 关系ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 更新关系状态
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(UserId userId, UserId relatedUserId, Integer relationType, Integer status);

    /**
     * 统计用户关系数量
     *
     * @param userId 用户ID
     * @param relationType 关系类型
     * @param status 状态
     * @return 关系数量
     */
    int countByUserIdAndType(UserId userId, Integer relationType, Integer status);

    /**
     * 检查用户关系是否存在
     *
     * @param userId 用户ID
     * @param relatedUserId 关联用户ID
     * @param relationType 关系类型
     * @param status 状态
     * @return 是否存在
     */
    boolean exists(UserId userId, UserId relatedUserId, Integer relationType, Integer status);

    /**
     * 获取互相为好友的用户列表
     *
     * @param userId 用户ID
     * @return 互相为好友的用户ID列表
     */
    List<UserId> findMutualFriendUserIds(UserId userId);

    /**
     * 批量获取用户关系
     *
     * @param userId 用户ID
     * @param relatedUserIds 关联用户ID列表
     * @param relationType 关系类型
     * @return 用户关系列表
     */
    List<UserRelationship> findByUserIdAndRelatedUserIdsAndType(UserId userId, List<UserId> relatedUserIds, Integer relationType);
}