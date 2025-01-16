package com.example.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // 自动生成getter、setter、toString等方法
@NoArgsConstructor // 自动生成无参构造函数
@AllArgsConstructor // 自动生成全参构造函数
public class CartItem {
    private Long id; // 购物车项ID
    private Long user_id; // 用户ID
    private Long product_id; // 商品ID
    private String product_name; // 商品名称
    private String product_image; // 商品图片URL
    private String product_variant; // 商品变体（如颜色、尺寸等）
    private BigDecimal price; // 商品价格
    private Integer quantity; // 商品数量
    private LocalDateTime created_at; // 记录创建时间
    private LocalDateTime updated_at; // 记录更新时间
    private String shopName; // 店铺名称
    private String shopAvatarUrl; // 店铺头像URL
}