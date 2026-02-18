package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 查询所有启用状态的商品
     */
    @Select("SELECT * FROM products WHERE status = 'active' ORDER BY created_at DESC")
    List<Product> findAllActive();
    
    /**
     * 根据商家ID查询商品
     */
    @Select("SELECT * FROM products WHERE seller_id = #{sellerId} AND status = 'active'")
    List<Product> findBySellerId(@Param("sellerId") Long sellerId);
    
    /**
     * 根据分类ID查询商品
     */
    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND status = 'active'")
    List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 查询热门商品
     */
    @Select("SELECT * FROM products WHERE is_hot = true AND status = 'active' ORDER BY total_sales DESC")
    List<Product> findHotProducts();
    
    /**
     * 查询新品
     */
    @Select("SELECT * FROM products WHERE is_new = true AND status = 'active' ORDER BY created_at DESC")
    List<Product> findNewProducts();
    
    /**
     * 查询推荐商品
     */
    @Select("SELECT * FROM products WHERE is_recommended = true AND status = 'active' ORDER BY rating DESC")
    List<Product> findRecommendedProducts();
}