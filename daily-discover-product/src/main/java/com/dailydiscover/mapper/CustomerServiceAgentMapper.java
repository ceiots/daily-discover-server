package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.CustomerServiceAgent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 客服人员表 Mapper
 */
@Mapper
public interface CustomerServiceAgentMapper extends BaseMapper<CustomerServiceAgent> {
    
    /**
     * 查询所有在线客服
     */
    @Select("SELECT * FROM customer_service_agents WHERE status = 'online' ORDER BY last_active_time DESC")
    List<CustomerServiceAgent> findOnlineAgents();
    
    /**
     * 查询可用的客服（在线且未达到最大会话数）
     */
    @Select("SELECT * FROM customer_service_agents WHERE status = 'online' AND current_sessions < max_sessions ORDER BY current_sessions ASC")
    List<CustomerServiceAgent> findAvailableAgents();
    
    /**
     * 根据客服状态查询
     */
    @Select("SELECT * FROM customer_service_agents WHERE status = #{status} ORDER BY last_active_time DESC")
    List<CustomerServiceAgent> findByStatus(@Param("status") String status);
    
    /**
     * 根据客服名称模糊查询
     */
    @Select("SELECT * FROM customer_service_agents WHERE agent_name LIKE CONCAT('%', #{name}, '%') ORDER BY last_active_time DESC")
    List<CustomerServiceAgent> findByNameLike(@Param("name") String name);
    
    /**
     * 查询高评分客服
     */
    @Select("SELECT * FROM customer_service_agents WHERE rating >= #{minRating} AND status = 'online' ORDER BY rating DESC")
    List<CustomerServiceAgent> findHighRatingAgents(@Param("minRating") Double minRating);
    
    /**
     * 更新客服状态
     */
    @Select("UPDATE customer_service_agents SET status = #{status} WHERE id = #{agentId}")
    boolean updateAgentStatus(@Param("agentId") Long agentId, @Param("status") String status);
    
    /**
     * 更新客服当前会话数
     */
    @Select("UPDATE customer_service_agents SET current_sessions = #{currentSessions} WHERE id = #{agentId}")
    boolean updateCurrentSessions(@Param("agentId") Long agentId, @Param("currentSessions") Integer currentSessions);
    
    /**
     * 更新客服最后活跃时间
     */
    @Select("UPDATE customer_service_agents SET last_active_time = NOW() WHERE id = #{agentId}")
    boolean updateLastActiveTime(@Param("agentId") Long agentId);
}