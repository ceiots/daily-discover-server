package com.example.dao;

import com.example.model.AiChatRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI聊天记录DAO接口
 */
@Mapper
public interface AiChatRecordDao {
    
    /**
     * 插入聊天记录
     */
    int insert(AiChatRecord record);
    
    /**
     * 查询用户的聊天记录，按时间倒序排列
     */
    List<AiChatRecord> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit, @Param("offset") Integer offset);
    
    /**
     * 统计用户的聊天记录数量
     */
    int countByUserId(@Param("userId") Long userId);
    
    /**
     * 根据会话ID获取聊天记录
     */
    List<AiChatRecord> selectBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 标记聊天记录为收藏
     */
    int markAsFavorite(@Param("id") Long id, @Param("isFavorite") Boolean isFavorite);
    
    /**
     * 删除聊天记录
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 删除用户的所有聊天记录
     */
    int deleteByUserId(@Param("userId") Long userId);
} 