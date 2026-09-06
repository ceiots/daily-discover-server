package com.dailydiscover.behavior.service;

import com.dailydiscover.behavior.domain.Behavior;
import com.dailydiscover.behavior.repository.BehaviorRepository;
import com.dailydiscover.content.domain.Content;
import com.dailydiscover.content.repository.ContentRepository;
import com.dailydiscover.user.domain.User;
import com.dailydiscover.user.service.UserService;
import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BehaviorService {

    private final BehaviorRepository behaviorRepository;
    private final ContentRepository contentRepository;
    private final UserService userService;

    // 支持的行为类型
    private static final Set<String> VALID_TYPES = Set.of(
            "VIEW", "CLICK", "LIKE", "DISLIKE", "SHARE", "SKIP"
    );

    public void recordBehavior(String anonymousId, Long contentId, String behaviorType) {
        if (!VALID_TYPES.contains(behaviorType)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "不支持的行为类型: " + behaviorType);
        }

        User user = userService.getOrCreateUser(anonymousId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        // VIEW 行为允许重复记录，其他行为去重
        if (!"VIEW".equals(behaviorType)) {
            if (behaviorRepository.existsByUserIdAndContentIdAndBehaviorType(user.getId(), contentId, behaviorType)) {
                return; // 已存在，忽略
            }
        }

        Behavior behavior = Behavior.builder()
                .user(user)
                .content(content)
                .behaviorType(behaviorType)
                .build();

        behaviorRepository.save(behavior);
    }
}