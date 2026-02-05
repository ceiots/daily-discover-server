package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductImage {
    private Long id;
    private Long productId;
    private String imageType;
    private String imageUrl;
    private String altText;
    private Integer sortOrder;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}