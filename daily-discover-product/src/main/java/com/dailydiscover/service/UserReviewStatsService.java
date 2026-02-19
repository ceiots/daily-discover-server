package com.dailydiscover.service;

import com.dailydiscover.model.UserReviewStats;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户评论统计服务接口
 */
public interface UserReviewStatsService extends IService<UserReviewStats> {
    
    /**
     * 根据评价ID查询评论统计
     */
    UserReviewStats getByReviewId(Long reviewId);
    
    /**
     * 更新评论统计
     */
    boolean updateReviewStats(Long reviewId, Integer helpfulCount, Integer replyCount, Integer likeCount);
    
    /**
     * 增加有用数量统计
     */
    boolean incrementHelpfulCount(Long reviewId);
    
    /**
     * 增加回复数量统计
     */
    boolean incrementReplyCount(Long reviewId);
    
    /**
     * 增加点赞数量统计
     */
    boolean incrementLikeCount(Long reviewId);
    
    /**
     * 获取高有用数量的评论统计列表
     */
    java.util.List<UserReviewStats> getTopHelpfulReviews(Integer limit);
    
    /**
     * 获取高点赞数量的评论统计列表
     */
    java.util.List<UserReviewStats> getTopLikedReviews(Integer limit);
}