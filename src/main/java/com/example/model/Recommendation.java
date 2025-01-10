package com.example.model;

import lombok.Data;

@Data
public class Recommendation {
    private Long id;
    private String title;
    private String imageUrl;
    private String shopName;
    private String shopAvatarUrl; // 新增字段
    private Double price;
    private Integer soldCount;

    // Getters and Setters
    // ...
}