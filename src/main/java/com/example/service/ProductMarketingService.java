package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.model.ProductMarketing;

import java.util.List;

public interface ProductMarketingService {

    /**
     * 初始化商品营销信息
     */
    @Transactional
    ProductMarketing initialize(Long productId);
    
    /**
     * 获取商品营销信息
     */
    ProductMarketing getByProductId(Long productId);
    
    /**
     * 更新商品营销信息
     */
    @Transactional
    boolean update(ProductMarketing marketing);
    
    /**
     * 设置商品为热门
     */
    @Transactional
    boolean setHot(Long productId, boolean isHot);
    
    /**
     * 设置商品为新品
     */
    @Transactional
    boolean setNew(Long productId, boolean isNew);
    
    /**
     * 设置商品为推荐
     */
    @Transactional
    boolean setRecommended(Long productId, boolean isRecommended);
    
    /**
     * 设置商品在首页展示
     */
    @Transactional
    boolean setShowInHomepage(Long productId, boolean showInHomepage);
    
    /**
     * 更新商品标签
     */
    @Transactional
    boolean updateLabels(Long productId, List<String> labels);
    
    /**
     * 更新相关商品列表
     */
    @Transactional
    boolean updateRelatedProducts(Long productId, List<Long> relatedProductIds);
    
    /**
     * 获取热门商品列表
     */
    List<ProductMarketing> getHotProducts(int limit);
    
    /**
     * 获取新品列表
     */
    List<ProductMarketing> getNewProducts(int limit);
    
    /**
     * 获取推荐商品列表
     */
    List<ProductMarketing> getRecommendedProducts(int limit);
    
    /**
     * 获取首页展示商品列表
     */
    List<ProductMarketing> getHomepageProducts(int limit);
} 