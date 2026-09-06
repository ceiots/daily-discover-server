package com.dailydiscover.recommendation.controller;

import com.dailydiscover.behavior.service.BehaviorService;
import com.dailydiscover.common.response.ApiResponse;
import com.dailydiscover.common.util.AnonymousId;
import com.dailydiscover.content.domain.Content;
import com.dailydiscover.content.dto.ContentDetailResponse;
import com.dailydiscover.content.service.ContentService;
import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import com.dailydiscover.recommendation.dto.TodayDiscoverResponse;
import com.dailydiscover.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 发现控制器
 * - GET  /api/v1/discover/today  今日发现
 * - GET  /api/v1/discover/{id}   详情
 */
@Slf4j
@RestController
@RequestMapping("/v1/discover")
@RequiredArgsConstructor
public class DiscoverController {

    private final RecommendationService recommendationService;
    private final ContentService contentService;
    private final BehaviorService behaviorService;

    /**
     * 获取今日发现
     */
    @GetMapping("/today")
    public ApiResponse<TodayDiscoverResponse> getTodayDiscover(
            @AnonymousId String anonymousId,
            @RequestParam(defaultValue = "10") Integer limit) {
        log.debug("Get today discover for user: {}, limit: {}", anonymousId, limit);

        List<Content> contents = recommendationService.getTodayDiscover(anonymousId, limit);
        
        List<TodayDiscoverResponse.DiscoverItem> items = contents.stream()
                .map(TodayDiscoverResponse::fromContent)
                .toList();

        TodayDiscoverResponse response = new TodayDiscoverResponse(items);
        return ApiResponse.success(response);
    }

    /**
     * 获取内容详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ContentDetailResponse> getDiscoverDetail(
            @AnonymousId String anonymousId,
            @PathVariable Long id) {
        log.debug("Get discover detail, id: {}, user: {}", id, anonymousId);

        Content content = contentService.findPublishedById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        // 自动记录 VIEW 行为
        try {
            behaviorService.recordBehavior(anonymousId, id, "VIEW");
        } catch (Exception e) {
            log.warn("Failed to record VIEW behavior: {}", e.getMessage());
        }

        ContentDetailResponse response = ContentDetailResponse.fromContent(content);
        return ApiResponse.success(response);
    }
}