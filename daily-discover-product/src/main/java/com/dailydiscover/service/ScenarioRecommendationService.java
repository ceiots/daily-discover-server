package com.dailydiscover.service;

import com.dailydiscover.dto.RecommendationPhraseRequestDTO;
import com.dailydiscover.dto.RecommendationPhraseResponseDTO;
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
    
    /**
     * 根据场景类型获取推荐（Controller中调用的方法）
     */
    java.util.List<ScenarioRecommendation> getByScenarioType(String scenarioType);
    
    /**
     * 根据用户ID获取推荐（Controller中调用的方法）
     */
    java.util.List<ScenarioRecommendation> getByUserId(Long userId);
    
    /**
     * 获取活跃推荐（Controller中调用的方法）
     */
    java.util.List<ScenarioRecommendation> getActiveRecommendations();
    
    /**
     * 生成推荐语
     * @param request 推荐语生成请求
     * @return 生成的推荐语
     */
    RecommendationPhraseResponseDTO generateRecommendationPhrase(
            RecommendationPhraseRequestDTO request);
    
    /**
     * 获取推荐语列表
     * @param scenarioType 场景类型
     * @param approvalStatus 审核状态（JSON路径查询）
     * @return 推荐语列表
     */
    java.util.List<RecommendationPhraseResponseDTO> getRecommendationPhrases(
            String scenarioType, String approvalStatus);
    
    /**
     * 更新推荐语元数据
     * @param id 推荐ID
     * @param metadataJson 元数据JSON
     * @return 是否成功
     */
    boolean updateRecommendationMetadata(Long id, String metadataJson);
    
    /**
     * 记录推荐语使用
     * @param id 推荐ID
     * @return 是否成功
     */
    boolean recordRecommendationUsage(Long id);
    
    /**
     * 根据商品ID获取推荐语（独立接口，不耦合商品逻辑）
     * @param productId 商品ID
     * @param scenarioType 场景类型
     * @return 推荐语信息
     */
    RecommendationPhraseResponseDTO getProductRecommendation(
            Long productId, String scenarioType);
}