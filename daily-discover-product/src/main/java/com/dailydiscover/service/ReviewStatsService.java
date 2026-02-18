package com.dailydiscover.service;

import com.dailydiscover.model.ReviewStats;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 评论统计服务接口
 */
public interface ReviewStatsService extends IService<ReviewStats> {
    
    /**
     * 根据商品ID查询评论统计
     */
    ReviewStats getByProductId(Long productId);
    
    /**
     * 更新评论统计
     */
    boolean updateReviewStats(Long productId, Integer totalReviews, Integer positiveReviews, 
                             Integer neutralReviews, Integer negativeReviews, Double averageRating);
    
    /**
     * 增加评论统计
     */
    boolean incrementReviewStats(Long productId, Integer rating);
    
    /**
     * 减少评论统计
     */
    boolean decrementReviewStats(Long productId, Integer rating);
    
    /**
     * 获取高评分商品列表
     */
    java.util.List<ReviewStats> getHighRatedProducts(Double minRating, Integer limit);
    
    /**
     * 获取评论统计排名
     */
    java.util.List<ReviewStats> getReviewStatsRanking(Integer limit);
}