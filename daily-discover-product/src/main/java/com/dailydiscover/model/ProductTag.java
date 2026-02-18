package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品标签表
 */
@Data
@TableName("product_tags")
public class ProductTag {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("tag_name")
    private String tagName;
    
    @TableField("tag_type")
    private String tagType;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}