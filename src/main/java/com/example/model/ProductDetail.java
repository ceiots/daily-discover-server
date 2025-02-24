package com.example.model;

import com.example.config.ImageConfig;

import lombok.Data;

@Data
public class ProductDetail {
    private String type;     // 内容类型：text/image
    private String content;  // 文本内容或图片URL
    private Integer sort;    // 排序
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
         // 只有当 type 是 "image" 时，才添加图片前缀
         if ("image".equalsIgnoreCase(this.type)) {
            this.content = ImageConfig.getImagePrefix() + content;
        } else {
            this.content = content;
        }
    }

} 