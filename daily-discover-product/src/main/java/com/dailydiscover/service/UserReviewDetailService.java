package com.dailydiscover.service;

import com.dailydiscover.model.UserReviewDetail;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户评论详情服务接口
 */
public interface UserReviewDetailService extends IService<UserReviewDetail> {
    
    /**
     * 根据评论ID查询评论详情
     */
    UserReviewDetail getByReviewId(Long reviewId);
    
    /**
     * 更新评论详情
     */
    boolean updateReviewDetail(Long reviewId, String reviewContent, String reviewImages, 
                              String pros, String cons, String usageExperience);
    
    /**
     * 添加评论图片
     */
    boolean addReviewImages(Long reviewId, String imageUrls);
    
    /**
     * 获取带图片的评论列表
     */
    java.util.List<UserReviewDetail> getReviewsWithImages(Integer limit);
}