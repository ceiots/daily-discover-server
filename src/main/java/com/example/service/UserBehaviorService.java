package com.example.service;

import java.util.List;
import java.util.Date;

import com.example.model.UserBehavior;

/**
 * 用户行为服务接口
 */
public interface UserBehaviorService {
    
    /**
     * 记录用户行为
     * @param behavior 用户行为记录
     */
    void recordBehavior(UserBehavior behavior);
    
    /**
     * 记录用户浏览行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param categoryId 类别ID
     */
    void recordViewBehavior(Long userId, Long productId, Long categoryId);
    
    /**
     * 记录用户点击行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param categoryId 类别ID
     * @param extraData 额外数据，如点击位置等
     */
    void recordClickBehavior(Long userId, Long productId, Long categoryId, String extraData);
    
    /**
     * 记录用户停留行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param categoryId 类别ID
     * @param duration 停留时长(秒)
     */
    void recordStayBehavior(Long userId, Long productId, Long categoryId, Double duration);
    
    /**
     * 记录用户购买行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param categoryId 类别ID
     * @param extraData 额外数据，如购买数量、金额等
     */
    void recordBuyBehavior(Long userId, Long productId, Long categoryId, String extraData);
    
    /**
     * 记录用户收藏行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param categoryId 类别ID
     */
    void recordFavoriteBehavior(Long userId, Long productId, Long categoryId);
    
    /**
     * 记录用户评价行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param categoryId 类别ID
     * @param extraData 额外数据，如评价内容、星级等
     */
    void recordCommentBehavior(Long userId, Long productId, Long categoryId, String extraData);
    
    /**
     * 记录用户分享行为
     * @param userId 用户ID
     * @param productId 商品ID
     * @param categoryId 类别ID
     * @param extraData 额外数据，如分享平台等
     */
    void recordShareBehavior(Long userId, Long productId, Long categoryId, String extraData);
    
    /**
     * 获取用户行为历史
     * @param userId 用户ID
     * @param limit 限制条数
     * @return 行为历史列表
     */
    List<UserBehavior> getUserBehaviorHistory(Long userId, int limit);
    
    /**
     * 获取用户特定类型的行为历史
     * @param userId 用户ID
     * @param behaviorType 行为类型
     * @param limit 限制条数
     * @return 行为历史列表
     */
    List<UserBehavior> getUserBehaviorHistoryByType(Long userId, String behaviorType, int limit);
    
    /**
     * 获取用户在特定时间范围内的行为历史
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 行为历史列表
     */
    List<UserBehavior> getUserBehaviorHistoryByTimeRange(Long userId, Date startTime, Date endTime);
    
    /**
     * 更新用户偏好
     * 根据用户行为记录自动更新用户偏好
     * @param userId 用户ID
     */
    void updateUserPreferences(Long userId);
} 