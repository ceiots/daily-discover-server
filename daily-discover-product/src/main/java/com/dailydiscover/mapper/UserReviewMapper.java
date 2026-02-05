package com.dailydiscover.mapper;

import com.dailydiscover.model.UserReview;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserReviewMapper {
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' ORDER BY helpful_count DESC, created_at DESC")
    List<UserReview> findByProductId(Long productId);
    
    @Select("SELECT * FROM user_reviews WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserReview> findByUserId(Long userId);
    
    @Select("SELECT * FROM user_reviews WHERE id = #{id}")
    UserReview findById(Long id);
    
    @Insert("INSERT INTO user_reviews (product_id, user_id, order_id, rating, title, comment, image_urls, video_url, is_anonymous, is_verified_purchase, review_date, status) " +
            "VALUES (#{productId}, #{userId}, #{orderId}, #{rating}, #{title}, #{comment}, #{imageUrls}, #{videoUrl}, #{isAnonymous}, #{isVerifiedPurchase}, #{reviewDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserReview userReview);
    
    @Update("UPDATE user_reviews SET rating = #{rating}, title = #{title}, comment = #{comment}, image_urls = #{imageUrls}, video_url = #{videoUrl}, is_anonymous = #{isAnonymous}, status = #{status}, moderation_notes = #{moderationNotes}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(UserReview userReview);
    
    @Update("UPDATE user_reviews SET helpful_count = helpful_count + 1 WHERE id = #{id}")
    void incrementHelpfulCount(Long id);
    
    @Update("UPDATE user_reviews SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(Long id);
    
    @Update("UPDATE user_reviews SET reply_count = reply_count + 1 WHERE id = #{id}")
    void incrementReplyCount(Long id);
}