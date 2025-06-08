package com.example.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品标签关联
 */
@Data
@TableName("product_tag")
public class ProductTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签类型:1-普通,2-活动,3-系统
     */
    @TableField("tag_type")
    private Integer tagType;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    /**
     * 创建新的商品标签关联
     */
    public static ProductTag create(Long productId, Long tagId, String tagName, Integer tagType) {
        ProductTag tag = new ProductTag();
        tag.setProductId(productId);
        tag.setTagId(tagId);
        tag.setTagName(tagName);
        tag.setTagType(tagType != null ? tagType : 1); // 默认为普通标签
        return tag;
    }
} 