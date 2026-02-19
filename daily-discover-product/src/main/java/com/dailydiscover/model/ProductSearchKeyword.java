package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_search_keywords")
public class ProductSearchKeyword {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("keyword")
    private String keyword;
    
    @TableField("search_count")
    private Integer searchCount;
    
    @TableField("click_count")
    private Integer clickCount;
    
    @TableField("conversion_count")
    private Integer conversionCount;
    
    @TableField("last_searched_at")
    private LocalDateTime lastSearchedAt;
    
    @TableField("is_trending")
    private Boolean isTrending;
    
    @TableField("is_recommended")
    private Boolean isRecommended;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}