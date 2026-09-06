package com.dailydiscover.feedback.service;

import com.dailydiscover.feedback.domain.Feedback;
import com.dailydiscover.feedback.repository.FeedbackRepository;
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
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final ContentRepository contentRepository;
    private final UserService userService;

    // 支持的反馈类型
    private static final Set<String> VALID_TYPES = Set.of(
            "USEFUL", "NOT_USEFUL", "IRRELEVANT", "LOW_QUALITY"
    );

    public void submitFeedback(String anonymousId, Long contentId, String feedbackType, String reason) {
        if (!VALID_TYPES.contains(feedbackType)) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "不支持的反馈类型: " + feedbackType);
        }

        User user = userService.getOrCreateUser(anonymousId);
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        // 去重：同一用户对同一内容的同类反馈只保留最新
        Optional<Feedback> existing = feedbackRepository.findByUserIdAndContentIdAndFeedbackType(
                user.getId(), contentId, feedbackType);
        
        if (existing.isPresent()) {
            Feedback f = existing.get();
            f.setReason(reason);
            feedbackRepository.save(f);
            return;
        }

        Feedback feedback = Feedback.builder()
                .user(user)
                .content(content)
                .feedbackType(feedbackType)
                .reason(reason)
                .build();

        feedbackRepository.save(feedback);
    }
}