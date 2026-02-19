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
        return reviewReplyMapper.findByReviewId(reviewId);
    }
    
    @Override
    public java.util.List<ReviewReply> getByReplierId(Long replierId) {
        return reviewReplyMapper.findByUserId(replierId);
    }
    
    @Override
    public ReviewReply addReply(Long reviewId, Long replierId, String replierType, String content) {
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setUserId(replierId);
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
        Long count = lambdaQuery().eq(ReviewReply::getReviewId, reviewId).count();
        return count != null ? Math.toIntExact(count) : 0;
    }
    
    @Override
    public ReviewReply getSellerReplyByReviewId(Long reviewId) {
        List<ReviewReply> sellerReplies = reviewReplyMapper.findSellerRepliesByReviewId(reviewId);
        return sellerReplies.isEmpty() ? null : sellerReplies.get(0);
    }
}