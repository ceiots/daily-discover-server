package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductDetail {
    private Long id;
    private Long productId;
    private String specifications;
    private String features;
    private String usageInstructions;
    private String precautions;
    private String packageContents;
    private String warrantyInfo;
    private String shippingInfo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}