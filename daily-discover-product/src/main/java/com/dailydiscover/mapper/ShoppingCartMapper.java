package com.dailydiscover.mapper;

import com.dailydiscover.model.ShoppingCart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 购物车表 Mapper
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    
    /**
     * 根据用户ID查询购物车列表
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<ShoppingCart> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询用户选中的购物车项
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND is_selected = true ORDER BY created_at DESC")
    List<ShoppingCart> findSelectedItemsByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和SKU ID查询购物车项
     */
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND sku_id = #{skuId}")
    ShoppingCart findByUserIdAndSkuId(@Param("userId") Long userId, @Param("skuId") Long skuId);
    
    /**
     * 统计用户购物车商品数量
     */
    @Select("SELECT SUM(quantity) FROM shopping_cart WHERE user_id = #{userId}")
    Integer countCartItems(@Param("userId") Long userId);
}