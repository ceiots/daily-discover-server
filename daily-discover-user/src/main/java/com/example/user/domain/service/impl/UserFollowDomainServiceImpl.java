package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.User;
import com.example.user.domain.model.UserFollow;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.repository.UserFollowRepository;
import com.example.user.domain.repository.UserRepository;
import com.example.user.domain.service.UserFollowDomainService;
import com.example.user.infrastructure.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户关注领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserFollowDomainServiceImpl extends BaseDomainServiceImpl implements UserFollowDomainService {

    private final UserFollowRepository userFollowRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserFollow followUser(UserId userId, UserId followUserId, String remark) {
        // 不能关注自己
        if (userId.equals(followUserId)) {
            throw new BusinessException("不能关注自己");
        }
        
        // 检查是否已关注
        if (isFollowed(userId, followUserId)) {
            throw new BusinessException("已经关注了该用户");
        }
        
        // 创建关注关系
        UserFollow userFollow = UserFollow.create(userId, followUserId, remark);
        return userFollowRepository.save(userFollow);
    }

    @Override
    @Transactional
    public boolean unfollowUser(UserId userId, UserId followUserId) {
        return userFollowRepository.removeFollow(userId, followUserId);
    }

    @Override
    public List<UserFollow> getUserFollows(UserId userId, Integer limit) {
        return userFollowRepository.findFollows(userId, limit);
    }

    @Override
    public PageResult<UserFollow> getUserFollowPage(UserId userId, PageRequest pageRequest) {
        return userFollowRepository.findFollowsPage(userId, pageRequest);
    }

    @Override
    public List<UserFollow> getUserFans(UserId userId, Integer limit) {
        return userFollowRepository.findFans(userId, limit);
    }

    @Override
    public PageResult<UserFollow> getUserFansPage(UserId userId, PageRequest pageRequest) {
        return userFollowRepository.findFansPage(userId, pageRequest);
    }

    @Override
    public boolean isFollowed(UserId userId, UserId followUserId) {
        return userFollowRepository.existsFollow(userId, followUserId);
    }

    @Override
    public Long countFollows(UserId userId) {
        return userFollowRepository.countFollows(userId);
    }

    @Override
    public Long countFans(UserId userId) {
        return userFollowRepository.countFans(userId);
    }

    @Override
    @Transactional
    public boolean updateFollowRemark(UserId userId, UserId followUserId, String remark) {
        return userFollowRepository.updateRemark(userId, followUserId, remark);
    }

    @Override
    public List<User> getFollowedUsers(UserId userId, Integer limit) {
        List<UserFollow> follows = getUserFollows(userId, limit);
        List<UserId> followUserIds = follows.stream()
                .map(UserFollow::getFollowUserId)
                .collect(Collectors.toList());
        
        return userRepository.findByIds(followUserIds);
    }

    @Override
    public List<User> getFansUsers(UserId userId, Integer limit) {
        List<UserFollow> fans = getUserFans(userId, limit);
        List<UserId> fanUserIds = fans.stream()
                .map(UserFollow::getUserId)
                .collect(Collectors.toList());
        
        return userRepository.findByIds(fanUserIds);
    }
}