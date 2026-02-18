package com.dailydiscover.mapper;

import com.dailydiscover.model.CustomerServiceMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 客服消息表 Mapper
 */
@Mapper
public interface CustomerServiceMessageMapper extends BaseMapper<CustomerServiceMessage> {
    
    /**
     * 根据会话ID查询消息列表
     */
    @Select("SELECT * FROM customer_service_messages WHERE conversation_id = #{conversationId} ORDER BY created_at ASC")
    List<CustomerServiceMessage> findByConversationId(@Param("conversationId") Long conversationId);
    
    /**
     * 查询未读消息
     */
    @Select("SELECT * FROM customer_service_messages WHERE conversation_id = #{conversationId} AND is_read = false ORDER BY created_at ASC")
    List<CustomerServiceMessage> findUnreadMessages(@Param("conversationId") Long conversationId);
    
    /**
     * 根据发送者查询消息
     */
    @Select("SELECT * FROM customer_service_messages WHERE sender_type = #{senderType} AND sender_id = #{senderId} ORDER BY created_at DESC")
    List<CustomerServiceMessage> findBySender(@Param("senderType") String senderType, @Param("senderId") Long senderId);
    
    /**
     * 查询包含文件的客服消息
     */
    @Select("SELECT * FROM customer_service_messages WHERE file_url IS NOT NULL AND conversation_id = #{conversationId} ORDER BY created_at DESC")
    List<CustomerServiceMessage> findMessagesWithFiles(@Param("conversationId") Long conversationId);
    
    /**
     * 统计会话中的消息数量
     */
    @Select("SELECT COUNT(*) FROM customer_service_messages WHERE conversation_id = #{conversationId}")
    Integer countMessagesByConversationId(@Param("conversationId") Long conversationId);
}