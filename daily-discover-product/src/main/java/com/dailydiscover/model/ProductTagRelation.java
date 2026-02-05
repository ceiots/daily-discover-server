package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductTagRelation {
    private Long id;
    private Long productId;
    private Long tagId;
    private BigDecimal tagWeight;
    private LocalDateTime createdAt;
}