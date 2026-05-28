package com.dailydiscover.feed.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_display_read_model")
public class HotProductReadModel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("product_id")
    private Long productId;

    @TableField("title")
    private String title;

    @TableField("main_image_url")
    private String mainImageUrl;

    @TableField("min_price")
    private BigDecimal minPrice;

    @TableField("goods_slogan")
    private String goodsSlogan;

    @TableField("recommendation_reason")
    private String recommendationReason;

    @TableField("category_id")
    private Long categoryId;

    @TableField("brand")
    private String brand;

    @TableField("status")
    private Integer status;

    @TableField("sales_count")
    private Integer salesCount;

    @TableField("view_count")
    private Integer viewCount;

    @TableField("favorite_count")
    private Integer favoriteCount;

    @TableField("sales_growth_rate")
    private BigDecimal salesGrowthRate;

    @TableField("hot_score")
    private BigDecimal hotScore;

    @TableField("average_rating")
    private BigDecimal averageRating;

    @TableField("total_reviews")
    private Integer totalReviews;

    @TableField("positive_rate")
    private BigDecimal positiveRate;

    @TableField("is_trending")
    private Integer isTrending;

    @TableField("is_new_arrival")
    private Integer isNewArrival;

    @TableField("hot_tag")
    private String hotTag;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}