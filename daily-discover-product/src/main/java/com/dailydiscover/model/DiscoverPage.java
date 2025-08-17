package com.dailydiscover.model;

import java.time.LocalDateTime;
import java.util.List;

public class DiscoverPage {
    private Long id;
    private String title;
    private String subtitle;
    private String themeImage;
    private String dateInfo;
    private List<Motto> mottos;
    private List<Product> recommendedProducts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DiscoverPage() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public DiscoverPage(String title, String subtitle, String themeImage, String dateInfo) {
        this();
        this.title = title;
        this.subtitle = subtitle;
        this.themeImage = themeImage;
        this.dateInfo = dateInfo;
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

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getThemeImage() {
        return themeImage;
    }

    public void setThemeImage(String themeImage) {
        this.themeImage = themeImage;
    }

    public String getDateInfo() {
        return dateInfo;
    }

    public void setDateInfo(String dateInfo) {
        this.dateInfo = dateInfo;
    }

    public List<Motto> getMottos() {
        return mottos;
    }

    public void setMottos(List<Motto> mottos) {
        this.mottos = mottos;
    }

    public List<Product> getRecommendedProducts() {
        return recommendedProducts;
    }

    public void setRecommendedProducts(List<Product> recommendedProducts) {
        this.recommendedProducts = recommendedProducts;
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