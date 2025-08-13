package com.dailydiscover.model;

import java.time.LocalDateTime;

public class Topic {
    
    private Long id;
    
    private String title;
    
    private String heat;
    
    private String trend;
    
    private String icon;
    
    private String description;
    
    private Product relatedProduct;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private Boolean isActive = true;
    
    // 构造函数
    public Topic() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Topic(String title, String heat, String trend, String icon, 
                 String description, Product relatedProduct) {
        this();
        this.title = title;
        this.heat = heat;
        this.trend = trend;
        this.icon = icon;
        this.description = description;
        this.relatedProduct = relatedProduct;
    }
    
    // Getter和Setter方法
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
    
    public String getHeat() {
        return heat;
    }
    
    public void setHeat(String heat) {
        this.heat = heat;
    }
    
    public String getTrend() {
        return trend;
    }
    
    public void setTrend(String trend) {
        this.trend = trend;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Product getRelatedProduct() {
        return relatedProduct;
    }
    
    public void setRelatedProduct(Product relatedProduct) {
        this.relatedProduct = relatedProduct;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}