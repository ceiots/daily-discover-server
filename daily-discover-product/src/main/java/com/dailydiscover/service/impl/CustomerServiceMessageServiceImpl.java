package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.CustomerServiceMessageMapper;
import com.dailydiscover.model.CustomerServiceMessage;
import com.dailydiscover.service.CustomerServiceMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerServiceMessageServiceImpl extends ServiceImpl<CustomerServiceMessageMapper, CustomerServiceMessage> implements CustomerServiceMessageService {
    
    @Autowired
    private CustomerServiceMessageMapper customerServiceMessageMapper;
    
    @Override
    public java.util.List<CustomerServiceMessage> findByConversationId(Long conversationId) {
        return customerServiceMessageMapper.findByConversationId(conversationId);
    }
    
    @Override
    public java.util.List<CustomerServiceMessage> findUnreadMessages(Long conversationId) {
        return customerServiceMessageMapper.findUnreadMessages(conversationId);
    }
    
    @Override
    public java.util.List<CustomerServiceMessage> findBySender(String senderType, Long senderId) {
        return customerServiceMessageMapper.findBySender(senderType, senderId);
    }
    
    @Override
    public java.util.List<CustomerServiceMessage> findMessagesWithFiles(Long conversationId) {
        return customerServiceMessageMapper.findMessagesWithFiles(conversationId);
    }
    
    @Override
    public Integer countMessagesByConversationId(Long conversationId) {
        return customerServiceMessageMapper.countMessagesByConversationId(conversationId);
    }
    
    @Override
    public CustomerServiceMessage sendMessage(Long conversationId, String senderType, Long senderId, 
                                            String messageType, String content, String fileUrl) {
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setConversationId(conversationId);
        message.setSenderType(senderType);
        message.setSenderId(senderId);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setFileUrl(fileUrl);
        message.setIsRead(false);
        
        save(message);
        return message;
    }
    
    @Override
    public boolean markMessageAsRead(Long messageId) {
        CustomerServiceMessage message = getById(messageId);
        if (message != null) {
            message.setIsRead(true);
            return updateById(message);
        }
        return false;
    }
    
    @Override
    public boolean batchMarkMessagesAsRead(java.util.List<Long> messageIds) {
        return lambdaUpdate()
                .in(CustomerServiceMessage::getId, messageIds)
                .set(CustomerServiceMessage::getIsRead, true)
                .update();
    }
    
    @Override
    public boolean attachFileToMessage(Long messageId, String fileUrl, String fileName) {
        CustomerServiceMessage message = getById(messageId);
        if (message != null) {
            message.setFileUrl(fileUrl);
            message.setFileName(fileName);
            return updateById(message);
        }
        return false;
    }
}