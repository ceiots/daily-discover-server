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
    public UserReviewDetail getByReviewId(Long reviewId) {
        return lambdaQuery().eq(UserReviewDetail::getReviewId, reviewId).one();
    }
    
    @Override
    public boolean updateReviewDetail(Long reviewId, String reviewContent, String reviewImages, 
                                     String pros, String cons, String usageExperience) {
        UserReviewDetail detail = getByReviewId(reviewId);
        if (detail != null) {
            detail.setReviewContent(reviewContent);
            detail.setReviewImages(reviewImages);
            detail.setPros(pros);
            detail.setCons(cons);
            detail.setUsageExperience(usageExperience);
            return updateById(detail);
        }
        return false;
    }
    
    @Override
    public boolean addReviewImages(Long reviewId, String imageUrls) {
        UserReviewDetail detail = getByReviewId(reviewId);
        if (detail != null) {
            String currentImages = detail.getReviewImages();
            if (currentImages == null || currentImages.isEmpty()) {
                detail.setReviewImages(imageUrls);
            } else {
                detail.setReviewImages(currentImages + "," + imageUrls);
            }
            return updateById(detail);
        }
        return false;
    }
    
    @Override
    public List<UserReviewDetail> getReviewsWithImages(Integer limit) {
        return lambdaQuery()
                .isNotNull(UserReviewDetail::getReviewImages)
                .ne(UserReviewDetail::getReviewImages, "")
                .last(limit != null ? "LIMIT " + limit : "")
                .list();
    }
}