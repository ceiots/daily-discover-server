package com.dailydiscover.service;

import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.model.dto.RelatedProductDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

/**
 * 商品推荐服务接口
 */
public interface ProductRecommendationService extends IService<ProductRecommendation> {
    
    /**
     * 根据商品ID获取推荐商品
     * @param productId 商品ID
     * @return 推荐商品列表
     */
    List<ProductRecommendation> getRecommendationsByProductId(Long productId);
    
    /**
     * 根据用户ID获取个性化推荐
     * @param userId 用户ID
     * @return 个性化推荐列表
     */
    List<ProductRecommendation> getPersonalizedRecommendations(Long userId);
    
    /**
     * 根据推荐类型获取推荐商品
     * @param recommendationType 推荐类型
     * @return 推荐商品列表
     */
    List<ProductRecommendation> getRecommendationsByType(String recommendationType);
    
    /**
     * 获取相似商品推荐
     * @param productId 商品ID
     * @return 相似商品推荐列表
     */
    List<ProductRecommendation> getSimilarRecommendations(Long productId);
    
    /**
     * 获取搭配商品推荐
     * @param productId 商品ID
     * @return 搭配商品推荐列表
     */
    List<ProductRecommendation> getComplementaryRecommendations(Long productId);
    
    /**
     * 获取商品详情页推荐（统一接口）
     * @param productId 商品ID
     * @param currentPrice 当前商品价格（用于价格敏感推荐）
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    List<RelatedProductDTO> getProductDetailRecommendations(Long productId, Double currentPrice, Integer limit);
    
    /**
     * 获取每日发现推荐
     * @param userId 用户ID
     * @return 每日发现推荐列表
     */
    List<ProductRecommendation> getDailyDiscoverRecommendations(Long userId);
    
    /**
     * 获取通用推荐
     * @param limit 限制数量
     * @return 通用推荐列表
     */
    List<ProductRecommendation> getGeneralRecommendations(int limit);

    // ==================== 首页推荐四模块 ====================

    /**
     * 获取今日发现推荐
     */
    List<Map<String, Object>> getDailyDiscoveryRecommendations(Long userId);

    /**
     * 获取生活场景推荐
     * @param userId 用户ID
     * @param timeContext 时间场景（morning/afternoon/evening）
     * @param locationContext 位置场景
     * @return 生活场景推荐列表
     */
    List<Map<String, Object>> getLifeScenarioRecommendations(Long userId, String timeContext, String locationContext);

    /**
     * 获取社区热榜推荐
     */
    List<Map<String, Object>> getCommunityHotList();

    /**
     * 获取个性化发现流推荐
     */
    List<Map<String, Object>> getPersonalizedDiscoveryStream(Long userId);
}