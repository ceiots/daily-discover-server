package com.dailydiscover.dto;

import lombok.Data;

/**
 * 商品图片数据传输对象
 */
@Data
public class ProductImageDTO {
    private Long id;
    private String imageUrl;
    private String altText;
    private String categoryName;
    private String displayName;
    private String customLabel;
    private Integer displayOrder;
    private Boolean isPrimary;
}