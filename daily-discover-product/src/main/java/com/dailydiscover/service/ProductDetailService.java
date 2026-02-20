package com.dailydiscover.service;

import com.dailydiscover.model.ProductDetail;
import java.util.List;

/**
 * 商品详情服务接口（电商媒体管理）
 */
public interface ProductDetailService {
    
    /**
     * 根据商品ID查询商品所有媒体详情
     */
    List<ProductDetail> findByProductId(Long productId);
    
    /**
     * 根据ID查询商品详情
     */
    ProductDetail findById(Long id);
    
    /**
     * 查询所有商品详情
     */
    List<ProductDetail> findAll();
    
    /**
     * 保存商品详情
     */
    boolean save(ProductDetail productDetail);
    
    /**
     * 更新商品详情
     */
    ProductDetail update(ProductDetail productDetail);
    
    /**
     * 删除商品详情
     */
    boolean delete(Long id);
    
    /**
     * 根据商品ID和媒体类型查询媒体详情
     */
    List<ProductDetail> findByProductIdAndMediaType(Long productId, Integer mediaType);
    
    /**
     * 获取商品轮播图（媒体类型=1）
     */
    List<ProductDetail> getProductCarousel(Long productId);
    
    /**
     * 获取商品详情图（媒体类型=2）
     */
    List<ProductDetail> getProductDetailImages(Long productId);
    
    /**
     * 获取商品视频（is_video=1）
     */
    List<ProductDetail> getProductVideos(Long productId);
    
    /**
     * 获取商品图片（is_video=0）
     */
    List<ProductDetail> getProductImages(Long productId);
    
    /**
     * 根据商品ID删除所有媒体详情
     */
    boolean deleteByProductId(Long productId);
    
    /**
     * 根据商品ID和媒体类型删除媒体详情
     */
    boolean deleteByProductIdAndMediaType(Long productId, Integer mediaType);
}