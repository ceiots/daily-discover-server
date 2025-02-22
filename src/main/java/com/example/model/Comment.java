// src/main/java/com/example/model/Comment.java
package com.example.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Comment {
    private String userName;
    private String userAvatarUrl;
    private String content;
    private BigDecimal rating;
    private Date date;
}