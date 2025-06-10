package com.example.user.application.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserBehaviorAssembler;
import com.example.user.application.dto.UserBehaviorDTO;
import com.example.user.application.service.UserBehaviorService;
import com.example.user.domain.model.UserBehavior;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserBehaviorDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户行为应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserBehaviorServiceImpl implements UserBehaviorService {

    private final UserBehaviorDomainService userBehaviorDomainService;
    private final UserBehaviorAssembler userBehaviorAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userBehaviorDomainService;
    }

    @Override
    @Transactional
    public UserBehaviorDTO recordBehavior(UserBehaviorDTO userBehaviorDTO) {
        UserBehavior userBehavior = userBehaviorAssembler.toDomain(userBehaviorDTO);
        UserBehavior savedBehavior = userBehaviorDomainService.recordBehavior(userBehavior);
        return userBehaviorAssembler.toDTO(savedBehavior);
    }

    @Override
    public List<UserBehaviorDTO> getUserBehaviors(Long userId, Integer behaviorType, Integer targetType, Integer limit) {
        List<UserBehavior> behaviors = userBehaviorDomainService.getUserBehaviors(
                new UserId(userId), behaviorType, targetType, limit);
        return behaviors.stream()
                .map(userBehaviorAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserBehaviorDTO> getUserBehaviorPage(Long userId, Integer behaviorType, Integer targetType, PageRequest pageRequest) {
        PageResult<UserBehavior> pageResult = userBehaviorDomainService.getUserBehaviorPage(
                new UserId(userId), behaviorType, targetType, pageRequest);
        
        List<UserBehaviorDTO> behaviorDTOs = pageResult.getList().stream()
                .map(userBehaviorAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(behaviorDTOs, pageResult.getTotal(), pageResult.getPages(), 
                pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    @Override
    public Long countBehaviorByTarget(Long targetId, Integer targetType, Integer behaviorType) {
        return userBehaviorDomainService.countBehaviorByTarget(targetId, targetType, behaviorType);
    }

    @Override
    public boolean hasUserBehavior(Long userId, Long targetId, Integer targetType, Integer behaviorType) {
        return userBehaviorDomainService.hasUserBehavior(new UserId(userId), targetId, targetType, behaviorType);
    }

    @Override
    @Transactional
    public boolean deleteBehavior(Long userId, Long targetId, Integer targetType, Integer behaviorType) {
        return userBehaviorDomainService.deleteBehavior(new UserId(userId), targetId, targetType, behaviorType);
    }
}