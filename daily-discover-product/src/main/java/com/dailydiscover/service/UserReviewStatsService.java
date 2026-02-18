package com.dailydiscover.service;

import com.dailydiscover.model.UserReviewStats;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户评论统计服务接口
 */
public interface UserReviewStatsService extends IService<UserReviewStats> {
    
    /**
     * 根据用户ID查询评论统计
     */
    UserReviewStats getByUserId(Long userId);
    
    /**
     * 更新用户评论统计
     */
    boolean updateUserReviewStats(Long userId, Integer totalReviews, Integer helpfulReviews, 
                                 Integer unhelpfulReviews, Double averageRating);
    
    /**
     * 增加用户评论统计
     */
    boolean incrementUserReviewStats(Long userId, Integer rating, Boolean isHelpful);
    
    /**
     * 获取活跃评论用户列表
     */
    java.util.List<UserReviewStats> getActiveReviewers(Integer limit);
    
    /**
     * 获取高质量评论用户列表
     */
    java.util.List<UserReviewStats> getHighQualityReviewers(Double minRating, Integer minHelpfulReviews, Integer limit);
}