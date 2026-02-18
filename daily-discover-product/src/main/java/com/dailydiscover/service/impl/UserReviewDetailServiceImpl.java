package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.UserReviewDetailMapper;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.service.UserReviewDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserReviewDetailServiceImpl extends ServiceImpl<UserReviewDetailMapper, UserReviewDetail> implements UserReviewDetailService {
    
    @Autowired
    private UserReviewDetailMapper userReviewDetailMapper;
    
    @Override
    public List<UserReviewDetail> getByUserId(Long userId) {
        return lambdaQuery().eq(UserReviewDetail::getUserId, userId).orderByDesc(UserReviewDetail::getCreatedAt).list();
    }
    
    @Override
    public List<UserReviewDetail> getByProductId(Long productId) {
        return lambdaQuery().eq(UserReviewDetail::getProductId, productId).orderByDesc(UserReviewDetail::getCreatedAt).list();
    }
    
    @Override
    public UserReviewDetail getByUserIdAndProductId(Long userId, Long productId) {
        return lambdaQuery()
                .eq(UserReviewDetail::getUserId, userId)
                .eq(UserReviewDetail::getProductId, productId)
                .one();
    }
    
    @Override
    public UserReviewDetail createReviewDetail(Long userId, Long productId, Integer rating, String reviewText, String reviewImages) {
        UserReviewDetail detail = new UserReviewDetail();
        detail.setUserId(userId);
        detail.setProductId(productId);
        detail.setRating(rating);
        detail.setReviewText(reviewText);
        detail.setReviewImages(reviewImages);
        
        save(detail);
        return detail;
    }
    
    @Override
    public boolean updateReviewDetail(Long reviewId, String reviewText, String reviewImages) {
        UserReviewDetail detail = getById(reviewId);
        if (detail != null) {
            detail.setReviewText(reviewText);
            detail.setReviewImages(reviewImages);
            return updateById(detail);
        }
        return false;
    }
    
    @Override
    public boolean deleteReviewDetail(Long reviewId) {
        return removeById(reviewId);
    }
    
    @Override
    public List<UserReviewDetail> getReviewsWithImages() {
        return lambdaQuery()
                .isNotNull(UserReviewDetail::getReviewImages)
                .ne(UserReviewDetail::getReviewImages, "")
                .list();
    }
}