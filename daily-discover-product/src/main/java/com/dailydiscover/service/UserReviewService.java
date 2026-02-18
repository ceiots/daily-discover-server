package com.dailydiscover.service;

import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import java.util.List;

public interface UserReviewService {
    
    /**
     * 根据商品ID查询已审核的评价
     */
    List<UserReview> findByProductId(Long productId);
    
    /**
     * 根据用户ID查询评价
     */
    List<UserReview> findByUserId(Long userId);
    
    /**
     * 删除评价详情
     */
    void deleteReviewDetail(Long reviewId);
    
    /**
     * 删除评价统计
     */
    void deleteReviewStats(Long reviewId);
    
    /**
     * 插入评价详情
     */
    void insertReviewDetail(UserReviewDetail reviewDetail);
    
    /**
     * 更新评价详情
     */
    void updateReviewDetail(UserReviewDetail reviewDetail);
    
    /**
     * 根据评价ID查询评价详情
     */
    UserReviewDetail findReviewDetailByReviewId(Long reviewId);
}