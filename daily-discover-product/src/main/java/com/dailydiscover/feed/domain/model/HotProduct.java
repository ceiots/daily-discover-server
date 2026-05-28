package com.dailydiscover.feed.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class HotProduct {

    private Long id;
    private Long productId;
    private String title;
    private String mainImageUrl;
    private BigDecimal minPrice;
    private String goodsSlogan;
    private String recommendationReason;
    private Long categoryId;
    private String brand;

    private Integer salesCount;
    private Integer viewCount;
    private Integer favoriteCount;
    private BigDecimal salesGrowthRate;
    private BigDecimal hotScore;

    private BigDecimal averageRating;
    private Integer totalReviews;
    private BigDecimal positiveRate;

    private Boolean isTrending;
    private Boolean isNewArrival;
    private String hotTag;

    public String formatFollowVerification() {
        String buyingCount = salesCount >= 10000
                ? String.format("%d万人正在买", salesCount / 10000)
                : String.format("%d人正在买", salesCount);
        String worthRate = positiveRate != null && positiveRate.compareTo(BigDecimal.ZERO) > 0
                ? String.format("%.0f%%觉得值得跟", positiveRate.doubleValue())
                : "";
        return worthRate.isEmpty() ? buyingCount : buyingCount + "，" + worthRate;
    }

    public boolean hasHotTag() {
        return hotTag != null && !hotTag.isEmpty();
    }
}