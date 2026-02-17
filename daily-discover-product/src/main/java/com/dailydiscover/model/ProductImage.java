package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品图片模型
 */
@Data
public class ProductImage {
    private Long id;
    private Long productId;
    private String imageType;
    private String imageUrl;
    private String altText;
    private Integer sortOrder;
    private Boolean isPrimary;
    private Long categoryId;
    private String customLabel;
    private Integer displayOrder;
    private Boolean isVisible;
    private LocalDateTime createdAt;
}