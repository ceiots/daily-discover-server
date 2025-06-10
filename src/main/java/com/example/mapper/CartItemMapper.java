package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.common.util.SpecificationsTypeHandler;
import com.example.model.CartItem;
import com.example.model.Specification;

@Mapper
public interface CartItemMapper {

    @Select("SELECT c.*, s.shop_name, s.shop_logo AS shop_avatar_url " +
            "FROM cart_items c " +
            "LEFT JOIN shop s ON c.shop_id = s.id " +
            "WHERE c.user_id = #{userId}")
    @Results({
        @Result(property = "specifications", column = "specifications", typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAvatarUrl", column = "shop_avatar_url")
    })
    List<CartItem> getCartItemsByUserId(Long userId);


    @Insert("INSERT INTO cart_items (user_id, product_id, product_name, product_image, specifications, price, quantity, shop_id) " +
            "VALUES (#{cartItem.userId}, #{cartItem.productId}, #{cartItem.productName}, #{cartItem.productImage}, " +
            "#{cartItem.specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, #{cartItem.price}, #{cartItem.quantity}, " +
            "#{cartItem.shopId}) " +
            "ON DUPLICATE KEY UPDATE quantity = quantity + #{cartItem.quantity}")
    void addCartItem(@Param("cartItem") CartItem cartItem);

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class)
    })
    List<CartItem> findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    @Update("UPDATE cart_items SET quantity = #{quantity},  specifications = #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler} WHERE user_id = #{userId} AND product_id = #{productId}")
    void updateCartItem(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity, @Param("specifications") String specifications);

    @Update("UPDATE cart_items SET quantity = #{quantity} WHERE id = #{itemId}")
    void updateCartItemQuantity(@Param("itemId") Long itemId, @Param("quantity") Integer quantity);

    @Delete("DELETE FROM cart_items WHERE id = #{itemId}")
    void deleteCartItem(Long itemId);
}