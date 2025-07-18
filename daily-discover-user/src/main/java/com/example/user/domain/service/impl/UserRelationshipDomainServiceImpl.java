package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserRelationship;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.repository.UserRelationshipRepository;
import com.example.user.domain.repository.UserRepository;
import com.example.user.domain.service.UserRelationshipDomainService;
import com.example.user.infrastructure.common.result.ResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户关系领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRelationshipDomainServiceImpl extends BaseDomainServiceImpl implements UserRelationshipDomainService {

    private final UserRelationshipRepository userRelationshipRepository;
    private final UserRepository userRepository;
    
    // 有效状态
    private static final Integer STATUS_ACTIVE = 1;

    @Override
    @Transactional
    public UserRelationship addRelationship(UserRelationship userRelationship) {
        // 检查关系是否已存在
        if (hasRelationship(userRelationship.getUserId(), userRelationship.getRelatedUserId(), userRelationship.getRelationType())) {
            throw new BusinessException("关系已存在");
        }
        
        return userRelationshipRepository.save(userRelationship);
    }

    @Override
    @Transactional
    public boolean removeRelationship(UserId userId, UserId relatedUserId, Integer relationType) {
        // 查找关系并更新状态为无效
        Optional<UserRelationship> relationshipOpt = userRelationshipRepository.findByUserIdAndRelatedUserIdAndType(
                userId, relatedUserId, relationType);
        
        if (relationshipOpt.isEmpty()) {
            return false;
        }
        
        UserRelationship relationship = relationshipOpt.get();
        return userRelationshipRepository.updateStatus(userId, relatedUserId, relationType, 0);
    }

    @Override
    public List<UserRelationship> getUserRelationships(UserId userId, Integer relationType, Integer limit) {
        return userRelationshipRepository.findByUserIdAndType(userId, relationType, STATUS_ACTIVE);
    }

    @Override
    public PageResult<UserRelationship> getUserRelationshipPage(UserId userId, Integer relationType, PageRequest pageRequest) {
        return userRelationshipRepository.findPage(userId, relationType, STATUS_ACTIVE, pageRequest);
    }

    @Override
    public boolean hasRelationship(UserId userId, UserId relatedUserId, Integer relationType) {
        return userRelationshipRepository.exists(userId, relatedUserId, relationType, STATUS_ACTIVE);
    }

    @Override
    public Long countRelationships(UserId userId, Integer relationType) {
        int count = userRelationshipRepository.countByUserIdAndType(userId, relationType, STATUS_ACTIVE);
        return (long) count;
    }

    @Override
    @Transactional
    public boolean updateRelationshipRemark(UserId userId, UserId relatedUserId, Integer relationType, String remark) {
        // 查找关系并更新备注
        Optional<UserRelationship> relationshipOpt = userRelationshipRepository.findByUserIdAndRelatedUserIdAndType(
                userId, relatedUserId, relationType);
        
        if (relationshipOpt.isEmpty()) {
            return false;
        }
        
        UserRelationship relationship = relationshipOpt.get();
        relationship.setRemark(remark);
        userRelationshipRepository.update(relationship);
        return true;
    }

    @Override
    public List<User> getRelatedUsers(UserId userId, Integer relationType, Integer limit) {
        List<UserRelationship> relationships = userRelationshipRepository.findByUserIdAndType(userId, relationType, STATUS_ACTIVE);
        List<UserId> relatedUserIds = relationships.stream()
                .map(UserRelationship::getRelatedUserId)
                .collect(Collectors.toList());
        
        return userRepository.findByIds(relatedUserIds);
    }

    @Override
    @Transactional
    public UserRelationship addFriend(UserId userId, UserId friendUserId, String remark) {
        UserRelationship relationship = UserRelationship.create(userId, friendUserId, 1);
        relationship.setRemark(remark);
        return addRelationship(relationship);
    }

    @Override
    @Transactional
    public UserRelationship addToBlacklist(UserId userId, UserId blockedUserId, String remark) {
        UserRelationship relationship = UserRelationship.create(userId, blockedUserId, 2);
        relationship.setRemark(remark);
        return addRelationship(relationship);
    }

    @Override
    @Transactional
    public UserRelationship addSpecialAttention(UserId userId, UserId specialUserId, String remark) {
        UserRelationship relationship = UserRelationship.create(userId, specialUserId, 3);
        relationship.setRemark(remark);
        return addRelationship(relationship);
    }
}