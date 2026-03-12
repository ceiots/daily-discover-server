package com.dailydiscover.recommendation.service;

import com.dailydiscover.recommendation.model.ProductRecommendation;
import com.dailydiscover.recommendation.dto.RelatedProductDTO;
import com.dailydiscover.recommendation.dto.DailyDiscoveryResponseDTO;
import com.dailydiscover.recommendation.dto.LifeScenarioResponseDTO;
import com.dailydiscover.recommendation.dto.CommunityHotListResponseDTO;
import com.dailydiscover.recommendation.dto.PersonalizedDiscoveryResponseDTO;
import com.dailydiscover.recommendation.dto.GuidedOptionDTO;
import com.dailydiscover.recommendation.dto.GuidedProductDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

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
     * 根据推荐类型获取推荐商品
     * @param recommendationType 推荐类型
     * @return 推荐商品列表
     */
    List<ProductRecommendation> getRecommendationsByType(String recommendationType);
    
    /**
     * 获取通用推荐商品
     * @param limit 限制数量
     * @return 推荐商品列表
     */
    List<ProductRecommendation> getGeneralRecommendations(int limit);
    
    /**
     * 获取搭配商品推荐
     * @param productId 商品ID
     * @return 搭配商品推荐列表
     */
    List<ProductRecommendation> getComplementaryRecommendations(Long productId);
    
    /**
     * 商品详情页推荐（前端统一调用此接口）
     * @param productId 商品ID
     * @param currentPrice 当前价格（可选）
     * @param limit 限制数量
     * @return 推荐商品DTO列表
     */
    List<RelatedProductDTO> getProductDetailRecommendations(Long productId, Double currentPrice, Integer limit);
    
    // ==================== 首页推荐四模块 ====================
    
    /**
     * 获取今日发现推荐
     * @param userId 用户ID（可选）
     * @param limit 限制数量（可选）
     * @param page 页码（可选）
     * @return 今日发现推荐列表
     */
    List<DailyDiscoveryResponseDTO> getDailyDiscoveryRecommendations(Long userId, Integer limit, Integer page);
    
    /**
     * 获取生活场景推荐
     * @param userId 用户ID（可选）
     * @param dateTime 时间参数（可选）
     * @return 生活场景推荐列表
     */
    List<LifeScenarioResponseDTO> getLifeScenarioRecommendations(Long userId, String dateTime);
    
    /**
     * 获取社区热榜推荐
     * @return 社区热榜推荐列表
     */
    List<CommunityHotListResponseDTO> getCommunityHotList();
    
    /**
     * 获取个性化发现流推荐
     * @param userId 用户ID
     * @return 个性化发现流推荐列表
     */
    List<PersonalizedDiscoveryResponseDTO> getPersonalizedDiscoveryStream(Long userId);
    
    // ==================== 引导推荐接口 ====================
    
    /**
     * 获取引导推荐选项
     * @return 引导推荐选项列表
     */
    List<GuidedOptionDTO> getGuidedOptions();
    
    /**
     * 获取引导推荐商品
     * @param sessionId 会话ID
     * @param intentLabel 意图标签
     * @param limit 限制数量
     * @param userId 用户ID（可选）
     * @return 引导推荐商品列表
     */
    List<GuidedProductDTO> getGuidedProducts(String sessionId, String intentLabel, Integer limit, Long userId);
    
    /**
     * 生成下一级推荐选项
     * @param products 当前商品列表
     * @param intentLabel 当前意图标签
     * @param round 当前轮次
     * @return 下一级推荐选项列表
     */
    List<GuidedOptionDTO> generateNextOptions(List<GuidedProductDTO> products, String intentLabel, Integer round);
}