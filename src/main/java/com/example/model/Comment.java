// src/main/java/com/example/model/Comment.java
package com.example.model;

import lombok.Data;

@Data
public class Comment {
    private String userName;
    private String userAvatarUrl;
    private String content;
    private Double rating;
    private String date;
}