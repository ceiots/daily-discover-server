package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品详情表 Mapper（电商媒体管理）
 */
@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetail> {
    
    /**
     * 根据商品ID查询商品所有媒体详情
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId} ORDER BY media_type, sort_order")
    List<ProductDetail> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据商品ID和媒体类型查询媒体详情
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId} AND media_type = #{mediaType} ORDER BY sort_order")
    List<ProductDetail> findByProductIdAndMediaType(@Param("productId") Long productId, @Param("mediaType") Integer mediaType);
    
    /**
     * 获取商品轮播图（媒体类型=1）
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId} AND media_type = 1 ORDER BY sort_order")
    List<ProductDetail> getProductCarousel(@Param("productId") Long productId);
    
    /**
     * 获取商品详情图（媒体类型=2）
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId} AND media_type = 2 ORDER BY sort_order")
    List<ProductDetail> getProductDetailImages(@Param("productId") Long productId);
    
    /**
     * 获取商品视频（is_video=1）
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId} AND is_video = 1 ORDER BY sort_order")
    List<ProductDetail> getProductVideos(@Param("productId") Long productId);
    
    /**
     * 获取商品图片（is_video=0）
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId} AND is_video = 0 ORDER BY sort_order")
    List<ProductDetail> getProductImages(@Param("productId") Long productId);
    
    /**
     * 根据商品ID删除所有媒体详情
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM product_details WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
    
    /**
     * 根据商品ID和媒体类型删除媒体详情
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM product_details WHERE product_id = #{productId} AND media_type = #{mediaType}")
    int deleteByProductIdAndMediaType(@Param("productId") Long productId, @Param("mediaType") Integer mediaType);
}