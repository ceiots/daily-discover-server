package com.example.model;

import java.util.List;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long orderId; // 关联的订单 ID
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private List<Specification> specifications; // 新增属性，关联商品规格

    // 新增 getter 和 setter 方法
    public List<Specification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Specification> specifications) {
        this.specifications = specifications;
    }
}