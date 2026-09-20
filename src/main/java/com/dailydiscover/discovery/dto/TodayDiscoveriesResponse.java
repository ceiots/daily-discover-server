package com.dailydiscover.discovery.dto;

import com.dailydiscover.discovery.domain.Discovery;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 今日发现响应（03 API-MVP.md 第 5 节）：{ date, items[] }
 */
@Data
@Builder
public class TodayDiscoveriesResponse {
    private LocalDate date;
    private List<DiscoveryItem> items;

    @Data
    @Builder
    public static class DiscoveryItem {
        private Long id;
        private String name;
        private String scene;
        private String reason;
        private String suitableFor;
        private BigDecimal price;
        private String coverUrl;
        private String actionTitle;
        private String actionUrl;
    }
}
