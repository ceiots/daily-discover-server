// src/main/java/com/example/model/Comment.java
package com.example.model;

import com.example.config.ImageConfig;
import lombok.Data;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;


@Data
public class Comment {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

    private String userName;
    private String userAvatarUrl;
    private String content;
    private BigDecimal rating;
    private Date date;

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public String getDate() {
        return sdf.format(new Date());
    }

    public void setDate(Date date) {
        this.date = date;
    }

}