package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductSearchKeyword {
    private Long id;
    private String keyword;
    private Integer searchCount;
    private Integer clickCount;
    private Integer conversionCount;
    private LocalDateTime lastSearchedAt;
    private Boolean isTrending;
    private Boolean isRecommended;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}