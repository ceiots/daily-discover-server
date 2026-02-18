package com.dailydiscover.service;

import com.dailydiscover.model.CustomerServiceMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客服消息服务接口
 */
public interface CustomerServiceMessageService extends IService<CustomerServiceMessage> {
    
    /**
     * 根据会话ID查询消息列表
     */
    java.util.List<CustomerServiceMessage> findByConversationId(Long conversationId);
    
    /**
     * 查询未读消息
     */
    java.util.List<CustomerServiceMessage> findUnreadMessages(Long conversationId);
    
    /**
     * 根据发送者查询消息
     */
    java.util.List<CustomerServiceMessage> findBySender(String senderType, Long senderId);
    
    /**
     * 查询包含文件的客服消息
     */
    java.util.List<CustomerServiceMessage> findMessagesWithFiles(Long conversationId);
    
    /**
     * 统计会话中的消息数量
     */
    Integer countMessagesByConversationId(Long conversationId);
    
    /**
     * 发送客服消息
     */
    CustomerServiceMessage sendMessage(Long conversationId, String senderType, Long senderId, 
                                      String messageType, String content, String fileUrl);
    
    /**
     * 标记消息为已读
     */
    boolean markMessageAsRead(Long messageId);
    
    /**
     * 批量标记消息为已读
     */
    boolean batchMarkMessagesAsRead(java.util.List<Long> messageIds);
    
    /**
     * 删除消息
     */
    boolean deleteMessage(Long messageId);
    
    /**
     * 获取会话中的最新消息
     */
    CustomerServiceMessage getLatestMessage(Long conversationId);
}