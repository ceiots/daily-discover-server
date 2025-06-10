package com.example.user.domain.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.behavior.UserBehavior;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.repository.UserBehaviorRepository;
import com.example.user.domain.service.UserBehaviorDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户行为领域服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserBehaviorDomainServiceImpl extends BaseDomainServiceImpl implements UserBehaviorDomainService {

    private final UserBehaviorRepository userBehaviorRepository;

    @Override
    @Transactional
    public UserBehavior recordBehavior(UserId userId, Integer behaviorType, Long targetId, Integer targetType,
                                      Integer deviceType, String deviceId, String ip) {
        return recordBehaviorWithData(userId, behaviorType, targetId, targetType, deviceType, deviceId, ip, null);
    }

    @Override
    @Transactional
    public UserBehavior recordBehaviorWithData(UserId userId, Integer behaviorType, Long targetId, Integer targetType,
                                              Integer deviceType, String deviceId, String ip, String behaviorData) {
        UserBehavior behavior = UserBehavior.create(userId, behaviorType, targetId, targetType, 
                                                   deviceType, deviceId, ip, behaviorData);
        return userBehaviorRepository.save(behavior);
    }

    @Override
    @Transactional
    public boolean recordBehaviors(List<UserBehavior> behaviors) {
        return userBehaviorRepository.saveBatch(behaviors);
    }

    @Override
    public Optional<UserBehavior> getBehavior(Long id) {
        return userBehaviorRepository.findById(id);
    }

    @Override
    public List<UserBehavior> getUserBehaviors(UserId userId, Integer behaviorType, Integer limit) {
        if (behaviorType != null) {
            return userBehaviorRepository.findByUserIdAndType(userId, behaviorType, limit);
        }
        return userBehaviorRepository.findByUserId(userId, limit);
    }

    @Override
    public List<UserBehavior> getTargetBehaviors(Long targetId, Integer targetType, Integer behaviorType, Integer limit) {
        return userBehaviorRepository.findByTarget(targetId, targetType, limit);
    }

    @Override
    public PageResult<UserBehavior> getUserBehaviorsPage(UserId userId, Integer behaviorType, PageRequest pageRequest) {
        return userBehaviorRepository.findPage(userId, behaviorType, pageRequest);
    }

    @Override
    @Transactional
    public boolean deleteBehavior(Long id) {
        return userBehaviorRepository.delete(id);
    }

    @Override
    @Transactional
    public boolean deleteUserBehaviors(UserId userId) {
        return userBehaviorRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public int deleteBeforeTime(LocalDateTime beforeTime) {
        return userBehaviorRepository.deleteBeforeTime(beforeTime);
    }

    @Override
    public int countUserBehaviors(UserId userId, Integer behaviorType) {
        return userBehaviorRepository.countByUserIdAndType(userId, behaviorType);
    }

    @Override
    public int countTargetBehaviors(Long targetId, Integer targetType, Integer behaviorType) {
        return userBehaviorRepository.countByTargetAndType(targetId, targetType, behaviorType);
    }

    @Override
    public boolean hasBehavior(UserId userId, Long targetId, Integer targetType, Integer behaviorType) {
        return userBehaviorRepository.exists(userId, targetId, targetType, behaviorType);
    }
}