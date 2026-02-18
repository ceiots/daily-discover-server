package com.dailydiscover.service;

import com.dailydiscover.model.CustomerServiceAgent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客服人员服务接口
 */
public interface CustomerServiceAgentService extends IService<CustomerServiceAgent> {
    
    /**
     * 查询所有在线客服
     */
    java.util.List<CustomerServiceAgent> getOnlineAgents();
    
    /**
     * 查询可用的客服（在线且未达到最大会话数）
     */
    java.util.List<CustomerServiceAgent> getAvailableAgents();
    
    /**
     * 根据客服状态查询
     */
    java.util.List<CustomerServiceAgent> findByStatus(String status);
    
    /**
     * 根据客服名称模糊查询
     */
    java.util.List<CustomerServiceAgent> findByNameLike(String name);
    
    /**
     * 查询高评分客服
     */
    java.util.List<CustomerServiceAgent> findHighRatingAgents(Double minRating);
    
    /**
     * 更新客服状态
     */
    boolean updateAgentStatus(Long agentId, String status);
    
    /**
     * 更新客服当前会话数
     */
    boolean updateCurrentSessions(Long agentId, Integer currentSessions);
    
    /**
     * 更新客服最后活动时间
     */
    boolean updateLastActiveTime(Long agentId);
}