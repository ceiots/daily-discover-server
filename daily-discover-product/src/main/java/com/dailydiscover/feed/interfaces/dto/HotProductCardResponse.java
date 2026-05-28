package com.dailydiscover.feed.interfaces.dto;

import com.dailydiscover.feed.domain.model.HotProduct;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class HotProductCardResponse {

    private Long productId;
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private String goodsSlogan;
    private String recommendationReason;
    private String hotTag;
    private String followVerification;

    public static HotProductCardResponse from(HotProduct product) {
        return HotProductCardResponse.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .imageUrl(product.getMainImageUrl())
                .price(product.getMinPrice())
                .goodsSlogan(product.getGoodsSlogan())
                .recommendationReason(product.getRecommendationReason())
                .hotTag(product.hasHotTag() ? product.getHotTag() : null)
                .followVerification(product.formatFollowVerification())
                .build();
    }
}