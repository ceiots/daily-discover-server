package com.dailydiscover.service;

import com.dailydiscover.model.RecommendationEffect;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 推荐效果追踪服务接口
 */
public interface RecommendationEffectService extends IService<RecommendationEffect> {
    
    /**
     * 根据推荐ID查询效果数据
     * @param recommendationId 推荐ID
     * @return 效果数据列表
     */
    java.util.List<RecommendationEffect> getEffectsByRecommendationId(Long recommendationId);
    
    /**
     * 根据用户ID查询推荐效果
     * @param userId 用户ID
     * @return 推荐效果列表
     */
    java.util.List<RecommendationEffect> getEffectsByUserId(Long userId);
    
    /**
     * 记录推荐展示
     * @param recommendationId 推荐ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean recordImpression(Long recommendationId, Long userId);
    
    /**
     * 记录推荐点击
     * @param recommendationId 推荐ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean recordClick(Long recommendationId, Long userId);
    
    /**
     * 记录推荐转化
     * @param recommendationId 推荐ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean recordConversion(Long recommendationId, Long userId);
}