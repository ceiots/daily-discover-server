package com.dailydiscover.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductEntity {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Long categoryId;
    private Boolean isActive;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductEntity() {
        this.isActive = true;
        this.viewCount = 0;
        this.likeCount = 0;
        this.favoriteCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ProductEntity(String name, String description, BigDecimal price, String imageUrl, Long categoryId) {
        this();
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Integer favoriteCount) {
        this.favoriteCount = favoriteCount;
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
}