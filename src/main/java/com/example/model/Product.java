package com.example.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.config.ImageConfig;
import com.example.util.JsonTypeHandler;
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
    private String shopName;
    private BigDecimal price;
    private Integer soldCount;
    private String shopAvatarUrl;
    
    // 规格参数，使用JSON存储
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<Specification> specifications;
    
    // 商品详情，支持图文
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<ProductDetail> productDetails;
    
    // 购买须知
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<PurchaseNotice> purchaseNotices;
    
    private String storeDescription;
    private Date createdAt;
    private Long categoryId;
    private Integer deleted;
    
    // 用户评论
    @TableField(exist = false)
    private List<Comment> comments;

    // 店铺ID字段
    private Long shopId; 
    
    // 店铺关联对象
    @TableField(exist = false)
    private Shop shop;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = ImageConfig.getImagePrefix() + imageUrl;
    }

    public String getShopAvatarUrl() {
        return shopAvatarUrl;
    }

    public void setShopAvatarUrl(String shopAvatarUrl) {
        this.shopAvatarUrl = ImageConfig.getImagePrefix() + shopAvatarUrl;
    }
}