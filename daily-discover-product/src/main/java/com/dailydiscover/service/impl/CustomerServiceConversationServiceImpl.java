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
    public List<CustomerServiceConversation> findByUserId(Long userId) {
        return customerServiceConversationMapper.findByUserId(userId);
    }
    
    @Override
    public List<CustomerServiceConversation> findByAgentId(Long agentId) {
        return customerServiceConversationMapper.findByAgentId(agentId);
    }
    
    @Override
    public List<CustomerServiceConversation> findActiveConversations() {
        return customerServiceConversationMapper.findActiveConversations();
    }
    
    @Override
    public List<CustomerServiceConversation> findPendingConversations() {
        return customerServiceConversationMapper.findPendingConversations();
    }
    
    @Override
    public List<CustomerServiceConversation> findByStatus(String status) {
        return customerServiceConversationMapper.findByStatus(status);
    }
    
    @Override
    public CustomerServiceConversation createConversation(Long userId, Long categoryId, String issueDescription) {
        CustomerServiceConversation conversation = new CustomerServiceConversation();
        conversation.setUserId(userId);
        conversation.setCategoryId(categoryId);
        conversation.setIssueDescription(issueDescription);
        conversation.setStatus("pending");
        
        save(conversation);
        return conversation;
    }
    
    @Override
    public boolean assignAgentToConversation(Long conversationId, Long agentId) {
        CustomerServiceConversation conversation = getById(conversationId);
        if (conversation != null) {
            conversation.setAgentId(agentId);
            conversation.setStatus("assigned");
            return updateById(conversation);
        }
        return false;
    }
    
    @Override
    public boolean updateConversationStatus(Long conversationId, String status) {
        CustomerServiceConversation conversation = getById(conversationId);
        if (conversation != null) {
            conversation.setStatus(status);
            return updateById(conversation);
        }
        return false;
    }
    
    @Override
    public boolean updateLastMessageTime(Long conversationId) {
        // 直接使用lambdaUpdate更新，避免先查询再更新
        return lambdaUpdate()
                .eq(CustomerServiceConversation::getId, conversationId)
                .set(CustomerServiceConversation::getLastMessageTime, new java.util.Date())
                .update();
    }
    
    @Override
    public java.util.Map<String, Object> getConversationStats() {
        long totalConversations = count();
        long pendingConversations = lambdaQuery().eq(CustomerServiceConversation::getStatus, "pending").count();
        long activeConversations = lambdaQuery().eq(CustomerServiceConversation::getStatus, "active").count();
        long closedConversations = lambdaQuery().eq(CustomerServiceConversation::getStatus, "closed").count();
        
        return java.util.Map.of(
            "total", totalConversations,
            "pending", pendingConversations,
            "active", activeConversations,
            "closed", closedConversations
        );
    }
}