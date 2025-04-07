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

import com.example.model.CartItem;
import com.example.util.SpecificationsTypeHandler;

@Mapper
public interface CartItemMapper {

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class)
    })
    List<CartItem> getCartItemsByUserId(Long userId);

    @Insert("INSERT INTO cart_items (user_id, product_id, product_name, product_image, specifications, price, quantity, shopName, shopAvatarUrl) VALUES (#{userId}, #{productId}, #{productName}, #{productImage}, #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, #{price}, #{quantity}, #{shopName}, #{shopAvatarUrl})")
    void addCartItem(CartItem cartItem);

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId} AND product_id = #{productId}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class)
    })
    CartItem findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Update("UPDATE cart_items SET quantity = #{quantity},  specifications = #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler} WHERE user_id = #{userId} AND product_id = #{productId}")
    void updateCartItem(@Param("userId") Long userId, @Param("productId") Long productId, @Param("quantity") Integer quantity, @Param("specifications") String specifications);

    @Update("UPDATE cart_items SET quantity = #{quantity} WHERE id = #{itemId}")
    void updateCartItemQuantity(@Param("itemId") Long itemId, @Param("quantity") Integer quantity);

    @Delete("DELETE FROM cart_items WHERE id = #{itemId}")
    void deleteCartItem(Long itemId);
}