package com.example.mapper;

// 检查导入路径是否正确
import com.example.model.OrderItem;
import com.example.util.SpecificationsTypeHandler; // 新增导入语句
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface OrderItemMapper {

    @Insert("INSERT INTO order_item (order_id, product_id, quantity, price, subtotal, specifications, shop_id, commission_rate, commission_amount, sku_id, sku_image) " +
            "VALUES (#{orderId}, #{productId}, #{quantity}, #{price}, #{subtotal}, #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, #{shopId}, #{commissionRate}, #{commissionAmount}, #{skuId}, #{skuImage})")
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
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "skuImage", column = "sku_image"),
        @Result(property = "commissionRate", column = "commission_rate"),
        @Result(property = "commissionAmount", column = "commission_amount"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class)
    })
    List<OrderItem> findByOrderId(Long orderId);

    @Delete("DELETE FROM order_item WHERE order_id = #{orderId}")
    void deleteByOrderId(Long orderId);

    @Update("UPDATE order_item SET quantity = #{quantity}, subtotal = #{subtotal}, " +
            "specifications = #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler} " +
            "WHERE id = #{id}")
    void updateQuantity(OrderItem orderItem);
}