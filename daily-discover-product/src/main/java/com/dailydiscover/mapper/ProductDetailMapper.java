package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商品详情表 Mapper
 */
@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetail> {
    
    /**
     * 根据商品ID查询商品详情
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId}")
    ProductDetail findByProductId(@Param("productId") Long productId);
    
    /**
     * 保存商品详情
     */
    @org.apache.ibatis.annotations.Insert("INSERT INTO product_details (product_id, description, specifications, features, media_url, created_at, updated_at) " +
            "VALUES (#{productId}, #{description}, #{specifications}, #{features}, #{mediaUrl}, NOW(), NOW())")
    int saveProductDetail(@Param("productDetail") com.dailydiscover.model.ProductDetail productDetail);
    
    /**
     * 更新商品详情
     */
    @org.apache.ibatis.annotations.Update("UPDATE product_details SET description = #{description}, specifications = #{specifications}, " +
            "features = #{features}, media_url = #{mediaUrl}, updated_at = NOW() WHERE product_id = #{productId}")
    int updateProductDetail(@Param("productDetail") com.dailydiscover.model.ProductDetail productDetail);
    
    /**
     * 删除商品详情
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM product_details WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
    
    /**
     * 获取商品图片
     */
    @Select("SELECT media_url FROM product_details WHERE product_id = #{productId}")
    String getProductImages(@Param("productId") Long productId);
    
    /**
     * 获取商品规格
     */
    @Select("SELECT specifications FROM product_details WHERE product_id = #{productId}")
    String getProductSpecifications(@Param("productId") Long productId);
    
    /**
     * 获取商品特性
     */
    @Select("SELECT features FROM product_details WHERE product_id = #{productId}")
    String getProductFeatures(@Param("productId") Long productId);
}