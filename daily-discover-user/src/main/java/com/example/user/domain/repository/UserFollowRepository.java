package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserFollow;
import com.example.user.domain.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户关注仓储接口
 */
public interface UserFollowRepository {
    
    /**
     * 保存用户关注
     *
     * @param userFollow 用户关注对象
     * @return 保存后的用户关注对象
     */
    UserFollow save(UserFollow userFollow);
    
    /**
     * 删除用户关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否删除成功
     */
    boolean removeFollow(UserId userId, UserId followUserId);
    
    /**
     * 查询用户关注列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户关注列表
     */
    List<UserFollow> findFollows(UserId userId, Integer limit);
    
    /**
     * 分页查询用户关注
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户关注分页结果
     */
    PageResult<UserFollow> findFollowsPage(UserId userId, PageRequest pageRequest);
    
    /**
     * 查询用户粉丝列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户粉丝列表
     */
    List<UserFollow> findFans(UserId userId, Integer limit);
    
    /**
     * 分页查询用户粉丝
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户粉丝分页结果
     */
    PageResult<UserFollow> findFansPage(UserId userId, PageRequest pageRequest);
    
    /**
     * 检查是否已关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否已关注
     */
    boolean existsFollow(UserId userId, UserId followUserId);
    
    /**
     * 统计用户关注数
     *
     * @param userId 用户ID
     * @return 关注数
     */
    Long countFollows(UserId userId);
    
    /**
     * 统计用户粉丝数
     *
     * @param userId 用户ID
     * @return 粉丝数
     */
    Long countFans(UserId userId);
    
    /**
     * 更新关注备注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @param remark 备注
     * @return 是否更新成功
     */
    boolean updateRemark(UserId userId, UserId followUserId, String remark);
} 