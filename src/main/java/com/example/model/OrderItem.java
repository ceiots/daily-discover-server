package com.example.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Integer quantity;
    private String specs;
    private String attributes;

    private String shopAvatarUrl;
    private String imageUrl;
    private String shopName;
    private BigDecimal subtotal;
    private List<Specification> specifications;
    private String name;
    // 新增 getter 和 setter 方法
    public List<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }

}