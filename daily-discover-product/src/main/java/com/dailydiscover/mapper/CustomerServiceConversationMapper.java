package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.CustomerServiceConversation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 客服会话表 Mapper
 */
@Mapper
public interface CustomerServiceConversationMapper extends BaseMapper<CustomerServiceConversation> {
    
    /**
     * 根据用户ID查询会话列表
     */
    @Select("SELECT * FROM customer_service_conversations WHERE user_id = #{userId} ORDER BY last_message_time DESC")
    List<CustomerServiceConversation> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据客服ID查询会话列表
     */
    @Select("SELECT * FROM customer_service_conversations WHERE agent_id = #{agentId} ORDER BY last_message_time DESC")
    List<CustomerServiceConversation> findByAgentId(@Param("agentId") Long agentId);
    
    /**
     * 查询未关闭的会话
     */
    @Select("SELECT * FROM customer_service_conversations WHERE status != 'closed' ORDER BY last_message_time DESC")
    List<CustomerServiceConversation> findActiveConversations();
    
    /**
     * 查询待处理的会话
     */
    @Select("SELECT * FROM customer_service_conversations WHERE status = 'pending' ORDER BY created_at ASC")
    List<CustomerServiceConversation> findPendingConversations();
    
    /**
     * 根据会话状态查询
     */
    @Select("SELECT * FROM customer_service_conversations WHERE status = #{status} ORDER BY last_message_time DESC")
    List<CustomerServiceConversation> findByStatus(@Param("status") String status);
}