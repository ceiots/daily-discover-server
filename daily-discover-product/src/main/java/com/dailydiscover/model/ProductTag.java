package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductTag {
    private Long id;
    private String tagName;
    private String tagType;
    private String tagColor;
    private Integer usageCount;
    private Boolean isHot;
    private Boolean isRecommended;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}