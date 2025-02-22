package com.example.mapper;

import org.apache.ibatis.annotations.Update;

public interface InventoryMapper {

    @Update("UPDATE inventory SET stock = stock - #{quantity} WHERE product_id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(Long productId, int quantity);
} 