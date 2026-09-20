package com.dailydiscover.behavior.service;

import com.dailydiscover.behavior.domain.Behavior;
import com.dailydiscover.behavior.domain.BehaviorType;
import com.dailydiscover.behavior.dto.BehaviorRequest;
import com.dailydiscover.behavior.repository.BehaviorRepository;
import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import com.dailydiscover.discovery.service.DiscoveryService;
import com.dailydiscover.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BehaviorService {

    private final BehaviorRepository behaviorRepository;
    private final UserService userService;
    private final DiscoveryService discoveryService;

    /**
     * 记录行为（03 API-MVP.md 第 7/14 节）：
     * anonymous_id 存在 → discoveryId 存在 → behaviorType 合法 → 追加写入
     */
    @Transactional
    public void recordBehavior(String anonymousId, BehaviorRequest request) {
        if (!BehaviorType.ALL.contains(request.getBehaviorType())) {
            throw new BusinessException(ErrorCode.INVALID_BEHAVIOR, HttpStatus.BAD_REQUEST);
        }

        // 确保匿名用户存在（users 表）
        userService.getOrCreateUser(anonymousId);

        // 校验发现存在且允许记录行为（业务校验，数据库无外键）
        discoveryService.getAvailableDiscovery(request.getDiscoveryId());

        String metadataJson = serializeMetadata(request.getMetadata());
        behaviorRepository.save(Behavior.builder()
                .anonymousId(anonymousId)
                .discoveryId(request.getDiscoveryId())
                .behaviorType(request.getBehaviorType())
                .metadata(metadataJson)
                .build());
        log.info("Behavior recorded: user={}, discovery={}, type={}",
                anonymousId, request.getDiscoveryId(), request.getBehaviorType());
    }

    /**
     * metadata 统一序列化为 JSONB 字符串保存
     */
    private String serializeMetadata(Object metadata) {
        if (metadata == null) {
            return null;
        }
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(metadata);
        } catch (Exception e) {
            return String.valueOf(metadata);
        }
    }
}
