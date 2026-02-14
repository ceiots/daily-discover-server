package com.dailydiscover.mapper;

import org.apache.ibatis.annotations.*;
import java.util.Map;

@Mapper
public interface CartMapper {
    
    @Insert("INSERT INTO cart_items (user_id, product_id, quantity, created_at, updated_at) " +
            "VALUES (#{userId}, #{productId}, #{quantity}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addToCart(Map<String, Object> cartItem);
    
    @Select("SELECT quantity FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId}")
    Integer getCartItemQuantity(@Param("userId") String userId, @Param("productId") String productId);
    
    @Select("SELECT COUNT(*) FROM cart_items WHERE user_id = #{userId}")
    Integer getCartTotalCount(String userId);
    
    @Update("UPDATE cart_items SET quantity = #{quantity}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND product_id = #{productId}")
    void updateCartItemQuantity(Map<String, Object> cartItem);
    
    @Delete("DELETE FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId}")
    void removeFromCart(@Param("userId") String userId, @Param("productId") String productId);
    
    @Delete("DELETE FROM cart_items WHERE user_id = #{userId}")
    void clearCart(String userId);
}