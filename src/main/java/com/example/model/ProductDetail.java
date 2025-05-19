package com.example.model;

import com.example.config.ImageConfig;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDetail {
    private Long id;         // 添加id字段
    private String type;     // 内容类型：text/image
    private String content;  // 文本内容或图片URL
    private Integer sort;    // 排序
} 