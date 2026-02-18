package com.dailydiscover.mapper;

import com.dailydiscover.model.UserReviewDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户评价详情表 Mapper
 */
@Mapper
public interface UserReviewDetailMapper extends BaseMapper<UserReviewDetail> {
    
    /**
     * 根据评价ID查询评价详情
     */
    @Select("SELECT * FROM user_review_details WHERE review_id = #{reviewId}")
    UserReviewDetail findByReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * 查询包含图片的评价详情
     */
    @Select("SELECT * FROM user_review_details WHERE image_urls IS NOT NULL AND JSON_LENGTH(image_urls) > 0")
    UserReviewDetail findReviewsWithImages();
    
    /**
     * 查询包含视频的评价详情
     */
    @Select("SELECT * FROM user_review_details WHERE video_url IS NOT NULL")
    UserReviewDetail findReviewsWithVideos();
}