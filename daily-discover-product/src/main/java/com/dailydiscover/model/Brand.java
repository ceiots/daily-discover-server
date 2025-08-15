package com.dailydiscover.model;

import java.util.Date;

public class Brand {
    private Long id;
    private String name;
    private String logo;
    private String description;
    private String category;
    private String followers;
    private String trend;
    private Boolean featured;
    private Date createdAt;
    private Date updatedAt;

    // 构造函数
    public Brand() {}

    public Brand(Long id, String name, String logo, String description, String category, String followers, String trend, Boolean featured) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.description = description;
        this.category = category;
        this.followers = followers;
        this.trend = trend;
        this.featured = featured;
    }

    // Getter和Setter方法
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public Boolean getFeatured() {
        return featured;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}