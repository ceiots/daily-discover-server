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
        return userReviewDetailMapper.findByReviewId(reviewId);
    }
    
    @Override
    public boolean updateReviewDetail(Long reviewId, String comment, String imageUrls, 
                                     String videoUrl, String moderationNotes) {
        UserReviewDetail detail = getByReviewId(reviewId);
        if (detail != null) {
            detail.setComment(comment);
            detail.setImageUrls(imageUrls);
            detail.setVideoUrl(videoUrl);
            detail.setModerationNotes(moderationNotes);
            return updateById(detail);
        }
        return false;
    }
    
    @Override
    public boolean addReviewImages(Long reviewId, String imageUrls) {
        UserReviewDetail detail = getByReviewId(reviewId);
        if (detail != null) {
            String currentImages = detail.getImageUrls();
            if (currentImages == null || currentImages.isEmpty()) {
                detail.setImageUrls(imageUrls);
            } else {
                detail.setImageUrls(currentImages + "," + imageUrls);
            }
            return updateById(detail);
        }
        return false;
    }
    
    @Override
    public List<UserReviewDetail> getReviewsWithImages(Integer limit) {
        UserReviewDetail detail = userReviewDetailMapper.findReviewsWithImages();
        if (detail != null) {
            return lambdaQuery()
                    .isNotNull(UserReviewDetail::getImageUrls)
                    .ne(UserReviewDetail::getImageUrls, "")
                    .last(limit != null ? "LIMIT " + limit : "")
                    .list();
        }
        return List.of();
    }
}