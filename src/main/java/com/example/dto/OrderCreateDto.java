package com.example.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import com.example.model.Specification;
import com.example.model.Order;

@Data
public class OrderCreateDTO {
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer payType;
    private AddressDto addressDto;
    private List<OrderItemDto> items;
    private Order order;

    @Data
    public static class OrderItemDto {
        private Long id;
        private Long productId;
        private String name;
        private String imageUrl;
        private String shopAvatarUrl;
        private String shopName;
        private Long shopId;
        private Long skuId;
        private Integer quantity;
        private BigDecimal price;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public AddressDto getAddressDto() {
        return addressDto;
    }

    public void setAddressDto(AddressDto addressDto) {
        this.addressDto = addressDto;
    }
}
