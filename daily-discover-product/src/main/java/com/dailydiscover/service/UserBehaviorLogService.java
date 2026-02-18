package com.dailydiscover.service;

import com.dailydiscover.model.UserBehaviorLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户行为日志服务接口
 */
public interface UserBehaviorLogService extends IService<UserBehaviorLog> {
    
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
}