package com.example.user.application.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserAssembler;
import com.example.user.application.assembler.UserFollowAssembler;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserFollowDTO;
import com.example.user.application.service.UserFollowService;
import com.example.user.domain.model.UserFollow;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserFollowDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户关注应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserFollowServiceImpl implements UserFollowService {

    private final UserFollowDomainService userFollowDomainService;
    private final UserFollowAssembler userFollowAssembler;
    private final UserAssembler userAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userFollowDomainService;
    }

    @Override
    @Transactional
    public UserFollowDTO followUser(Long userId, Long followUserId, String remark) {
        UserFollow userFollow = userFollowDomainService.followUser(new UserId(userId), new UserId(followUserId), remark);
        return userFollowAssembler.toDTO(userFollow);
    }

    @Override
    @Transactional
    public boolean unfollowUser(Long userId, Long followUserId) {
        return userFollowDomainService.unfollowUser(new UserId(userId), new UserId(followUserId));
    }

    @Override
    public List<UserFollowDTO> getUserFollows(Long userId, Integer limit) {
        List<UserFollow> follows = userFollowDomainService.getUserFollows(new UserId(userId), limit);
        return follows.stream()
                .map(userFollowAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserFollowDTO> getUserFollowPage(Long userId, PageRequest pageRequest) {
        PageResult<UserFollow> pageResult = userFollowDomainService.getUserFollowPage(new UserId(userId), pageRequest);
        
        List<UserFollowDTO> followDTOs = pageResult.getList().stream()
                .map(userFollowAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), 
                pageResult.getTotal(), followDTOs);
    }

    @Override
    public List<UserFollowDTO> getUserFans(Long userId, Integer limit) {
        List<UserFollow> fans = userFollowDomainService.getUserFans(new UserId(userId), limit);
        return fans.stream()
                .map(userFollowAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserFollowDTO> getUserFansPage(Long userId, PageRequest pageRequest) {
        PageResult<UserFollow> pageResult = userFollowDomainService.getUserFansPage(new UserId(userId), pageRequest);
        
        List<UserFollowDTO> fanDTOs = pageResult.getList().stream()
                .map(userFollowAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), 
                pageResult.getTotal(), fanDTOs);
    }

    @Override
    public boolean isFollowed(Long userId, Long followUserId) {
        return userFollowDomainService.isFollowed(new UserId(userId), new UserId(followUserId));
    }

    @Override
    public Long countFollows(Long userId) {
        return userFollowDomainService.countFollows(new UserId(userId));
    }

    @Override
    public Long countFans(Long userId) {
        return userFollowDomainService.countFans(new UserId(userId));
    }

    @Override
    @Transactional
    public boolean updateFollowRemark(Long userId, Long followUserId, String remark) {
        return userFollowDomainService.updateFollowRemark(new UserId(userId), new UserId(followUserId), remark);
    }

    @Override
    public List<UserDTO> getFollowedUsers(Long userId, Integer limit) {
        List<User> users = userFollowDomainService.getFollowedUsers(new UserId(userId), limit);
        return users.stream()
                .map(userAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getFansUsers(Long userId, Integer limit) {
        List<User> users = userFollowDomainService.getFansUsers(new UserId(userId), limit);
        return users.stream()
                .map(userAssembler::toDTO)
                .collect(Collectors.toList());
    }
}