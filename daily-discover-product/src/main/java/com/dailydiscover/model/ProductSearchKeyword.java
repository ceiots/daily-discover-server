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
    
    @TableField("last_searched_at")
    private LocalDateTime lastSearchedAt;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}