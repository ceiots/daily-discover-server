package com.dailydiscover.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DailyTheme {
    private Long id;
    private String title;
    private String subtitle;
    private String imageUrl;
    private LocalDate themeDate;
    private String themeType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DailyTheme() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public DailyTheme(String title, String subtitle, String imageUrl, LocalDate themeDate, String themeType) {
        this();
        this.title = title;
        this.subtitle = subtitle;
        this.imageUrl = imageUrl;
        this.themeDate = themeDate;
        this.themeType = themeType;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getThemeDate() {
        return themeDate;
    }

    public void setThemeDate(LocalDate themeDate) {
        this.themeDate = themeDate;
    }

    public String getThemeType() {
        return themeType;
    }

    public void setThemeType(String themeType) {
        this.themeType = themeType;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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