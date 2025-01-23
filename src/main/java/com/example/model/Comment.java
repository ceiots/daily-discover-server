// src/main/java/com/example/model/Comment.java
package com.example.model;

import com.example.config.ImageConfig;

public class Comment {
    private String userName;
    private String userAvatarUrl;
    private String content;
    private Double rating;
    private String date;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }
    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = ImageConfig.getImagePrefix() + userAvatarUrl;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Double getRating() {
        return rating;
    }
    public void setRating(Double rating) {
        this.rating = rating;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }


}