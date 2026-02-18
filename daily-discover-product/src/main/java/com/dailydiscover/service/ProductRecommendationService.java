package com.dailydiscover.service;

import com.dailydiscover.model.ProductRecommendation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品推荐服务接口
 */
public interface ProductRecommendationService extends IService<ProductRecommendation> {
    
    /**
     * 根据商品ID获取推荐商品
     * @param productId 商品ID
     * @return 推荐商品列表
     */
    java.util.List<ProductRecommendation> getRecommendationsByProductId(Long productId);
    
    /**
     * 根据用户ID获取个性化推荐
     * @param userId 用户ID
     * @return 个性化推荐列表
     */
    java.util.List<ProductRecommendation> getPersonalizedRecommendations(Long userId);
    
    /**
     * 根据推荐类型获取推荐商品
     * @param recommendationType 推荐类型
     * @return 推荐商品列表
     */
    java.util.List<ProductRecommendation> getRecommendationsByType(String recommendationType);
}