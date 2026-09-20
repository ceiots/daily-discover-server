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
 * 行为接口（03 API-MVP.md P0）：POST /api/v1/behaviors
 * 用户判断（INTERESTED / NOT_INTERESTED / SKIP）也统一通过本接口记录
 */
@Slf4j
@RestController
@RequestMapping("/v1/behaviors")
@RequiredArgsConstructor
public class BehaviorController {

    private final BehaviorService behaviorService;

    @PostMapping
    public ApiResponse<Void> recordBehavior(
            @AnonymousId String anonymousId,
            @Valid @RequestBody BehaviorRequest request) {
        behaviorService.recordBehavior(anonymousId, request);
        return ApiResponse.success();
    }
}
