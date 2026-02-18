package com.dailydiscover.mapper;


import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.model.ReviewStats;
import com.dailydiscover.model.UserReview;
import com.dailydiscover.model.UserReviewDetail;
import com.dailydiscover.model.UserReviewStats;
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
    
    @Insert("INSERT INTO user_reviews (product_id, user_id, order_id, rating, title, is_anonymous, is_verified_purchase, review_date, status) " +
            "VALUES (#{productId}, #{userId}, #{orderId}, #{rating}, #{title}, #{isAnonymous}, #{isVerifiedPurchase}, #{reviewDate}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserReview userReview);
    
    @Update("UPDATE user_reviews SET rating = #{rating}, title = #{title}, is_anonymous = #{isAnonymous}, status = #{status}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(UserReview userReview);
    
    @Delete("DELETE FROM user_reviews WHERE id = #{id}")
    void delete(Long id);
    
    @Delete("DELETE FROM user_review_details WHERE review_id = #{reviewId}")
    void deleteReviewDetail(Long reviewId);
    
    @Delete("DELETE FROM user_review_stats WHERE review_id = #{reviewId}")
    void deleteReviewStats(Long reviewId);
    
    // 评价详情管理
    @Insert("INSERT INTO user_review_details (review_id, user_avatar, comment, image_urls, video_url, moderation_notes) " +
            "VALUES (#{reviewId}, #{userAvatar}, #{comment}, #{imageUrls}, #{videoUrl}, #{moderationNotes})")
    void insertReviewDetail(UserReviewDetail reviewDetail);
    
    @Update("UPDATE user_review_details SET user_avatar = #{userAvatar}, comment = #{comment}, image_urls = #{imageUrls}, video_url = #{videoUrl}, moderation_notes = #{moderationNotes} WHERE review_id = #{reviewId}")
    void updateReviewDetail(UserReviewDetail reviewDetail);
    
    @Select("SELECT * FROM user_review_details WHERE review_id = #{reviewId}")
    UserReviewDetail findReviewDetailByReviewId(Long reviewId);
    
    // 评价统计相关
    @Select("SELECT * FROM review_stats WHERE product_id = #{productId}")
    ReviewStats getProductReviewStats(Long productId);
    
    @Select("SELECT * FROM user_review_stats WHERE review_id = #{reviewId}")
    UserReviewStats findReviewStatsByReviewId(Long reviewId);
    
    @Insert("INSERT INTO user_review_stats (review_id, helpful_count, reply_count, like_count) " +
            "VALUES (#{reviewId}, #{helpfulCount}, #{replyCount}, #{likeCount})")
    void insertReviewStats(UserReviewStats reviewStats);
    
    @Update("UPDATE user_review_stats SET helpful_count = #{helpfulCount}, reply_count = #{replyCount}, like_count = #{likeCount}, last_updated_at = CURRENT_TIMESTAMP WHERE review_id = #{reviewId}")
    void updateReviewStats(UserReviewStats reviewStats);
    
    @Update("UPDATE user_review_stats SET helpful_count = helpful_count + 1, last_updated_at = CURRENT_TIMESTAMP WHERE review_id = #{reviewId}")
    void incrementHelpfulCount(Long reviewId);
    
    @Update("UPDATE user_review_stats SET like_count = like_count + 1, last_updated_at = CURRENT_TIMESTAMP WHERE review_id = #{reviewId}")
    void incrementLikeCount(Long reviewId);
    
    @Update("UPDATE user_review_stats SET reply_count = reply_count + 1, last_updated_at = CURRENT_TIMESTAMP WHERE review_id = #{reviewId}")
    void incrementReplyCount(Long reviewId);
    
    // 评价审核管理
    @Update("UPDATE user_reviews SET status = 'approved', updated_at = CURRENT_TIMESTAMP WHERE id = #{reviewId}")
    void approveReview(Long reviewId);
    
    @Update("UPDATE user_reviews SET status = 'rejected', moderation_notes = #{reason}, updated_at = CURRENT_TIMESTAMP WHERE id = #{reviewId}")
    void rejectReview(@Param("reviewId") Long reviewId, @Param("reason") String reason);
    
    @Update("UPDATE user_reviews SET status = 'hidden', updated_at = CURRENT_TIMESTAMP WHERE id = #{reviewId}")
    void hideReview(Long reviewId);
    
    @Select("SELECT * FROM user_reviews WHERE status = 'pending' ORDER BY created_at DESC")
    List<UserReview> findPendingReviews();
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' ORDER BY created_at DESC")
    List<UserReview> findApprovedReviewsByProductId(Long productId);
    

    
    // 回复相关
    @Insert("INSERT INTO review_replies (review_id, user_id, parent_reply_id, content, is_seller_reply) " +
            "VALUES (#{reviewId}, #{userId}, #{parentReplyId}, #{content}, #{isSellerReply})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertReviewReply(ReviewReply reply);
    
    @Delete("DELETE FROM review_replies WHERE id = #{replyId}")
    void deleteReviewReply(Long replyId);
    
    @Select("SELECT * FROM review_replies WHERE review_id = #{reviewId} AND status = 'active' ORDER BY created_at ASC")
    List<ReviewReply> findRepliesByReviewId(Long reviewId);
    
    @Select("SELECT * FROM review_replies WHERE review_id = #{reviewId} AND is_seller_reply = true AND status = 'active' ORDER BY created_at ASC")
    List<ReviewReply> findSellerRepliesByReviewId(Long reviewId);
    
    @Update("UPDATE review_replies SET like_count = like_count + 1 WHERE id = #{replyId}")
    void incrementReplyLikeCount(Long replyId);
    
    @Update("UPDATE review_replies SET like_count = like_count - 1 WHERE id = #{replyId}")
    void decrementReplyLikeCount(Long replyId);
    
    // 查询相关
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' ORDER BY helpful_count DESC, like_count DESC LIMIT #{limit}")
    List<UserReview> findTopReviewsByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' ORDER BY created_at DESC LIMIT #{limit}")
    List<UserReview> findRecentReviewsByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' AND image_urls IS NOT NULL AND JSON_LENGTH(image_urls) > 0 ORDER BY created_at DESC")
    List<UserReview> findReviewsWithImagesByProductId(Long productId);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND is_verified_purchase = true AND status = 'approved' ORDER BY created_at DESC")
    List<UserReview> findVerifiedPurchaseReviews(Long productId);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND is_anonymous = true AND status = 'approved' ORDER BY created_at DESC")
    List<UserReview> findAnonymousReviews(Long productId);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' AND helpful_count >= #{minHelpfulCount} ORDER BY helpful_count DESC")
    List<UserReview> findHighQualityReviews(@Param("productId") Long productId, @Param("minHelpfulCount") int minHelpfulCount);
    
    @Select("SELECT * FROM user_reviews WHERE product_id = #{productId} AND status = 'approved' AND rating BETWEEN #{minRating} AND #{maxRating} ORDER BY rating DESC, created_at DESC")
    List<UserReview> findReviewsByRatingRange(@Param("productId") Long productId, @Param("minRating") int minRating, @Param("maxRating") int maxRating);
    
    // 批量操作
    @Update({"<script>",
            "UPDATE user_reviews SET status = 'approved', updated_at = CURRENT_TIMESTAMP WHERE id IN",
            "<foreach collection='reviewIds' item='reviewId' open='(' separator=',' close=')'>",
            "#{reviewId}",
            "</foreach>",
            "</script>"})
    void batchApproveReviews(@Param("reviewIds") List<Long> reviewIds);
    
    @Update({"<script>",
            "UPDATE user_reviews SET status = 'rejected', moderation_notes = #{reason}, updated_at = CURRENT_TIMESTAMP WHERE id IN",
            "<foreach collection='reviewIds' item='reviewId' open='(' separator=',' close=')'>",
            "#{reviewId}",
            "</foreach>",
            "</script>"})
    void batchRejectReviews(@Param("reviewIds") List<Long> reviewIds, @Param("reason") String reason);
    
    @Delete({"<script>",
            "DELETE FROM user_reviews WHERE id IN",
            "<foreach collection='reviewIds' item='reviewId' open='(' separator=',' close=')'>",
            "#{reviewId}",
            "</foreach>",
            "</script>"})
    void batchDeleteReviews(@Param("reviewIds") List<Long> reviewIds);
}