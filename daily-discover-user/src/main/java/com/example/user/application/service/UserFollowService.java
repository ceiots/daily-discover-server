package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserFollowDTO;
import com.example.user.application.dto.UserDTO;

import java.util.List;

/**
 * 用户关注应用服务接口
 */
public interface UserFollowService extends BaseApplicationService {

    /**
     * 关注用户
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @param remark 备注
     * @return 用户关注DTO
     */
    UserFollowDTO followUser(Long userId, Long followUserId, String remark);

    /**
     * 取消关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否取消成功
     */
    boolean unfollowUser(Long userId, Long followUserId);

    /**
     * 获取用户关注列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户关注DTO列表
     */
    List<UserFollowDTO> getUserFollows(Long userId, Integer limit);

    /**
     * 分页获取用户关注
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户关注分页结果
     */
    PageResult<UserFollowDTO> getUserFollowPage(Long userId, PageRequest pageRequest);

    /**
     * 获取用户粉丝列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户关注DTO列表
     */
    List<UserFollowDTO> getUserFans(Long userId, Integer limit);

    /**
     * 分页获取用户粉丝
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户关注分页结果
     */
    PageResult<UserFollowDTO> getUserFansPage(Long userId, PageRequest pageRequest);

    /**
     * 检查用户是否关注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @return 是否已关注
     */
    boolean isFollowed(Long userId, Long followUserId);

    /**
     * 统计关注数量
     *
     * @param userId 用户ID
     * @return 关注数量
     */
    Long countFollows(Long userId);

    /**
     * 统计粉丝数量
     *
     * @param userId 用户ID
     * @return 粉丝数量
     */
    Long countFans(Long userId);

    /**
     * 更新关注备注
     *
     * @param userId 用户ID
     * @param followUserId 被关注用户ID
     * @param remark 备注
     * @return 是否更新成功
     */
    boolean updateFollowRemark(Long userId, Long followUserId, String remark);
    
    /**
     * 获取关注的用户信息列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户DTO列表
     */
    List<UserDTO> getFollowedUsers(Long userId, Integer limit);
    
    /**
     * 获取粉丝用户信息列表
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户DTO列表
     */
    List<UserDTO> getFansUsers(Long userId, Integer limit);
}