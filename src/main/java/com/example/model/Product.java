package com.example.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.config.ImageConfig;
import com.example.util.JsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Date;

@Data
@TableName(value = "recommendations", autoResultMap = true)
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private Integer soldCount;
    private Integer stock; // 添加库存字段

    // 规格参数，使用JSON存储
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<Specification> specifications;

    // 商品详情，支持图文
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<ProductDetail> productDetails;
    
    // 添加details字段，映射到productDetails
    @JsonProperty("details")
    @TableField(exist = false)
    private List<ProductDetail> details;

    // 购买须知
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<PurchaseNotice> purchaseNotices;

    private Date createdAt;
    private Long categoryId;
    private Integer deleted;
    
    // 添加用户ID字段，表示该商品的创建者
    private Long userId;

    // 用户评论
    @TableField(exist = false)
    private List<Comment> comments;

    // 店铺ID字段
    private Long shopId;
    
    // 店铺相关信息
    @TableField(exist = false)
    private String shopName;
    
    @TableField(exist = false)
    private String shopAvatarUrl;
    
    @TableField(exist = false)
    private String storeDescription;

    // 添加shop属性，用于与Shop对象建立关联
    @TableField(exist = false)
    private Shop shop;

    private Integer auditStatus; // 0-待审核, 1-审核通过, 2-审核拒绝
    private String auditRemark; // 审核备注
    private Date updateTime;


    private BigDecimal originalPrice;
    private Long parentCategoryId;
    private Long grandCategoryId;
    private List<String> images;
    private List<Long> tagIds;

    // 添加匹配分数字段，用于推荐系统
    private Integer matchScore;

}