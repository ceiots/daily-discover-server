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
    public List<ReviewReply> getRepliesByReviewId(Long reviewId) {
        return lambdaQuery().eq(ReviewReply::getReviewId, reviewId).orderByAsc(ReviewReply::getCreatedAt).list();
    }
    
    @Override
    public List<ReviewReply> getRepliesBySellerId(Long sellerId) {
        return lambdaQuery().eq(ReviewReply::getSellerId, sellerId).orderByDesc(ReviewReply::getCreatedAt).list();
    }
    
    @Override
    public ReviewReply createReply(Long reviewId, Long sellerId, String content) {
        ReviewReply reply = new ReviewReply();
        reply.setReviewId(reviewId);
        reply.setSellerId(sellerId);
        reply.setContent(content);
        
        save(reply);
        return reply;
    }
    
    @Override
    public boolean updateReplyContent(Long replyId, String content) {
        ReviewReply reply = getById(replyId);
        if (reply != null) {
            reply.setContent(content);
            return updateById(reply);
        }
        return false;
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