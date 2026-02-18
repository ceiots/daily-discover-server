package com.dailydiscover.service;

import com.dailydiscover.model.PromotionActivity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 促销活动服务接口
 */
public interface PromotionActivityService extends IService<PromotionActivity> {
    
    /**
     * 根据活动名称查询促销活动
     */
    PromotionActivity getByActivityName(String activityName);
    
    /**
     * 查询进行中的促销活动
     */
    java.util.List<PromotionActivity> getActiveActivities();
    
    /**
     * 查询即将开始的促销活动
     */
    java.util.List<PromotionActivity> getUpcomingActivities();
    
    /**
     * 查询已结束的促销活动
     */
    java.util.List<PromotionActivity> getExpiredActivities();
    
    /**
     * 根据活动类型查询促销活动
     */
    java.util.List<PromotionActivity> getByActivityType(String activityType);
    
    /**
     * 创建促销活动
     */
    PromotionActivity createActivity(String activityName, String activityType, java.math.BigDecimal discountAmount,
                                   java.math.BigDecimal minOrderAmount, java.time.LocalDateTime startTime,
                                   java.time.LocalDateTime endTime, String rules);
    
    /**
     * 更新促销活动状态
     */
    boolean updateActivityStatus(Long activityId, String status);
    
    /**
     * 验证促销活动是否可用
     */
    boolean validateActivity(Long activityId, java.math.BigDecimal orderAmount);
    
    /**
     * 获取促销活动统计信息
     */
    java.util.Map<String, Object> getActivityStats();
    
    /**
     * 根据活动类型查询促销活动
     */
    java.util.List<PromotionActivity> findByActivityType(String activityType);
    
    /**
     * 根据状态查询促销活动
     */
    java.util.List<PromotionActivity> findByStatus(String status);
    
    /**
     * 根据商品ID查询促销活动
     */
    java.util.List<PromotionActivity> findByProductId(Long productId);
}