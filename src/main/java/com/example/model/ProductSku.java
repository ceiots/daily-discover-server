package com.example.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.util.MapJsonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 商品SKU实体类
 */
@Data
@TableName(value = "product_sku", autoResultMap = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSku {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("sku_code")
    private String skuCode;
    
    private BigDecimal price;
    
    private Integer stock;
    
    @TableField("image_url")
    private String imageUrl;
    
    /**
     * 规格属性，使用JSON存储
     * 例如：{"颜色": "红色", "尺码": "XL"}
     */
    @TableField(typeHandler = MapJsonTypeHandler.class)
    private Map<String, String> specifications;
    
    @TableField("created_at")
    private Date createdAt;
    
    @TableField("updated_at")
    private Date updatedAt;
    
    // 非数据库字段，用于关联商品信息
    @TableField(exist = false)
    private Product product;
} 