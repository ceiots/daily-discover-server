package com.example.model;

import java.util.Date;
import java.util.List;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品评价实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductReview {
    private Long id;
    private Long productId;
    private Long orderId;
    private Long userId;
    private Long skuId;          // 购买的SKU ID
    private Integer rating;      // 评分(1-5)
    private String content;      // 评论内容
    private List<String> images; // 评论图片
    private Boolean isAnonymous; // 是否匿名评价
    private String replyContent; // 商家回复内容
    private Date replyTime;      // 商家回复时间
    private Integer status;      // 状态:0-隐藏,1-显示
    private Boolean hasImage;    // 是否有图片
    private Date createTime;
    private Date updateTime;
    
    // 非持久化字段，用于API展示
    private transient User user;
    private transient ProductSku sku;
    
    /**
     * 创建新的商品评价
     */
    public static ProductReview create(Long productId, Long orderId, Long userId, Long skuId, 
                                      Integer rating, String content, List<String> images, Boolean isAnonymous) {
        ProductReview review = new ProductReview();
        review.setProductId(productId);
        review.setOrderId(orderId);
        review.setUserId(userId);
        review.setSkuId(skuId);
        review.setRating(rating);
        review.setContent(content);
        review.setImages(images);
        review.setIsAnonymous(isAnonymous);
        review.setStatus(1); // 默认显示
        review.setHasImage(images != null && !images.isEmpty());
        return review;
    }
    
    /**
     * 商家回复
     */
    public void reply(String replyContent) {
        this.replyContent = replyContent;
        this.replyTime = new Date();
    }
    
    /**
     * 获取评分等级描述
     */
    public String getRatingLevelDesc() {
        if (rating == null) {
            return "未评分";
        }
        
        switch (rating) {
            case 1: return "非常差";
            case 2: return "差";
            case 3: return "一般";
            case 4: return "好";
            case 5: return "非常好";
            default: return "未知评分";
        }
    }
    
    /**
     * 判断是否为好评
     */
    public boolean isPositiveReview() {
        return rating != null && rating >= 4;
    }
    
    /**
     * 判断是否为差评
     */
    public boolean isNegativeReview() {
        return rating != null && rating <= 2;
    }
    
    /**
     * 检查是否是中评
     */
    public boolean isNeutralReview() {
        return rating != null && rating == 3;
    }
    
    /**
     * 获取状态描述
     */
    public String getStatusDesc() {
        if (status == null) {
            return "未知状态";
        }
        
        switch (status) {
            case 0: return "隐藏";
            case 1: return "显示";
            default: return "未知状态";
        }
    }
} 