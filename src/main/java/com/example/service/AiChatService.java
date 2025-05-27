package com.example.service;

import com.example.model.AiChatRecord;

import java.util.List;
import java.util.Map;

/**
 * AI聊天服务接口
 */
public interface AiChatService {
    
    /**
     * 保存聊天记录
     */
    AiChatRecord saveChatRecord(Long userId, String message, String type, String sessionId);
    
    /**
     * 获取用户聊天历史
     */
    List<Map<String, Object>> getChatHistory(Long userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取快速提问列表
     */
    List<String> getQuickQuestions();
    
    /**
     * 清空聊天历史
     */
    boolean clearChatHistory(Long userId);
    
    /**
     * 根据会话ID获取聊天记录
     */
    List<Map<String, Object>> getChatHistoryBySessionId(String sessionId);
    
    /**
     * 创建新会话
     */
    String createNewSession(Long userId);
} 