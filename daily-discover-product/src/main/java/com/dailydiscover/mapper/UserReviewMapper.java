package com.dailydiscover.mapper;

import com.dailydiscover.model.ReviewLike;
import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserReviewMapper {
    // 基础CRUD操作
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
    
    @Delete("DELETE FROM user_reviews WHERE id = #{id}")
    void delete(Long id);
    
    // 评价统计相关
    @Select("SELECT * FROM review_stats WHERE product_id = #{productId}")
    ReviewStats getProductReviewStats(Long productId);
    
    @Update("UPDATE user_reviews SET helpful_count = helpful_count + 1 WHERE id = #{id}")
    void incrementHelpfulCount(Long id);
    
    @Update("UPDATE user_reviews SET like_count = like_count + 1 WHERE id = #{id}")
    void incrementLikeCount(Long id);
    
    @Update("UPDATE user_reviews SET reply_count = reply_count + 1 WHERE id = #{id}")
    void incrementReplyCount(Long id);
    
    // 点赞相关
    @Insert("INSERT INTO review_likes (review_id, user_id) VALUES (#{reviewId}, #{userId})")
    void insertReviewLike(ReviewLike like);
    
    @Delete("DELETE FROM review_likes WHERE review_id = #{reviewId} AND user_id = #{userId}")
    void deleteReviewLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM review_likes WHERE review_id = #{reviewId} AND user_id = #{userId}")
    int countReviewLike(@Param("reviewId") Long reviewId, @Param("userId") Long userId);
    
    // 回复相关
    @Insert("INSERT INTO review_replies (review_id, user_id, parent_reply_id, content, is_seller_reply) " +
            "VALUES (#{reviewId}, #{userId}, #{parentReplyId}, #{content}, #{isSellerReply})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertReviewReply(ReviewReply reply);
    
    @Select("SELECT * FROM review_replies WHERE review_id = #{reviewId} AND status = 'active' ORDER BY created_at ASC")
    List<ReviewReply> findRepliesByReviewId(Long reviewId);
    
    // 查询相关
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' ORDER BY helpful_count DESC, like_count DESC LIMIT #{limit}")
    List<UserReview> findTopReviewsByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' ORDER BY created_at DESC LIMIT #{limit}")
    List<UserReview> findRecentReviewsByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' AND image_urls IS NOT NULL AND JSON_LENGTH(image_urls) > 0 ORDER BY created_at DESC")
    List<UserReview> findReviewsWithImagesByProductId(Long productId);
}