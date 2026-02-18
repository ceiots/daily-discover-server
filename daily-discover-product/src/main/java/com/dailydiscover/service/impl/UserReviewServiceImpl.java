package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.UserReviewMapper;

import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.model.UserReviewStats;
import com.dailydiscover.service.UserReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserReviewServiceImpl implements UserReviewService {
    
    @Autowired
    private UserReviewMapper userReviewMapper;
    
    @Override
    public UserReview findById(Long id) {
        return userReviewMapper.findById(id);
    }
    
    @Override
    public UserReviewDetail findReviewDetailById(Long reviewId) {
        return userReviewMapper.findReviewDetailByReviewId(reviewId);
    }
    
    @Override
    public List<UserReview> findByProductId(Long productId) {
        return userReviewMapper.findByProductId(productId);
    }
    
    @Override
    public List<UserReview> findByUserId(Long userId) {
        return userReviewMapper.findByUserId(userId);
    }
    
    @Override
    public UserReviewDetail findReviewDetailById(Long reviewId) {
        return userReviewMapper.findReviewDetailByReviewId(reviewId);
    }
    
    @Override
    @Transactional
    public void save(UserReview userReview, UserReviewDetail reviewDetail) {
        try {
            // 保存评价基本信息
            userReviewMapper.insert(userReview);
            
            // 保存评价详情
            if (reviewDetail != null) {
                reviewDetail.setReviewId(userReview.getId());
                userReviewMapper.insertReviewDetail(reviewDetail);
            }
            
            // 初始化评价统计
            UserReviewStats reviewStats = new UserReviewStats();
            reviewStats.setReviewId(userReview.getId());
            reviewStats.setHelpfulCount(0);
            reviewStats.setReplyCount(0);
            reviewStats.setLikeCount(0);
            userReviewMapper.insertReviewStats(reviewStats);
            
            log.info("评价创建成功: reviewId={}, productId={}, userId={}", userReview.getId(), userReview.getProductId(), userReview.getUserId());
        } catch (Exception e) {
            log.error("保存评价失败: productId={}, userId={}", userReview.getProductId(), userReview.getUserId(), e);
            throw new RuntimeException("保存评价失败", e);
        }
    }
    
    @Override
    @Transactional
    public void update(UserReview userReview, UserReviewDetail reviewDetail) {
        try {
            // 更新评价基本信息
            userReviewMapper.update(userReview);
            
            // 更新评价详情
            if (reviewDetail != null) {
                userReviewMapper.updateReviewDetail(reviewDetail);
            }
            
            log.info("评价更新成功: reviewId={}", userReview.getId());
        } catch (Exception e) {
            log.error("更新评价失败: reviewId={}", userReview.getId(), e);
            throw new RuntimeException("更新评价失败", e);
        }
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        try {
            // 删除评价详情
            userReviewMapper.deleteReviewDetail(id);
            
            // 删除评价统计
            userReviewMapper.deleteReviewStats(id);
            
            // 删除评价基本信息
            userReviewMapper.delete(id);
            
            log.info("评价删除成功: reviewId={}", id);
        } catch (Exception e) {
            log.error("删除评价失败: reviewId={}", id, e);
            throw new RuntimeException("删除评价失败", e);
        }
    }
    
    @Override
    public void saveReviewDetail(UserReviewDetail reviewDetail) {
        try {
            userReviewMapper.insertReviewDetail(reviewDetail);
            log.info("评价详情保存成功: reviewId={}", reviewDetail.getReviewId());
        } catch (Exception e) {
            log.error("保存评价详情失败: reviewId={}", reviewDetail.getReviewId(), e);
            throw new RuntimeException("保存评价详情失败", e);
        }
    }
    
    @Override
    public void updateReviewDetail(UserReviewDetail reviewDetail) {
        try {
            userReviewMapper.updateReviewDetail(reviewDetail);
            log.info("评价详情更新成功: reviewId={}", reviewDetail.getReviewId());
        } catch (Exception e) {
            log.error("更新评价详情失败: reviewId={}", reviewDetail.getReviewId(), e);
            throw new RuntimeException("更新评价详情失败", e);
        }
    }
    
    @Override
    public UserReviewDetail getReviewDetailByReviewId(Long reviewId) {
        return userReviewMapper.findReviewDetailByReviewId(reviewId);
    }
    
    @Override
    public ReviewStats getProductReviewStats(Long productId) {
        return userReviewMapper.getProductReviewStats(productId);
    }
    
    @Override
    public UserReviewStats getReviewStatsByReviewId(Long reviewId) {
        return userReviewMapper.findReviewStatsByReviewId(reviewId);
    }
    
    @Override
    public void updateReviewStats(UserReviewStats reviewStats) {
        try {
            userReviewMapper.updateReviewStats(reviewStats);
            log.info("评价统计更新成功: reviewId={}", reviewStats.getReviewId());
        } catch (Exception e) {
            log.error("更新评价统计失败: reviewId={}", reviewStats.getReviewId(), e);
            throw new RuntimeException("更新评价统计失败", e);
        }
    }
    
    @Override
    public void incrementHelpfulCount(Long id) {
        try {
            userReviewMapper.incrementHelpfulCount(id);
            log.info("评价有用数增加: reviewId={}", id);
        } catch (Exception e) {
            log.error("增加评价有用数失败: reviewId={}", id, e);
            throw new RuntimeException("增加评价有用数失败", e);
        }
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        try {
            userReviewMapper.incrementLikeCount(id);
            log.info("评价点赞数增加: reviewId={}", id);
        } catch (Exception e) {
            log.error("增加评价点赞数失败: reviewId={}", id, e);
            throw new RuntimeException("增加评价点赞数失败", e);
        }
    }
    
    @Override
    public void incrementReplyCount(Long id) {
        try {
            userReviewMapper.incrementReplyCount(id);
            log.info("评价回复数增加: reviewId={}", id);
        } catch (Exception e) {
            log.error("增加评价回复数失败: reviewId={}", id, e);
            throw new RuntimeException("增加评价回复数失败", e);
        }
    }
    
    @Override
    public void approveReview(Long reviewId) {
        try {
            userReviewMapper.approveReview(reviewId);
            log.info("评价审核通过: reviewId={}", reviewId);
        } catch (Exception e) {
            log.error("审核评价失败: reviewId={}", reviewId, e);
            throw new RuntimeException("审核评价失败", e);
        }
    }
    
    @Override
    public void rejectReview(Long reviewId, String reason) {
        try {
            userReviewMapper.rejectReview(reviewId, reason);
            log.info("评价审核拒绝: reviewId={}, reason={}", reviewId, reason);
        } catch (Exception e) {
            log.error("拒绝评价失败: reviewId={}", reviewId, e);
            throw new RuntimeException("拒绝评价失败", e);
        }
    }
    
    @Override
    public void hideReview(Long reviewId) {
        try {
            userReviewMapper.hideReview(reviewId);
            log.info("评价隐藏: reviewId={}", reviewId);
        } catch (Exception e) {
            log.error("隐藏评价失败: reviewId={}", reviewId, e);
            throw new RuntimeException("隐藏评价失败", e);
        }
    }
    
    @Override
    public List<UserReview> findPendingReviews() {
        return userReviewMapper.findPendingReviews();
    }
    
    @Override
    public List<UserReview> findApprovedReviewsByProductId(Long productId) {
        return userReviewMapper.findApprovedReviewsByProductId(productId);
    }
    

    
    @Override
    @Transactional
    public void replyReview(ReviewReply reply) {
        try {
            userReviewMapper.insertReviewReply(reply);
            userReviewMapper.incrementReplyCount(reply.getReviewId());
            log.info("评价回复成功: reviewId={}, userId={}", reply.getReviewId(), reply.getUserId());
        } catch (Exception e) {
            log.error("回复评价失败: reviewId={}, userId={}", reply.getReviewId(), reply.getUserId(), e);
            throw new RuntimeException("回复评价失败", e);
        }
    }
    
    @Override
    public void deleteReply(Long replyId) {
        try {
            userReviewMapper.deleteReviewReply(replyId);
            log.info("删除回复成功: replyId={}", replyId);
        } catch (Exception e) {
            log.error("删除回复失败: replyId={}", replyId, e);
            throw new RuntimeException("删除回复失败", e);
        }
    }
    
    @Override
    public List<ReviewReply> findRepliesByReviewId(Long reviewId) {
        return userReviewMapper.findRepliesByReviewId(reviewId);
    }
    
    @Override
    public List<ReviewReply> findSellerRepliesByReviewId(Long reviewId) {
        return userReviewMapper.findSellerRepliesByReviewId(reviewId);
    }
    
    @Override
    public void likeReply(Long replyId, Long userId) {
        try {
            userReviewMapper.incrementReplyLikeCount(replyId);
            log.info("回复点赞成功: replyId={}, userId={}", replyId, userId);
        } catch (Exception e) {
            log.error("点赞回复失败: replyId={}, userId={}", replyId, userId, e);
            throw new RuntimeException("点赞回复失败", e);
        }
    }
    
    @Override
    public void unlikeReply(Long replyId, Long userId) {
        try {
            userReviewMapper.decrementReplyLikeCount(replyId);
            log.info("取消回复点赞成功: replyId={}, userId={}", replyId, userId);
        } catch (Exception e) {
            log.error("取消回复点赞失败: replyId={}, userId={}", replyId, userId, e);
            throw new RuntimeException("取消回复点赞失败", e);
        }
    }
    
    @Override
    public List<UserReview> findTopReviewsByProductId(Long productId, int limit) {
        return userReviewMapper.findTopReviewsByProductId(productId, limit);
    }
    
    @Override
    public List<UserReview> findRecentReviewsByProductId(Long productId, int limit) {
        return userReviewMapper.findRecentReviewsByProductId(productId, limit);
    }
    
    @Override
    public List<UserReview> findReviewsWithImagesByProductId(Long productId) {
        return userReviewMapper.findReviewsWithImagesByProductId(productId);
    }
    
    @Override
    public List<UserReview> findVerifiedPurchaseReviews(Long productId) {
        return userReviewMapper.findVerifiedPurchaseReviews(productId);
    }
    
    @Override
    public List<UserReview> findAnonymousReviews(Long productId) {
        return userReviewMapper.findAnonymousReviews(productId);
    }
    
    @Override
    public Map<String, Object> getReviewAnalysisByProductId(Long productId) {
        try {
            Map<String, Object> analysis = new HashMap<>();
            
            // 获取评价统计
            ReviewStats stats = userReviewMapper.getProductReviewStats(productId);
            if (stats != null) {
                analysis.put("totalReviews", stats.getTotalReviews());
                analysis.put("averageRating", stats.getAverageRating());
                analysis.put("ratingDistribution", stats.getRatingDistribution());
            }
            
            // 获取带图评价数量
            List<UserReview> reviewsWithImages = userReviewMapper.findReviewsWithImagesByProductId(productId);
            analysis.put("reviewsWithImagesCount", reviewsWithImages.size());
            
            // 获取已验证购买评价数量
            List<UserReview> verifiedReviews = userReviewMapper.findVerifiedPurchaseReviews(productId);
            analysis.put("verifiedPurchaseReviewsCount", verifiedReviews.size());
            
            // 获取高质量评价数量（有用数大于10）
            List<UserReview> highQualityReviews = userReviewMapper.findHighQualityReviews(productId, 10);
            analysis.put("highQualityReviewsCount", highQualityReviews.size());
            
            log.info("评价分析完成: productId={}", productId);
            return analysis;
        } catch (Exception e) {
            log.error("评价分析失败: productId={}", productId, e);
            throw new RuntimeException("评价分析失败", e);
        }
    }
    
    @Override
    public List<UserReview> findHighQualityReviews(Long productId, int minHelpfulCount) {
        return userReviewMapper.findHighQualityReviews(productId, minHelpfulCount);
    }
    
    @Override
    public List<UserReview> findReviewsByRatingRange(Long productId, int minRating, int maxRating) {
        return userReviewMapper.findReviewsByRatingRange(productId, minRating, maxRating);
    }
    
    @Override
    @Transactional
    public void batchApproveReviews(List<Long> reviewIds) {
        try {
            userReviewMapper.batchApproveReviews(reviewIds);
            log.info("批量审核通过成功: reviewIds={}", reviewIds);
        } catch (Exception e) {
            log.error("批量审核通过失败: reviewIds={}", reviewIds, e);
            throw new RuntimeException("批量审核通过失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchRejectReviews(List<Long> reviewIds, String reason) {
        try {
            userReviewMapper.batchRejectReviews(reviewIds, reason);
            log.info("批量审核拒绝成功: reviewIds={}, reason={}", reviewIds, reason);
        } catch (Exception e) {
            log.error("批量审核拒绝失败: reviewIds={}", reviewIds, e);
            throw new RuntimeException("批量审核拒绝失败", e);
        }
    }
    
    @Override
    @Transactional
    public void batchDeleteReviews(List<Long> reviewIds) {
        try {
            userReviewMapper.batchDeleteReviews(reviewIds);
            log.info("批量删除评价成功: reviewIds={}", reviewIds);
        } catch (Exception e) {
            log.error("批量删除评价失败: reviewIds={}", reviewIds, e);
            throw new RuntimeException("批量删除评价失败", e);
        }
    }
}