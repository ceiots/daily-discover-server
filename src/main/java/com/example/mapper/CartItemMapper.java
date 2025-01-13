package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.model.CartItem;

@Mapper
public interface CartItemMapper {

    @Select("SELECT * FROM cart_items WHERE user_id = #{userId}")
    List<CartItem> getCartItemsByUserId(Long userId);

    @Insert("INSERT INTO cart_items (user_id, product_id, product_name, product_image, product_variant, price, quantity) VALUES (#{user_id}, #{product_id}, #{product_name}, #{product_image}, #{product_variant}, #{price}, #{quantity})")
    void addCartItem(CartItem cartItem);

     @Select("SELECT COUNT(*) FROM cart_items WHERE user_id = #{user_id} AND product_id = #{product_id}")
    int countCartItem(@Param("user_id") Long user_id, @Param("product_id") Long product_id);

    @Update("UPDATE cart_items SET quantity = #{quantity},  product_variant = #{product_variant} WHERE user_id = #{user_id} AND product_id = #{product_id}")
    void updateCartItem(@Param("user_id") Long user_id, @Param("product_id") Long product_id, @Param("quantity") Integer quantity, @Param("product_variant") String product_variant);
}