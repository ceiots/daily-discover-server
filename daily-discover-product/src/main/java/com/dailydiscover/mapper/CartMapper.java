package com.dailydiscover.mapper;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface CartMapper {
    
    // 基础购物车操作（支持SKU规格）
    @Insert("INSERT INTO shopping_cart (user_id, product_id, sku_id, quantity, specs_json, specs_text, is_selected, created_at, updated_at) " +
            "VALUES (#{userId}, #{productId}, #{skuId}, #{quantity}, #{specsJson}, #{specsText}, #{isSelected}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void addToCart(Map<String, Object> cartItem);
    
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Map<String, Object>> getCartItems(@Param("userId") Long userId);
    
    @Select("SELECT * FROM shopping_cart WHERE id = #{cartItemId}")
    Map<String, Object> getCartItemById(@Param("cartItemId") Long cartItemId);
    
    @Select("SELECT COUNT(*) FROM shopping_cart WHERE user_id = #{userId}")
    Integer getCartTotalCount(@Param("userId") Long userId);
    
    @Select("SELECT SUM(quantity) FROM shopping_cart WHERE user_id = #{userId}")
    Integer getCartTotalQuantity(@Param("userId") Long userId);
    
    @Update("UPDATE shopping_cart SET quantity = #{quantity}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = #{cartItemId}")
    void updateCartItemQuantity(@Param("cartItemId") Long cartItemId, @Param("quantity") Integer quantity);
    
    @Delete("DELETE FROM shopping_cart WHERE id = #{cartItemId}")
    void removeFromCart(@Param("cartItemId") Long cartItemId);
    
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void clearCart(@Param("userId") Long userId);
    
    @Delete({"<script>",
            "DELETE FROM shopping_cart WHERE id IN",
            "<foreach collection='cartItemIds' item='cartItemId' open='(' separator=',' close=')'>",
            "#{cartItemId}",
            "</foreach>",
            "</script>"})
    void batchRemoveFromCart(@Param("cartItemIds") List<Long> cartItemIds);
    
    @Update("UPDATE shopping_cart SET is_selected = #{isSelected}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = #{cartItemId}")
    void updateCartItemSelection(@Param("cartItemId") Long cartItemId, @Param("isSelected") Integer isSelected);
    
    @Update({"<script>",
            "UPDATE shopping_cart SET is_selected = #{isSelected}, updated_at = CURRENT_TIMESTAMP WHERE id IN",
            "<foreach collection='cartItemIds' item='cartItemId' open='(' separator=',' close=')'>",
            "#{cartItemId}",
            "</foreach>",
            "</script>"})
    void batchUpdateCartItemSelection(@Param("cartItemIds") List<Long> cartItemIds, @Param("isSelected") Integer isSelected);
    
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND is_selected = 1 ORDER BY created_at DESC")
    List<Map<String, Object>> getSelectedCartItems(@Param("userId") Long userId);
    
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND sku_id = #{skuId}")
    Map<String, Object> getCartItemBySku(@Param("userId") Long userId, @Param("skuId") Long skuId);
    
    @Update("UPDATE shopping_cart SET quantity = quantity + #{quantity}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND sku_id = #{skuId}")
    void updateCartItemQuantityBySku(@Param("userId") Long userId, @Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}