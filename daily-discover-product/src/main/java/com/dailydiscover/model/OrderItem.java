package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 订单项表实体类
 */
@Data
@TableName("order_items")
public class OrderItem {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("sku_id")
    private Long skuId;
    
    @TableField("product_title")
    private String productTitle;
    
    @TableField("product_image")
    private String productImage;
    
    @TableField("specs_json")
    private String specsJson;
    
    @TableField("specs_text")
    private String specsText;
    
    @TableField("unit_price")
    private BigDecimal unitPrice;
    
    @TableField("quantity")
    private Integer quantity;
    
    @TableField("subtotal_amount")
    private BigDecimal subtotalAmount;
}