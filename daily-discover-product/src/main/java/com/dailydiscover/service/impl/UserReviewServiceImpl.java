package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.UserReviewMapper;
import com.dailydiscover.model.ReviewLike;
import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.service.UserReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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
    public List<UserReview> findByProductId(Long productId) {
        return userReviewMapper.findByProductId(productId);
    }
    
    @Override
    public List<UserReview> findByUserId(Long userId) {
        return userReviewMapper.findByUserId(userId);
    }
    
    @Override
    public void save(UserReview userReview) {
        userReviewMapper.insert(userReview);
    }
    
    @Override
    public void update(UserReview userReview) {
        userReviewMapper.update(userReview);
    }
    
    @Override
    public void delete(Long id) {
        userReviewMapper.delete(id);
    }
    
    @Override
    public ReviewStats getProductReviewStats(Long productId) {
        return userReviewMapper.getProductReviewStats(productId);
    }
    
    @Override
    public void incrementHelpfulCount(Long id) {
        userReviewMapper.incrementHelpfulCount(id);
    }
    
    @Override
    public void incrementLikeCount(Long id) {
        userReviewMapper.incrementLikeCount(id);
    }
    
    @Override
    public void incrementReplyCount(Long id) {
        userReviewMapper.incrementReplyCount(id);
    }
    
    @Override
    @Transactional
    public void likeReview(ReviewLike like) {
        // 检查是否已经点赞
        int count = userReviewMapper.countReviewLike(like.getReviewId(), like.getUserId());
        if (count == 0) {
            userReviewMapper.insertReviewLike(like);
            userReviewMapper.incrementLikeCount(like.getReviewId());
            log.info("用户 {} 点赞评价 {}", like.getUserId(), like.getReviewId());
        } else {
            log.warn("用户 {} 已经点赞过评价 {}", like.getUserId(), like.getReviewId());
        }
    }
    
    @Override
    @Transactional
    public void unlikeReview(Long reviewId, Long userId) {
        userReviewMapper.deleteReviewLike(reviewId, userId);
        log.info("用户 {} 取消点赞评价 {}", userId, reviewId);
    }
    
    @Override
    @Transactional
    public void replyReview(ReviewReply reply) {
        userReviewMapper.insertReviewReply(reply);
        userReviewMapper.incrementReplyCount(reply.getReviewId());
        log.info("用户 {} 回复评价 {}", reply.getUserId(), reply.getReviewId());
    }
    
    @Override
    public List<ReviewReply> findRepliesByReviewId(Long reviewId) {
        return userReviewMapper.findRepliesByReviewId(reviewId);
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
}