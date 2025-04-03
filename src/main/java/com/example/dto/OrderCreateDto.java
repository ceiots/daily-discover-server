package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.example.model.Specification;

@Data
public class OrderCreateDto {
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer payType;
    private AddressDto address;
    private List<OrderItemDto> items;

    @Data
    public static class OrderItemDto {
        private Long id;
        private Long productId;
        private Integer quantity;
        private BigDecimal price;
        private List<Specification> specifications; // 添加规格信息字段
    }
}
