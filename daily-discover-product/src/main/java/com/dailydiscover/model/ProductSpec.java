package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductSpec {
    private Long id;
    private Long productId;
    private String specName;
    private String specLabel;
    private String specValue;
    private String specUnit;
    private String specGroup;
    private Integer sortOrder;
    private LocalDateTime createdAt;
}