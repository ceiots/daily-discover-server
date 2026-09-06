package com.dailydiscover.behavior.controller;

import com.dailydiscover.behavior.dto.BehaviorRequest;
import com.dailydiscover.behavior.service.BehaviorService;
import com.dailydiscover.common.response.ApiResponse;
import com.dailydiscover.common.util.AnonymousId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 行为控制器
 * - POST /api/v1/behaviors  上报行为
 */
@Slf4j
@RestController
@RequestMapping("/v1/behaviors")
@RequiredArgsConstructor
public class BehaviorController {

    private final BehaviorService behaviorService;

    /**
     * 上报用户行为
     */
    @PostMapping
    public ApiResponse<Void> recordBehavior(
            @AnonymousId String anonymousId,
            @Valid @RequestBody BehaviorRequest request) {
        log.debug("Record behavior, user: {}, contentId: {}, type: {}", 
                anonymousId, request.getContentId(), request.getBehaviorType());

        behaviorService.recordBehavior(
                anonymousId,
                request.getContentId(),
                request.getBehaviorType()
        );

        return ApiResponse.success();
    }
}