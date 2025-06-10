package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.common.util.JsonTypeHandler;

import lombok.Data;
import java.util.List;

@Data
@TableName(value = "cart_items", autoResultMap = true)
public class CartItem {
    @TableId(type = IdType.AUTO)
    private Long id; // 购物车项ID
    @TableField("user_id")
    private Long userId; // 用户ID
    @TableField("product_id")
    private Long productId; // 商品ID
    @TableField("product_name")
    private String productName; // 商品名称
    @TableField("product_image")
    private String productImage; // 商品图片URL

    @TableField(typeHandler = JsonTypeHandler.class)
    private List<Specification> specifications; // 商品变体（如颜色、尺寸等）
    private BigDecimal price; // 商品价格
    private Integer quantity; // 商品数量
    @TableField("created_at")
    private LocalDateTime createdAt; // 记录创建时间
    @TableField("updated_at")
    private LocalDateTime updatedAt; // 记录更新时间
    private String shopName; // 店铺名称
    private String shopAvatarUrl; // 店铺头像URL
    private Long shopId; // 店铺ID

    public Long getShopId() { return shopId; }
    public void setShopId(Long shopId) { this.shopId = shopId; }
}
