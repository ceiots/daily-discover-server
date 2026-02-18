package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.CustomerServiceConversationMapper;
import com.dailydiscover.model.CustomerServiceConversation;
import com.dailydiscover.service.CustomerServiceConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerServiceConversationServiceImpl extends ServiceImpl<CustomerServiceConversationMapper, CustomerServiceConversation> implements CustomerServiceConversationService {
    
    @Autowired
    private CustomerServiceConversationMapper customerServiceConversationMapper;
    
    @Override
    public List<CustomerServiceConversation> getByUserId(Long userId) {
        return customerServiceConversationMapper.findByUserId(userId);
    }
    
    @Override
    public List<CustomerServiceConversation> getByAgentId(Long agentId) {
        return customerServiceConversationMapper.findByAgentId(agentId);
    }
    
    @Override
    public List<CustomerServiceConversation> getActiveConversations() {
        return customerServiceConversationMapper.findActiveConversations();
    }
    
    @Override
    public List<CustomerServiceConversation> getPendingConversations() {
        return customerServiceConversationMapper.findPendingConversations();
    }
    
    @Override
    public List<CustomerServiceConversation> getByStatus(String status) {
        return customerServiceConversationMapper.findByStatus(status);
    }
    
    @Override
    public CustomerServiceConversation createConversation(Long userId, String subject, String category) {
        CustomerServiceConversation conversation = new CustomerServiceConversation();
        conversation.setUserId(userId);
        conversation.setSubject(subject);
        conversation.setCategory(category);
        conversation.setStatus("pending");
        
        save(conversation);
        return conversation;
    }
    
    @Override
    public boolean assignConversation(Long conversationId, Long agentId) {
        CustomerServiceConversation conversation = getById(conversationId);
        if (conversation != null) {
            conversation.setAgentId(agentId);
            conversation.setStatus("assigned");
            return updateById(conversation);
        }
        return false;
    }
    
    @Override
    public boolean closeConversation(Long conversationId) {
        CustomerServiceConversation conversation = getById(conversationId);
        if (conversation != null) {
            conversation.setStatus("closed");
            return updateById(conversation);
        }
        return false;
    }
    
    @Override
    public boolean updateLastMessageTime(Long conversationId) {
        CustomerServiceConversation conversation = getById(conversationId);
        if (conversation != null) {
            // 使用MyBatis Plus的update方法更新最后消息时间
            return lambdaUpdate()
                    .eq(CustomerServiceConversation::getId, conversationId)
                    .set(CustomerServiceConversation::getLastMessageTime, new java.util.Date())
                    .update();
        }
        return false;
    }
}