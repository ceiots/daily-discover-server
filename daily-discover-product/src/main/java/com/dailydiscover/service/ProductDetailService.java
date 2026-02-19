package com.dailydiscover.service;

import com.dailydiscover.model.ProductDetail;
import java.util.List;

public interface ProductDetailService {
    
    /**
     * 根据商品ID查询商品详情
     */
    ProductDetail findByProductId(Long productId);
    
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
     * 获取商品图片列表
     */
    java.util.List<String> getProductImages(Long productId);
    
    /**
     * 获取商品规格参数
     */
    java.util.List<String> getProductSpecifications(Long productId);
    
    /**
     * 获取商品特性
     */
    java.util.List<String> getProductFeatures(Long productId);
    
    /**
     * 获取完整商品详情
     */
    ProductFullDetailDTO getProductFullDetail(Long productId);
}