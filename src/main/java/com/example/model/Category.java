// Category.java
package com.example.model;

import com.example.config.ImageConfig;

public class Category {
    private Long id;
    private String name;
    private String imageUrl;
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
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = ImageConfig.getImagePrefix() + imageUrl;
    }

   
}