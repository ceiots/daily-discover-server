package com.example.config;
//@ConfigurationProperties(prefix = "app.image")
public class ImageConfig {

    // 定义常量
    public static final String IMAGE_PREFIX = "https://32dcc27b.r5.cpolar.top";

    /* private String prefix;
    
    // getter 和 setter
    
    public String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.startsWith("http")) {
            return imageUrl;
        }
        return prefix + imageUrl;
    } */
}