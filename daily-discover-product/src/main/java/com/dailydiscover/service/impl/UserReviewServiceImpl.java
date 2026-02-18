package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.UserReviewMapper;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.service.UserReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserReviewServiceImpl implements UserReviewService {
    
    @Autowired
    private UserReviewMapper userReviewMapper;
    
    @Override
    public List<UserReview> findByProductId(Long productId) {
        return userReviewMapper.findByProductId(productId);
    }
    
    @Override
    public List<UserReview> findByUserId(Long userId) {
        return userReviewMapper.findByUserId(userId);
    }
    
    @Override
    public void deleteReviewDetail(Long reviewId) {
        userReviewMapper.deleteReviewDetail(reviewId);
    }
    
    @Override
    public void deleteReviewStats(Long reviewId) {
        userReviewMapper.deleteReviewStats(reviewId);
    }
    
    @Override
    public void insertReviewDetail(UserReviewDetail reviewDetail) {
        userReviewMapper.insertReviewDetail(reviewDetail);
    }
    
    @Override
    public void updateReviewDetail(UserReviewDetail reviewDetail) {
        userReviewMapper.updateReviewDetail(reviewDetail);
    }
    
    @Override
    public UserReviewDetail findReviewDetailByReviewId(Long reviewId) {
        return userReviewMapper.findReviewDetailByReviewId(reviewId);
    }
}