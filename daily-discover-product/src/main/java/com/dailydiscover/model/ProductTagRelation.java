package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 商品标签关联表
 */
@Data
@TableName("product_tag_relations")
public class ProductTagRelation {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("tag_id")
    private Long tagId;
}