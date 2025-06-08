package com.example.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 商品统计信息
 */
@Data
@TableName("product_statistics")
public class ProductStatistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 浏览次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 收藏次数
     */
    @TableField("favorite_count")
    private Integer favoriteCount;

    /**
     * 分享次数
     */
    @TableField("share_count")
    private Integer shareCount;

    /**
     * 商品评分
     */
    @TableField("rating")
    private Double rating;

    /**
     * 评论数量
     */
    @TableField("review_count")
    private Integer reviewCount;

    /**
     * 好评数
     */
    @TableField("positive_reviews")
    private Integer positiveReviews;

    /**
     * 差评数
     */
    @TableField("negative_reviews")
    private Integer negativeReviews;

    /**
     * 最后活跃时间
     */
    @TableField("last_active_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastActiveTime;

    /**
     * 每日浏览趋势
     */
    @TableField("daily_views_trend")
    private Map<String, Integer> dailyViewsTrend;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
    
    /**
     * 创建新的商品统计对象
     */
    public static ProductStatistics create(Long productId) {
        ProductStatistics statistics = new ProductStatistics();
        statistics.setProductId(productId);
        statistics.setViewCount(0);
        statistics.setFavoriteCount(0);
        statistics.setShareCount(0);
        statistics.setReviewCount(0);
        statistics.setPositiveReviews(0);
        statistics.setNegativeReviews(0);
        statistics.setLastActiveTime(new Date());
        return statistics;
    }
} 