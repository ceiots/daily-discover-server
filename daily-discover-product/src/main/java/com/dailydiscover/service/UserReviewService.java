package com.dailydiscover.service;

import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.model.UserReviewStats;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

public interface UserReviewService extends IService<UserReview> {
    
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
    
    // ==================== 新增方法，用于支持UserReviewController ====================
    
    /**
     * 根据商品ID查找最近评价
     */
    List<UserReview> findRecentReviewsByProductId(Long productId, int limit);
    
    /**
     * 根据商品ID查找热门评价
     */
    List<UserReview> findTopReviewsByProductId(Long productId, int limit);
    
    /**
     * 根据商品ID查找带图片的评价
     */
    List<UserReview> findReviewsWithImagesByProductId(Long productId);
    
    /**
     * 根据商品ID查找已验证购买的评价
     */
    List<UserReview> findVerifiedPurchaseReviews(Long productId);
    
    /**
     * 根据商品ID查找匿名评价
     */
    List<UserReview> findAnonymousReviews(Long productId);
    
    /**
     * 获取商品评价统计
     */
    UserReviewStats getProductReviewStats(Long productId);
    
    /**
     * 根据评价ID获取评价统计
     */
    UserReviewStats getReviewStatsByReviewId(Long reviewId);
    
    /**
     * 根据商品ID获取评价分析
     */
    Map<String, Object> getReviewAnalysisByProductId(Long productId);
    
    /**
     * 查找待审核的评价
     */
    List<UserReview> findPendingReviews();
    
    /**
     * 批准评价
     */
    boolean approveReview(Long reviewId);
    
    /**
     * 拒绝评价
     */
    boolean rejectReview(Long reviewId, String reason);
    
    /**
     * 隐藏评价
     */
    boolean hideReview(Long reviewId);
    
    /**
     * 批量批准评价
     */
    boolean batchApproveReviews(List<Long> reviewIds);
    
    /**
     * 批量拒绝评价
     */
    boolean batchRejectReviews(List<Long> reviewIds, String reason);
    
    /**
     * 回复评价
     */
    boolean replyReview(ReviewReply reply);
    
    /**
     * 根据评价ID查找回复
     */
    List<ReviewReply> findRepliesByReviewId(Long reviewId);
    
    /**
     * 根据评价ID查找商家回复
     */
    List<ReviewReply> findSellerRepliesByReviewId(Long reviewId);
    
    /**
     * 删除回复
     */
    boolean deleteReply(Long replyId);
}