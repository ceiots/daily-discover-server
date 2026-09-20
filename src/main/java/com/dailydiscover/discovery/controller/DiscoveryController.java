package com.dailydiscover.discovery.controller;

import com.dailydiscover.common.response.ApiResponse;
import com.dailydiscover.common.util.AnonymousId;
import com.dailydiscover.discovery.domain.Discovery;
import com.dailydiscover.discovery.dto.DiscoveryDetailResponse;
import com.dailydiscover.discovery.dto.TodayDiscoveriesResponse;
import com.dailydiscover.discovery.service.DiscoveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 发现接口（03 API-MVP.md P0）：
 * - GET /api/v1/discoveries/today
 * - GET /api/v1/discoveries/{id}
 */
@Slf4j
@RestController
@RequestMapping("/v1/discoveries")
@RequiredArgsConstructor
public class DiscoveryController {

    private final DiscoveryService discoveryService;

    @GetMapping("/today")
    public ApiResponse<TodayDiscoveriesResponse> getToday(@AnonymousId String anonymousId) {
        log.info("GET /discoveries/today, user={}", anonymousId);

        List<TodayDiscoveriesResponse.DiscoveryItem> items =
                discoveryService.getTodayDiscoveries().stream()
                        .map(d -> TodayDiscoveriesResponse.DiscoveryItem.builder()
                                .id(d.getId())
                                .name(d.getName())
                                .scene(d.getScene())
                                .reason(d.getReason())
                                .suitableFor(d.getSuitableFor())
                                .price(d.getPrice())
                                .coverUrl(d.getCoverUrl())
                                .actionTitle(d.getActionTitle())
                                .actionUrl(d.getActionUrl())
                                .build())
                        .toList();

        return ApiResponse.success(TodayDiscoveriesResponse.builder()
                .date(LocalDate.now())
                .items(items)
                .build());
    }

    @GetMapping("/{id}")
    public ApiResponse<DiscoveryDetailResponse> getDetail(
            @AnonymousId String anonymousId, @PathVariable Long id) {
        log.info("GET /discoveries/{}, user={}", id, anonymousId);

        Discovery discovery = discoveryService.getAvailableDiscovery(id);
        return ApiResponse.success(discoveryService.buildDetail(discovery));
    }
}
