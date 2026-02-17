package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 图片分类配置模型
 */
@Data
public class ImageCategory {
    private Long id;
    private String categoryName;
    private String categoryType;
    private String displayName;
    private String description;
    private Integer sortOrder;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}