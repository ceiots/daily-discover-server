package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ReviewReplyMapper;
import com.dailydiscover.model.ReviewReply;
import com.dailydiscover.service.ReviewReplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReviewReplyServiceImpl extends ServiceImpl<ReviewReplyMapper, ReviewReply> implements ReviewReplyService {
    
    @Autowired
    private ReviewReplyMapper reviewReplyMapper;
    
    @Override
    public java.util.List<ReviewReply> getByReviewId(Long reviewId) {
        return lambdaQuery().eq(ReviewReply::getReviewId, reviewId).orderByAsc(ReviewReply::getCreatedAt).list();
    }
    
    @Override
    public java.util.List<ReviewReply> getByReplierId(Long replierId) {
        return lambdaQuery().eq(ReviewReply::getUserId, replierId).orderByDesc(ReviewReply::getCreatedAt).list();
    }
    
    @Override
    public ReviewReply addReply(Long reviewId, Long replierId, String replierType, String content) {
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setUserId(replierId); // 使用 userId 字段，对应数据库的 user_id
        // replierType 参数在当前的数据库设计中未使用，保留参数以保持接口兼容性
        reply.setContent(content);
        
        save(reply);
        return reply;
    }
    
    @Override
    public boolean deleteReply(Long replyId) {
        return removeById(replyId);
    }
    
    @Override
    public Integer getReplyCountByReviewId(Long reviewId) {
        return lambdaQuery().eq(ReviewReply::getReviewId, reviewId).count();
    }
    
    @Override
    public ReviewReply getReplyByReviewAndSeller(Long reviewId, Long sellerId) {
        // 根据数据库表结构，商家回复通过 is_seller_reply 字段标识
        // 这里查询指定评论的商家回复（如果有多个商家回复，返回第一个）
        // 注意：sellerId 参数在当前的数据库设计中未使用，保留参数以保持接口兼容性
        return lambdaQuery()
                .eq(ReviewReply::getReviewId, reviewId)
                .eq(ReviewReply::getIsSellerReply, true)
                .orderByAsc(ReviewReply::getCreatedAt)
                .one();
    }
}