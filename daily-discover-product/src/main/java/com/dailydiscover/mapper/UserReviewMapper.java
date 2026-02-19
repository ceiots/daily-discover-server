package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserReviewMapper extends BaseMapper<UserReview> {
    
    /**
     * 根据商品ID查询已审核的评价
     */
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' ORDER BY helpful_count DESC, created_at DESC")
    List<UserReview> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据用户ID查询评价
     */
    @Select("SELECT * FROM user_reviews WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserReview> findByUserId(@Param("userId") Long userId);
    
    /**
     * 删除评价详情
     */
    @Delete("DELETE FROM user_review_details WHERE review_id = #{reviewId}")
    void deleteReviewDetail(@Param("reviewId") Long reviewId);
    
    /**
     * 删除评价统计
     */
    @Delete("DELETE FROM user_review_stats WHERE review_id = #{reviewId}")
    void deleteReviewStats(@Param("reviewId") Long reviewId);
    
    /**
     * 插入评价详情
     */
    @Insert("INSERT INTO user_review_details (review_id, user_avatar, comment, image_urls, video_url, moderation_notes) " +
            "VALUES (#{reviewId}, #{userAvatar}, #{comment}, #{imageUrls}, #{videoUrl}, #{moderationNotes})")
    void insertReviewDetail(UserReviewDetail reviewDetail);
    
    /**
     * 更新评价详情
     */
    @Update("UPDATE user_review_details SET user_avatar = #{userAvatar}, comment = #{comment}, image_urls = #{imageUrls}, video_url = #{videoUrl}, moderation_notes = #{moderationNotes} WHERE review_id = #{reviewId}")
    void updateReviewDetail(UserReviewDetail reviewDetail);
    
    /**
     * 根据评价ID查询评价详情
     */
    @Select("SELECT * FROM user_review_details WHERE review_id = #{reviewId}")
    UserReviewDetail findReviewDetailByReviewId(@Param("reviewId") Long reviewId);
}