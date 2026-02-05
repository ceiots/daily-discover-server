package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.UserReviewMapper;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.service.UserReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

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
}