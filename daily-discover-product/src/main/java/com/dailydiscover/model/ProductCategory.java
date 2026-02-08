package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductCategory {
    private Long id;
    
    public void setId(Long id) {
        this.id = id;
    }
    private Long parentId;
    private String name;
    private String description;
    private String imageUrl;
    private Integer sortOrder;
    private Integer level;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}