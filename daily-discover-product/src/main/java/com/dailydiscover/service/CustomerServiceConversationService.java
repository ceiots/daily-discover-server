package com.dailydiscover.service;

import com.dailydiscover.model.CustomerServiceConversation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客服会话服务接口
 */
public interface CustomerServiceConversationService extends IService<CustomerServiceConversation> {
    
    /**
     * 根据用户ID查询会话列表
     */
    java.util.List<CustomerServiceConversation> findByUserId(Long userId);
    
    /**
     * 根据客服ID查询会话列表
     */
    java.util.List<CustomerServiceConversation> findByAgentId(Long agentId);
    
    /**
     * 查询未关闭的会话
     */
    java.util.List<CustomerServiceConversation> findActiveConversations();
    
    /**
     * 查询待处理的会话
     */
    java.util.List<CustomerServiceConversation> findPendingConversations();
    
    /**
     * 根据会话状态查询
     */
    java.util.List<CustomerServiceConversation> findByStatus(String status);
    
    /**
     * 创建客服会话
     */
    CustomerServiceConversation createConversation(Long userId, Long categoryId, String issueDescription);
    
    /**
     * 分配客服给会话
     */
    boolean assignAgentToConversation(Long conversationId, Long agentId);
    
    /**
     * 更新会话状态
     */
    boolean updateConversationStatus(Long conversationId, String status);
    
    /**
     * 更新会话最后消息时间
     */
    boolean updateLastMessageTime(Long conversationId);
    
    /**
     * 获取会话统计信息
     */
    java.util.Map<String, Object> getConversationStats();
}