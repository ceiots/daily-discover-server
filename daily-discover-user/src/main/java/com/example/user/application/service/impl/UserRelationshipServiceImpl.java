package com.example.user.application.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserAssembler;
import com.example.user.application.assembler.UserRelationshipAssembler;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserRelationshipDTO;
import com.example.user.application.service.UserRelationshipService;
import com.example.user.domain.model.User;
import com.example.user.domain.model.UserRelationship;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserRelationshipDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户关系应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserRelationshipServiceImpl implements UserRelationshipService {

    private final UserRelationshipDomainService userRelationshipDomainService;
    private final UserRelationshipAssembler userRelationshipAssembler;
    private final UserAssembler userAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userRelationshipDomainService;
    }

    @Override
    @Transactional
    public UserRelationshipDTO addRelationship(UserRelationshipDTO userRelationshipDTO) {
        UserRelationship userRelationship = userRelationshipAssembler.toDomain(userRelationshipDTO);
        UserRelationship savedRelationship = userRelationshipDomainService.addRelationship(userRelationship);
        return userRelationshipAssembler.toDTO(savedRelationship);
    }

    @Override
    @Transactional
    public boolean removeRelationship(Long userId, Long relatedUserId, Integer relationType) {
        return userRelationshipDomainService.removeRelationship(
                new UserId(userId), new UserId(relatedUserId), relationType);
    }

    @Override
    public List<UserRelationshipDTO> getUserRelationships(Long userId, Integer relationType, Integer limit) {
        List<UserRelationship> relationships = userRelationshipDomainService.getUserRelationships(
                new UserId(userId), relationType, limit);
        return relationships.stream()
                .map(userRelationshipAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserRelationshipDTO> getUserRelationshipPage(Long userId, Integer relationType, PageRequest pageRequest) {
        PageResult<UserRelationship> pageResult = userRelationshipDomainService.getUserRelationshipPage(
                new UserId(userId), relationType, pageRequest);
        
        List<UserRelationshipDTO> relationshipDTOs = pageResult.getList().stream()
                .map(userRelationshipAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(relationshipDTOs, pageResult.getTotal(), pageResult.getPages(), 
                pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    @Override
    public boolean hasRelationship(Long userId, Long relatedUserId, Integer relationType) {
        return userRelationshipDomainService.hasRelationship(
                new UserId(userId), new UserId(relatedUserId), relationType);
    }

    @Override
    public Long countRelationships(Long userId, Integer relationType) {
        return userRelationshipDomainService.countRelationships(new UserId(userId), relationType);
    }

    @Override
    @Transactional
    public boolean updateRelationshipRemark(Long userId, Long relatedUserId, Integer relationType, String remark) {
        return userRelationshipDomainService.updateRelationshipRemark(
                new UserId(userId), new UserId(relatedUserId), relationType, remark);
    }

    @Override
    public List<UserDTO> getRelatedUsers(Long userId, Integer relationType, Integer limit) {
        List<User> users = userRelationshipDomainService.getRelatedUsers(new UserId(userId), relationType, limit);
        return users.stream()
                .map(userAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserRelationshipDTO addFriend(Long userId, Long friendUserId, String remark) {
        UserRelationship relationship = userRelationshipDomainService.addFriend(
                new UserId(userId), new UserId(friendUserId), remark);
        return userRelationshipAssembler.toDTO(relationship);
    }

    @Override
    @Transactional
    public UserRelationshipDTO addToBlacklist(Long userId, Long blockedUserId, String remark) {
        UserRelationship relationship = userRelationshipDomainService.addToBlacklist(
                new UserId(userId), new UserId(blockedUserId), remark);
        return userRelationshipAssembler.toDTO(relationship);
    }

    @Override
    @Transactional
    public UserRelationshipDTO addSpecialAttention(Long userId, Long specialUserId, String remark) {
        UserRelationship relationship = userRelationshipDomainService.addSpecialAttention(
                new UserId(userId), new UserId(specialUserId), remark);
        return userRelationshipAssembler.toDTO(relationship);
    }
}