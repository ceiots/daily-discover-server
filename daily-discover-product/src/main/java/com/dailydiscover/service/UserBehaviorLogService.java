package com.dailydiscover.service;

import com.dailydiscover.model.UserBehaviorLog;
import com.dailydiscover.model.UserBehaviorLogCore;
import com.dailydiscover.model.UserBehaviorLogDetails;
import com.dailydiscover.dto.ProductViewCountDTO;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户行为日志服务接口（支持双表操作）
 */
public interface UserBehaviorLogService extends IService<UserBehaviorLogCore> {
    
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
     * @return 商品视图统计DTO列表
     */
    List<ProductViewCountDTO> getPopularProducts(int limit);
    
    /**
     * 根据用户ID查询行为日志
     */
    java.util.List<UserBehaviorLog> getByUserId(Long userId);
    
    /**
     * 根据行为类型查询行为日志
     */
    java.util.List<UserBehaviorLog> getByBehaviorType(String behaviorType);
    
    /**
     * 根据时间范围查询行为日志
     */
    java.util.List<UserBehaviorLog> getByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    
    /**
     * 记录用户行为
     */
    UserBehaviorLog recordBehavior(Long userId, String behaviorType, Long targetId, 
                                 String targetType, String details);
    
    /**
     * 获取用户行为统计
     */
    java.util.Map<String, Object> getUserBehaviorStats(Long userId);
    
    /**
     * 获取热门商品行为统计
     */
    java.util.List<java.util.Map<String, Object>> getHotProductBehaviorStats(Integer limit);
    
    /**
     * 获取行为趋势分析
     */
    java.util.Map<String, Object> getBehaviorTrendAnalysis(String behaviorType, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    
    /**
     * 记录用户行为（双表操作）
     * @param userId 用户ID
     * @param productId 商品ID
     * @param behaviorType 行为类型
     * @param sessionId 会话ID
     * @param referrerUrl 来源页面
     * @param behaviorContext 行为上下文
     * @return 是否成功
     */
    boolean recordUserBehaviorWithDetails(Long userId, Long productId, String behaviorType, 
                                         String sessionId, String referrerUrl, String behaviorContext);
    
    /**
     * 根据用户ID查询完整行为记录（包含详情）
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 完整行为记录列表
     */
    java.util.List<UserBehaviorLog> getCompleteUserBehaviorHistory(Long userId, int limit);
    
    /**
     * 根据商品ID查询完整行为记录（包含详情）
     * @param productId 商品ID
     * @param limit 限制数量
     * @return 完整行为记录列表
     */
    java.util.List<UserBehaviorLog> getCompleteProductBehaviorHistory(Long productId, int limit);
    
    /**
     * 根据行为ID获取行为详情
     * @param behaviorId 行为ID
     * @return 行为详情
     */
    UserBehaviorLogDetails getBehaviorDetails(Long behaviorId);
}