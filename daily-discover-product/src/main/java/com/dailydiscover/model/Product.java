package com.dailydiscover.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Product {
    private Long id;
    private String title;
    private BigDecimal price;
    private String description;
    private String imageUrl;
    private String timeSlot;
    private String category;
    private BigDecimal originalPrice;
    private String tag;
    private String reason;
    private Boolean isHotSale = false;
    private Boolean isHighQuality = false;
    private Boolean isFastDelivery = false;
    private String features;
    private Boolean isActive = true;
    private Integer sales = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Product(String title, BigDecimal price, String description, String imageUrl, String timeSlot, String category) {
        this();
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timeSlot = timeSlot;
        this.category = category;
    }
    
    // 完整构造函数，包含所有字段
    public Product(String title, BigDecimal price, String description, String imageUrl, String timeSlot, String category, BigDecimal originalPrice, String tag, String reason) {
        this();
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timeSlot = timeSlot;
        this.category = category;
        this.originalPrice = originalPrice;
        this.tag = tag;
        this.reason = reason;
    }
    
    // 完整构造函数，包含所有字段（包含新特性字段）
    public Product(String title, BigDecimal price, String description, String imageUrl, String timeSlot, String category, BigDecimal originalPrice, String tag, String reason, Boolean isHotSale, Boolean isHighQuality, Boolean isFastDelivery, String features) {
        this();
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timeSlot = timeSlot;
        this.category = category;
        this.originalPrice = originalPrice;
        this.tag = tag;
        this.reason = reason;
        this.isHotSale = isHotSale;
        this.isHighQuality = isHighQuality;
        this.isFastDelivery = isFastDelivery;
        this.features = features;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getIsHotSale() {
        return isHotSale;
    }

    public void setIsHotSale(Boolean isHotSale) {
        this.isHotSale = isHotSale;
    }

    public Boolean getIsHighQuality() {
        return isHighQuality;
    }

    public void setIsHighQuality(Boolean isHighQuality) {
        this.isHighQuality = isHighQuality;
    }

    public Boolean getIsFastDelivery() {
        return isFastDelivery;
    }

    public void setIsFastDelivery(Boolean isFastDelivery) {
        this.isFastDelivery = isFastDelivery;
    }

    public String getFeatures() {
        return features;
    }

    public void setFeatures(String features) {
        this.features = features;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getSales() {
        return sales;
    }

    public void setSales(Integer sales) {
        this.sales = sales;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}