package com.dailydiscover.service;

import com.dailydiscover.model.ReviewLike;
import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import java.util.List;

public interface UserReviewService {
    // 基础CRUD操作
    UserReview findById(Long id);
    List<UserReview> findByProductId(Long productId);
    List<UserReview> findByUserId(Long userId);
    void save(UserReview userReview);
    void update(UserReview userReview);
    void delete(Long id);
    
    // 评价统计相关
    ReviewStats getProductReviewStats(Long productId);
    void incrementHelpfulCount(Long id);
    void incrementLikeCount(Long id);
    void incrementReplyCount(Long id);
    
    // 点赞相关
    void likeReview(ReviewLike like);
    void unlikeReview(Long reviewId, Long userId);
    
    // 回复相关
    void replyReview(ReviewReply reply);
    List<ReviewReply> findRepliesByReviewId(Long reviewId);
    
    // 查询相关
    List<UserReview> findTopReviewsByProductId(Long productId, int limit);
    List<UserReview> findRecentReviewsByProductId(Long productId, int limit);
    List<UserReview> findReviewsWithImagesByProductId(Long productId);
}