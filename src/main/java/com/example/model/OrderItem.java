package com.example.model;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.util.JsonTypeHandler;
import lombok.Data;

@Data
@TableName(value = "order_item", autoResultMap = true)
public class OrderItem {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("product_id")
    private Long productId;
    
    private String productName;
    private String productImage;
    private String specs;
    private String attributes;
    
    private String name;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    
    // 商品规格
    @TableField(typeHandler = JsonTypeHandler.class)
    private List<Specification> specifications;
    
    // 店铺信息
    private String shopName;
    private String shopAvatarUrl;
    
    // 关联Shop对象
    @TableField(exist = false)
    private Shop shop;

    // 新增 getter 和 setter 方法
    public List<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }
}