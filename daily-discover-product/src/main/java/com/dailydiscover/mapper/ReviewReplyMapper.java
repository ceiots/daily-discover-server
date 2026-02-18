package com.dailydiscover.mapper;

import com.dailydiscover.model.ReviewReply;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评价回复表 Mapper
 */
@Mapper
public interface ReviewReplyMapper extends BaseMapper<ReviewReply> {
    
    /**
     * 根据评价ID查询回复列表
     */
    @Select("SELECT * FROM review_replies WHERE review_id = #{reviewId} AND status = 'active' ORDER BY created_at ASC")
    List<ReviewReply> findByReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * 查询商家回复
     */
    @Select("SELECT * FROM review_replies WHERE review_id = #{reviewId} AND is_seller_reply = true AND status = 'active' ORDER BY created_at ASC")
    List<ReviewReply> findSellerRepliesByReviewId(@Param("reviewId") Long reviewId);
    
    /**
     * 根据用户ID查询回复
     */
    @Select("SELECT * FROM review_replies WHERE user_id = #{userId} AND status = 'active' ORDER BY created_at DESC")
    List<ReviewReply> findByUserId(@Param("userId") Long userId);
}