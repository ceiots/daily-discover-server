package com.dailydiscover.service;

import com.dailydiscover.model.ProductRecommendation;
import com.dailydiscover.dto.RelatedProductDTO;
import com.dailydiscover.dto.DailyDiscoveryResponseDTO;
import com.dailydiscover.dto.LifeScenarioResponseDTO;
import com.dailydiscover.dto.CommunityHotListResponseDTO;
import com.dailydiscover.dto.PersonalizedDiscoveryResponseDTO;
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

    
    /**
     * 获取通用推荐
     * @param limit 限制数量
     * @return 通用推荐列表
     */
    List<ProductRecommendation> getGeneralRecommendations(int limit);

    // ==================== 首页推荐四模块 ====================

    /**
     * 获取今日发现推荐
     * @param userId 用户ID
     * @param limit 每页数量
     * @param page 页码（从1开始）
     * @return 今日发现推荐列表
     */
    List<DailyDiscoveryResponseDTO> getDailyDiscoveryRecommendations(Long userId, Integer limit, Integer page);

    /**
     * 获取生活场景推荐（基于时间、日期、季节维度）
     * @param userId 用户ID（可选）
     * @param dateTime 日期时间字符串（ISO格式，可选，默认使用当前时间）
     * @return 生活场景推荐列表（保证返回2条记录）
     */
    List<LifeScenarioResponseDTO> getLifeScenarioRecommendations(Long userId, String dateTime);

    /**
     * 获取社区热榜推荐（客观排名，不关联用户）
     * @return 社区热榜推荐列表
     */
    List<CommunityHotListResponseDTO> getCommunityHotList();

    /**
     * 获取个性化发现流推荐
     */
    List<PersonalizedDiscoveryResponseDTO> getPersonalizedDiscoveryStream(Long userId);
}