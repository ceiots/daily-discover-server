package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI聊天记录实体类
 */
@Data
public class AiChatRecord {
    
    private Long id;
    
    private Long userId;
    
    private String message;
    
    private String type; // "user" 或 "ai"
    
    private LocalDateTime createTime;
    
    private String sessionId;
    
} 