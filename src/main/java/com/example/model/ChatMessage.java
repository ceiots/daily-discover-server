package com.example.model;

import java.util.Date;
import lombok.Data;

/**
 * 聊天消息模型，用于WebSocket通信
 */
@Data
public class ChatMessage {
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息类型：user, AI, ERROR, COMPLETE等
     */
    private String type;
    
    /**
     * 消息发送时间
     */
    private Date timestamp;
    
    /**
     * 会话ID
     */
    private String sessionId;
} 