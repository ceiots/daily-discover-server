package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.config.ImageConfig;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor // 自动生成无参构造函数
@AllArgsConstructor // 自动生成全参构造函数
public class CartItem {
    private Long id; // 购物车项ID
    private Long user_id; // 用户ID
    private Long product_id; // 商品ID
    private String product_name; // 商品名称
    private String product_image; // 商品图片URL
    private String product_variant; // 商品变体（如颜色、尺寸等）
    private BigDecimal price; // 商品价格
    private Integer quantity; // 商品数量
    private LocalDateTime created_at; // 记录创建时间
    private LocalDateTime updated_at; // 记录更新时间
    private String shopName; // 店铺名称
    private String shopAvatarUrl; // 店铺头像URL
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUser_id() {
        return user_id;
    }
    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }
    public Long getProduct_id() {
        return product_id;
    }
    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }
    public String getProduct_name() {
        return product_name;
    }
    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
    public String getProduct_image() {
        return product_image;
    }
    public void setProduct_image(String product_image) {
        this.product_image = ImageConfig.IMAGE_PREFIX + product_image;
    }
    public String getProduct_variant() {
        return product_variant;
    }
    public void setProduct_variant(String product_variant) {
        this.product_variant = product_variant;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
    public LocalDateTime getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
    public String getShopName() {
        return shopName;
    }
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
    public String getShopAvatarUrl() {
        return shopAvatarUrl;
    }
    public void setShopAvatarUrl(String shopAvatarUrl) {
        this.shopAvatarUrl = ImageConfig.IMAGE_PREFIX + shopAvatarUrl;
    }

    // Getters and Setters
}