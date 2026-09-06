package com.dailydiscover.recommendation.service;

import com.dailydiscover.behavior.domain.Behavior;
import com.dailydiscover.behavior.repository.BehaviorRepository;
import com.dailydiscover.content.domain.Content;
import com.dailydiscover.content.repository.ContentRepository;
import com.dailydiscover.feedback.domain.Feedback;
import com.dailydiscover.feedback.repository.FeedbackRepository;
import com.dailydiscover.user.domain.User;
import com.dailydiscover.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐服务 - MVP 规则推荐
 * 候选内容 -> 状态过滤 -> 时间过滤 -> 人工优先级 -> 简单行为排序 -> 生成今日结果
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final ContentRepository contentRepository;
    private final BehaviorRepository behaviorRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserService userService;

    @Value("${daily-discover.recommendation.max-candidates:100}")
    private int maxCandidates;

    @Value("${daily-discover.recommendation.max-results:10}")
    private int maxResults;

    /**
     * 获取今日发现列表
     */
    public List<Content> getTodayDiscover(String anonymousId, int limit) {
        User user = userService.getOrCreateUser(anonymousId);
        
        // 1. 候选内容：获取已发布内容
        List<Content> candidates = contentRepository.findPublishedContents("PUBLISHED", LocalDateTime.now())
                .stream()
                .limit(maxCandidates)
                .toList();

        if (candidates.isEmpty()) {
            return List.of();
        }

        // 2. 状态过滤 & 时间过滤 - 已在查询中完成

        // 3. 人工优先级 - 已按 priority DESC 排序

        // 4. 简单行为排序
        List<Content> ranked = rankByBehavior(user, candidates);

        // 5. 生成今日结果
        return ranked.stream()
                .limit(Math.min(limit, maxResults))
                .toList();
    }

    /**
     * 基于行为的简单排序
     * 规则：
     * - 用户未交互过的内容优先
     * - 用户点击/喜欢过的内容降权
     * - 用户标记无用/跳过的内容过滤
     */
    private List<Content> rankByBehavior(User user, List<Content> candidates) {
        Set<Long> viewedContentIds = getViewedContentIds(user.getId());
        Set<Long> likedContentIds = getLikedContentIds(user.getId());
        Set<Long> dislikedContentIds = getDislikedContentIds(user.getId());
        Set<Long> skippedContentIds = getSkippedContentIds(user.getId());

        // 过滤掉用户明确不喜欢/跳过的内容
        List<Content> filtered = candidates.stream()
                .filter(c -> !dislikedContentIds.contains(c.getId()))
                .filter(c -> !skippedContentIds.contains(c.getId()))
                .toList();

        // 计算分数并排序
        return filtered.stream()
                .map(content -> {
                    double score = content.getPriority(); // 基础分 = 人工优先级
                    
                    // 未看过加分
                    if (!viewedContentIds.contains(content.getId())) {
                        score += 100;
                    }
                    
                    // 点赞过的降权（避免重复推荐）
                    if (likedContentIds.contains(content.getId())) {
                        score -= 50;
                    }
                    
                    return new ScoredContent(content, score);
                })
                .sorted(Comparator.comparingDouble(ScoredContent::score).reversed())
                .map(ScoredContent::content)
                .toList();
    }

    private Set<Long> getViewedContentIds(Long userId) {
        return behaviorRepository.findByUserIdAndBehaviorType(userId, "VIEW").stream()
                .map(b -> b.getContent().getId())
                .collect(Collectors.toSet());
    }

    private Set<Long> getLikedContentIds(Long userId) {
        Set<Long> ids = new HashSet<>();
        ids.addAll(behaviorRepository.findByUserIdAndBehaviorType(userId, "LIKE").stream()
                .map(b -> b.getContent().getId()).toList());
        ids.addAll(feedbackRepository.findByUserIdAndContentIds(
                userId, 
                behaviorRepository.findByUserIdAndBehaviorType(userId, "LIKE").stream()
                    .map(b -> b.getContent().getId()).toList()
        ).stream().filter(f -> "USEFUL".equals(f.getFeedbackType()))
            .map(f -> f.getContent().getId()).toList());
        return ids;
    }

    private Set<Long> getDislikedContentIds(Long userId) {
        Set<Long> ids = new HashSet<>();
        ids.addAll(behaviorRepository.findByUserIdAndBehaviorType(userId, "DISLIKE").stream()
                .map(b -> b.getContent().getId()).toList());
        ids.addAll(feedbackRepository.findByUserIdAndContentIds(
                userId,
                behaviorRepository.findByUserIdAndBehaviorType(userId, "DISLIKE").stream()
                    .map(b -> b.getContent().getId()).toList()
        ).stream().filter(f -> "NOT_USEFUL".equals(f.getFeedbackType()))
            .map(f -> f.getContent().getId()).toList());
        return ids;
    }

    private Set<Long> getSkippedContentIds(Long userId) {
        return behaviorRepository.findByUserIdAndBehaviorType(userId, "SKIP").stream()
                .map(b -> b.getContent().getId())
                .collect(Collectors.toSet());
    }

    private record ScoredContent(Content content, double score) {}
}