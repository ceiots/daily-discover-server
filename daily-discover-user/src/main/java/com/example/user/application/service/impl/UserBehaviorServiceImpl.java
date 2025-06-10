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
        return (BaseDomainService) userBehaviorDomainService; // 假设 UserBehaviorDomainService 实现了 BaseDomainService
    }

    @Override
    @Transactional
    public UserBehaviorDTO recordBehavior(UserBehaviorDTO userBehaviorDTO) {
        // 从DTO中提取必要信息
        UserId userId = new UserId(userBehaviorDTO.getUserId());
        Integer behaviorType = userBehaviorDTO.getBehaviorType();
        Long targetId = userBehaviorDTO.getTargetId();
        Integer targetType = userBehaviorDTO.getTargetType();
        Integer deviceType = userBehaviorDTO.getDeviceType();
        String deviceId = userBehaviorDTO.getDeviceId();
        String ip = userBehaviorDTO.getIp();
        
        // 调用领域服务记录行为
        UserBehavior savedBehavior = userBehaviorDomainService.recordBehavior(
            userId, behaviorType, targetId, targetType, deviceType, deviceId, ip);
            
        // 转换为DTO并返回
        return userBehaviorAssembler.toDTO(savedBehavior);
    }

    @Override
    public List<UserBehaviorDTO> getUserBehaviors(Long userId, Integer behaviorType, Integer targetType, Integer limit) {
        // 调用领域服务获取用户行为列表
        // 由于领域服务方法签名不完全匹配，需要适配
        List<UserBehavior> behaviors;
        if (limit != null) {
            // 假设我们可以先获取所有行为，然后在应用层限制数量
            behaviors = userBehaviorDomainService.getUserBehaviors(new UserId(userId), behaviorType, limit);
            // 如果需要按targetType过滤，在应用层过滤
            if (targetType != null) {
                behaviors = behaviors.stream()
                    .filter(b -> targetType.equals(b.getTargetType()))
                    .collect(Collectors.toList());
            }
        } else {
            // 默认限制数量为20
            behaviors = userBehaviorDomainService.getUserBehaviors(new UserId(userId), behaviorType, 20);
            // 如果需要按targetType过滤，在应用层过滤
            if (targetType != null) {
                behaviors = behaviors.stream()
                    .filter(b -> targetType.equals(b.getTargetType()))
                    .collect(Collectors.toList());
            }
        }
        
        // 转换为DTO列表并返回
        return behaviors.stream()
                .map(userBehaviorAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserBehaviorDTO> getUserBehaviorPage(Long userId, Integer behaviorType, Integer targetType, PageRequest pageRequest) {
        // 这里需要实现分页查询，但领域服务没有直接提供对应方法
        // 我们需要使用现有方法进行适配
        
        // 先获取用户行为分页结果
        PageResult<UserBehavior> pageResult = new PageResult<>();
        
        // 假设我们可以使用现有方法获取分页数据
        List<UserBehavior> behaviors = userBehaviorDomainService.getUserBehaviors(new UserId(userId), behaviorType, 
                pageRequest.getPageSize() * pageRequest.getPageNum());
        
        // 如果需要按targetType过滤，在应用层过滤
        if (targetType != null) {
            behaviors = behaviors.stream()
                .filter(b -> targetType.equals(b.getTargetType()))
                .collect(Collectors.toList());
        }
        
        // 手动分页
        int start = (pageRequest.getPageNum() - 1) * pageRequest.getPageSize();
        int end = Math.min(start + pageRequest.getPageSize(), behaviors.size());
        
        List<UserBehavior> pagedBehaviors;
        if (start < behaviors.size()) {
            pagedBehaviors = behaviors.subList(start, end);
        } else {
            pagedBehaviors = List.of();
        }
        
        // 转换为DTO列表
        List<UserBehaviorDTO> behaviorDTOs = pagedBehaviors.stream()
                .map(userBehaviorAssembler::toDTO)
                .collect(Collectors.toList());
        
        // 创建分页结果
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), 
                behaviors.size(), behaviorDTOs);
    }

    @Override
    public Long countBehaviorByTarget(Long targetId, Integer targetType, Integer behaviorType) {
        // 调用领域服务统计目标行为数量
        int count = userBehaviorDomainService.countTargetBehaviors(targetId, targetType, behaviorType);
        return (long) count;
    }

    @Override
    public boolean hasUserBehavior(Long userId, Long targetId, Integer targetType, Integer behaviorType) {
        // 调用领域服务检查用户是否有指定行为
        return userBehaviorDomainService.hasBehavior(new UserId(userId), targetId, targetType, behaviorType);
    }

    @Override
    @Transactional
    public boolean deleteBehavior(Long userId, Long targetId, Integer targetType, Integer behaviorType) {
        // 领域服务没有直接提供按这些条件删除的方法
        // 我们需要先查找对应的行为，然后删除
        
        // 查找符合条件的行为
        List<UserBehavior> behaviors = userBehaviorDomainService.getUserBehaviors(new UserId(userId), behaviorType, 100);
        behaviors = behaviors.stream()
            .filter(b -> targetId.equals(b.getTargetId()) && targetType.equals(b.getTargetType()))
            .collect(Collectors.toList());
        
        // 如果找到了行为，删除第一个
        if (!behaviors.isEmpty()) {
            return userBehaviorDomainService.deleteBehavior(behaviors.get(0).getId());
        }
        
        return false;
    }
}