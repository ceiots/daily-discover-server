package com.example.model;

import java.util.Date;
import java.util.List;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品内容表实体类
 * 用于存储商品的详细展示内容
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductContent {
    private Long id;
    private Long productId;
    private List<String> images;           // 商品图片列表
    private List<ProductDetail> details;   // 商品详情内容
    private List<PurchaseNotice> purchaseNotices; // 购买须知
    private String videoUrl;               // 视频URL
    private String richDescription;        // 富文本描述
    private String seoTitle;               // SEO标题
    private String seoKeywords;            // SEO关键词
    private String seoDescription;         // SEO描述
    private Date createTime;
    private Date updateTime;
    
    /**
     * 获取主图URL，如果存在多张图片，返回第一张
     */
    public String getMainImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }
} 