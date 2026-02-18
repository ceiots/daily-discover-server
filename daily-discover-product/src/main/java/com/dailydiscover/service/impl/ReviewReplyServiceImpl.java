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
        return lambdaQuery().eq(ReviewReply::getReplierId, replierId).orderByDesc(ReviewReply::getCreatedAt).list();
    }
    
    @Override
    public ReviewReply addReply(Long reviewId, Long replierId, String replierType, String content) {
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setReplierId(replierId);
        reply.setReplierType(replierType);
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
    public boolean deleteReply(Long replyId) {
        return removeById(replyId);
    }
    
    @Override
    public ReviewReply getReplyByReviewAndSeller(Long reviewId, Long sellerId) {
        return lambdaQuery()
                .eq(ReviewReply::getReviewId, reviewId)
                .eq(ReviewReply::getSellerId, sellerId)
                .one();
    }
}