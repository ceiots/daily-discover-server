package com.example.mapper;

import com.example.model.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import com.example.util.ListTypeHandler;

public interface OrderMapper {

    @Insert("INSERT INTO orders (user_id, product_ids, shipping_address, status, created_at) " +
           "VALUES (#{userId}, #{productIds,typeHandler=com.example.config.ListTypeHandler}, " +
           "#{shippingAddress}, #{status}, #{createdAt})")
    void insert(Order order);

    @Select("SELECT * FROM orders WHERE id = #{orderId}")
    @Results({
        @Result(property = "productIds", column = "product_ids", 
                typeHandler = ListTypeHandler.class)
    })
    Order findById(Long orderId);
} 