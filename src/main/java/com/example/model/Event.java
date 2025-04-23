package com.example.model;

import java.util.Date;

import org.jsoup.Jsoup;

import com.example.config.ImageConfig;

public class Event {
    private Long id;
    private String title;
    private String description;
    private String category;
    private Date eventDate; // 使用 Date 类型来存储日期
    private String imageUrl;
    private String plainDescription;

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

  
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = ImageConfig.getImagePrefix() + imageUrl;
    }

     // 获取不含HTML标签的纯文本描述
     public String getPlainDescription() {
        if (plainDescription == null && description != null) {
            // 生成纯文本描述 (最多80字符)
            plainDescription = Jsoup.parse(description).text();
            if (plainDescription.length() > 80) {
                plainDescription = plainDescription.substring(0, 77) + "...";
            }
        }
        return plainDescription;
    }

    public void setPlainDescription(String plainDescription) {
        this.plainDescription = plainDescription;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }
}