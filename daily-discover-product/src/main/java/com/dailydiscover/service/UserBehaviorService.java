package com.dailydiscover.service;

import com.dailydiscover.model.UserBehaviorLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户行为服务接口
 */
public interface UserBehaviorService extends IService<UserBehaviorLog> {
    
    /**
     * 记录用户行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param behaviorType 行为类型
     * @param sessionId 会话ID
     * @return 是否成功
     */
    boolean recordUserBehavior(Long userId, Long productId, String behaviorType, String sessionId);
    
    /**
     * 根据用户ID查询行为记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 行为记录列表
     */
    java.util.List<UserBehaviorLog> getUserBehaviorHistory(Long userId, int limit);
    
    /**
     * 根据商品ID查询行为记录
     * @param productId 商品ID
     * @param limit 限制数量
     * @return 行为记录列表
     */
    java.util.List<UserBehaviorLog> getProductBehaviorHistory(Long productId, int limit);
    
    /**
     * 获取用户最近浏览的商品
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 商品ID列表
     */
    java.util.List<Long> getRecentlyViewedProducts(Long userId, int limit);
    
    /**
     * 获取热门商品（基于行为数据）
     * @param limit 限制数量
     * @return 商品ID列表
     */
    java.util.List<Long> getPopularProducts(int limit);
}