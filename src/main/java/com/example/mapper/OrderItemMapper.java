package com.example.mapper;

import com.example.model.OrderItem;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderItemMapper {
    
    @Insert("INSERT INTO order_item (order_id, product_id, quantity, price, subtotal, specifications) " +
            "VALUES (#{orderId}, #{productId}, #{quantity}, #{price}, #{subtotal}, " +
            "#{specifications,typeHandler=com.example.handler.SpecificationTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertOrderItem(OrderItem orderItem);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "price", column = "price"),
        @Result(property = "subtotal", column = "subtotal"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = com.example.util.SpecificationsTypeHandler.class)
    })
    List<OrderItem> findByOrderId(Long orderId);

    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    void deleteByOrderId(Long orderId);

    @Update("UPDATE order_item SET quantity = #{quantity}, subtotal = #{subtotal}, " +
            "specifications = #{specifications,typeHandler=com.example.handler.SpecificationTypeHandler} " +
            "WHERE id = #{id}")
    void updateQuantity(OrderItem orderItem);
} 