package com.example.service;

import java.util.Map;

/**
 * 推荐服务接口
 * 负责处理商品推荐相关的业务逻辑
 */
public interface RecommendationService {
    
    /**
     * 获取个性化商品推荐
     * @param userId 用户ID，可能为null（未登录用户）
     * @return 包含推荐商品列表和AI洞察的数据
     */
    Map<String, Object> getPersonalizedRecommendations(Long userId);
} 