package com.dailydiscover.discovery.dto;

import com.dailydiscover.discovery.domain.Discovery;
import com.dailydiscover.product.domain.Product;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发现详情响应（03 API-MVP.md 第 6 节）
 */
@Data
@Builder
public class DiscoveryDetailResponse {
    private Long id;
    private String name;
    private String scene;
    private String reason;
    private String suitableFor;
    private BigDecimal price;
    private String coverUrl;
    private String actionTitle;
    private String actionUrl;
    private List<ProductItem> products;

    @Data
    @Builder
    public static class ProductItem {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String imageUrl;
        private String purchaseUrl;
        private String platform;
    }
}
