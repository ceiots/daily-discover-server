package com.example.model;

import lombok.Data;

@Data
public class ProductDetail {
    private String type;     // 内容类型：text/image
    private String content;  // 文本内容或图片URL
    private Integer sort;    // 排序
} 