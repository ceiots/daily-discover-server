package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductAttribute {
    private Long id;
    private Long productId;
    private Boolean isNew;
    private Boolean isHot;
    private Boolean isRecommended;
    private String urgencyLevel;
    private String hotspotType;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}