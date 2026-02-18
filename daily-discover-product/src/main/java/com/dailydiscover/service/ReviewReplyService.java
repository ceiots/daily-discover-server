package com.dailydiscover.service;

import com.dailydiscover.model.ReviewReply;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 评论回复服务接口
 */
public interface ReviewReplyService extends IService<ReviewReply> {
    
    /**
     * 根据评论ID查询回复
     */
    java.util.List<ReviewReply> getByReviewId(Long reviewId);
    
    /**
     * 根据回复者ID查询回复
     */
    java.util.List<ReviewReply> getByReplierId(Long replierId);
    
    /**
     * 添加评论回复
     */
    ReviewReply addReply(Long reviewId, Long replierId, String replierType, String content);
    
    /**
     * 删除评论回复
     */
    boolean deleteReply(Long replyId);
    
    /**
     * 获取评论的回复统计
     */
    Integer getReplyCountByReviewId(Long reviewId);
}