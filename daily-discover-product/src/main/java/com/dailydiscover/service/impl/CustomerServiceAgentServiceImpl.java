package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.CustomerServiceAgentMapper;
import com.dailydiscover.model.CustomerServiceAgent;
import com.dailydiscover.service.CustomerServiceAgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerServiceAgentServiceImpl extends ServiceImpl<CustomerServiceAgentMapper, CustomerServiceAgent> implements CustomerServiceAgentService {
    
    @Autowired
    private CustomerServiceAgentMapper customerServiceAgentMapper;
    
    @Override
    public List<CustomerServiceAgent> getOnlineAgents() {
        return customerServiceAgentMapper.findOnlineAgents();
    }
    
    @Override
    public List<CustomerServiceAgent> getAvailableAgents() {
        return customerServiceAgentMapper.findAvailableAgents();
    }
    
    @Override
    public List<CustomerServiceAgent> findByStatus(String status) {
        return customerServiceAgentMapper.findByStatus(status);
    }
    
    @Override
    public List<CustomerServiceAgent> findByNameLike(String name) {
        return customerServiceAgentMapper.findByNameLike(name);
    }
    
    @Override
    public List<CustomerServiceAgent> findHighRatingAgents(Double minRating) {
        return customerServiceAgentMapper.findHighRatingAgents(minRating);
    }
    
    @Override
    public boolean updateAgentStatus(Long agentId, String status) {
        return customerServiceAgentMapper.updateAgentStatus(agentId, status);
    }
    
    @Override
    public boolean updateCurrentSessions(Long agentId, Integer currentSessions) {
        return customerServiceAgentMapper.updateCurrentSessions(agentId, currentSessions);
    }
    
    @Override
    public boolean updateLastActiveTime(Long agentId) {
        return customerServiceAgentMapper.updateLastActiveTime(agentId);
    }
}