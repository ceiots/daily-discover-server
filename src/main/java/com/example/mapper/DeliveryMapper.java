package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DeliveryMapper {

    @Update("UPDATE deliveries SET status = #{status} WHERE order_id = #{orderId}")
    void updateStatus(Long orderId, String status);
} 