package com.dailydiscover.config;

import org.springframework.stereotype.Component;

@Component
public class ImageConfig {

    private static String imagePrefix;
    public static String getImagePrefix() {
        return imagePrefix;
    }

    public static void setImagePrefix(String imagePrefix) {
        ImageConfig.imagePrefix = imagePrefix;
    }

    public static String getFullImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.startsWith("http") || imageUrl.startsWith("https")) {
            return imageUrl;
        }
        return getImagePrefix() + imageUrl;
    }
}