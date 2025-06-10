package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.User;
import com.example.user.domain.model.UserFollow;
import com.example.user.domain.model.id.UserId;

import java.util.List;

/**
 * 用户关注领域服务接口
 */
public interface UserFollowDomainService extends BaseDomainService {

    /**
     * 关注用户
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @param remark 备注
     * @return 用户关注
     */
    UserFollow followUser(UserId userId, UserId followUserId, String remark);

    /**
     * 取消关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否取消成功
     */
    boolean unfollowUser(UserId userId, UserId followUserId);

    /**
     * 获取用户关注列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户关注列表
     */
    List<UserFollow> getUserFollows(UserId userId, Integer limit);

    /**
     * 分页获取用户关注
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户关注分页结果
     */
    PageResult<UserFollow> getUserFollowPage(UserId userId, PageRequest pageRequest);

    /**
     * 获取用户粉丝列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户关注列表
     */
    List<UserFollow> getUserFans(UserId userId, Integer limit);

    /**
     * 分页获取用户粉丝
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户关注分页结果
     */
    PageResult<UserFollow> getUserFansPage(UserId userId, PageRequest pageRequest);

    /**
     * 检查用户是否关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否已关注
     */
    boolean isFollowed(UserId userId, UserId followUserId);

    /**
     * 统计关注数量
     *
     * @param userId 用户ID
     * @return 关注数量
     */
    Long countFollows(UserId userId);

    /**
     * 统计粉丝数量
     *
     * @param userId 用户ID
     * @return 粉丝数量
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
    boolean updateFollowRemark(UserId userId, UserId followUserId, String remark);
    
    /**
     * 获取关注的用户信息列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户列表
     */
    List<User> getFollowedUsers(UserId userId, Integer limit);
    
    /**
     * 获取粉丝用户信息列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户列表
     */
    List<User> getFansUsers(UserId userId, Integer limit);
}