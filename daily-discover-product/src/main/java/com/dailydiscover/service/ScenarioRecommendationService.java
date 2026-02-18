package com.dailydiscover.service;

import com.dailydiscover.model.ScenarioRecommendation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 场景推荐服务接口
 */
public interface ScenarioRecommendationService extends IService<ScenarioRecommendation> {
    
    /**
     * 根据场景类型查询推荐商品
     * @param scenarioType 场景类型
     * @return 推荐商品列表
     */
    java.util.List<ScenarioRecommendation> getRecommendationsByScenarioType(String scenarioType);
    
    /**
     * 根据用户ID查询个性化场景推荐
     * @param userId 用户ID
     * @return 个性化推荐列表
     */
    java.util.List<ScenarioRecommendation> getPersonalizedScenarioRecommendations(Long userId);
    
    /**
     * 根据商品ID查询场景推荐
     * @param productId 商品ID
     * @return 场景推荐列表
     */
    java.util.List<ScenarioRecommendation> getRecommendationsByProductId(Long productId);
    
    /**
     * 创建场景推荐
     * @param scenarioType 场景类型
     * @param timeSlot 时间段
     * @param recommendedProducts 推荐商品列表
     * @param scenarioStory 场景故事
     * @return 创建的推荐
     */
    ScenarioRecommendation createScenarioRecommendation(String scenarioType, String timeSlot, 
                                                        java.util.List<Long> recommendedProducts, 
                                                        String scenarioStory);
}