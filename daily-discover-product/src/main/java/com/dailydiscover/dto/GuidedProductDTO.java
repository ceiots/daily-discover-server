package com.dailydiscover.dto;

import lombok.Data;

/**
 * 引导推荐商品DTO
 */
@Data
public class GuidedProductDTO {
    private String id;
    private String title;
    private String imageUrl;
    private String price;
    private String currentPrice;
    private String originalPrice;
    private Double discount;
    private Double rating;
    private Integer reviews;
    private String category;
}