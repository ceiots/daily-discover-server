package com.dailydiscover.service;


import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.model.UserReviewStats;
import java.util.List;
import java.util.Map;

public interface UserReviewService {
    
    // 基础CRUD操作
    UserReview findById(Long id);
    UserReviewDetail findReviewDetailById(Long reviewId);
    List<UserReview> findByProductId(Long productId);
    List<UserReview> findByUserId(Long userId);
    void save(UserReview userReview, UserReviewDetail reviewDetail);
    void update(UserReview userReview, UserReviewDetail reviewDetail);
    void delete(Long id);
    
    // 评价详情管理
    void saveReviewDetail(UserReviewDetail reviewDetail);
    void updateReviewDetail(UserReviewDetail reviewDetail);
    UserReviewDetail getReviewDetailByReviewId(Long reviewId);
    
    // 评价统计相关
    ReviewStats getProductReviewStats(Long productId);
    UserReviewStats getReviewStatsByReviewId(Long reviewId);
    void updateReviewStats(UserReviewStats reviewStats);
    void incrementHelpfulCount(Long id);
    void incrementLikeCount(Long id);
    void incrementReplyCount(Long id);
    
    // 评价审核管理
    void approveReview(Long reviewId);
    void rejectReview(Long reviewId, String reason);
    void hideReview(Long reviewId);
    List<UserReview> findPendingReviews();
    List<UserReview> findApprovedReviewsByProductId(Long productId);
    

    
    // 回复相关
    void replyReview(ReviewReply reply);
    void deleteReply(Long replyId);
    List<ReviewReply> findRepliesByReviewId(Long reviewId);
    List<ReviewReply> findSellerRepliesByReviewId(Long reviewId);
    void likeReply(Long replyId, Long userId);
    void unlikeReply(Long replyId, Long userId);
    
    // 查询相关
    List<UserReview> findTopReviewsByProductId(Long productId, int limit);
    List<UserReview> findRecentReviewsByProductId(Long productId, int limit);
    List<UserReview> findReviewsWithImagesByProductId(Long productId);
    List<UserReview> findVerifiedPurchaseReviews(Long productId);
    List<UserReview> findAnonymousReviews(Long productId);
    
    // 评价分析相关
    Map<String, Object> getReviewAnalysisByProductId(Long productId);
    List<UserReview> findHighQualityReviews(Long productId, int minHelpfulCount);
    List<UserReview> findReviewsByRatingRange(Long productId, int minRating, int maxRating);
    
    // 批量操作
    void batchApproveReviews(List<Long> reviewIds);
    void batchRejectReviews(List<Long> reviewIds, String reason);
    void batchDeleteReviews(List<Long> reviewIds);
}