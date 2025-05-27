package com.example.service.impl;

import com.example.dao.AiChatRecordDao;
import com.example.model.AiChatRecord;
import com.example.service.AiChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * AI聊天服务实现类
 */
@Service
public class AiChatServiceImpl implements AiChatService {
    
    @Autowired
    private AiChatRecordDao aiChatRecordDao;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Override
    public AiChatRecord saveChatRecord(Long userId, String message, String type, String sessionId) {
        AiChatRecord record = new AiChatRecord();
        record.setUserId(userId);
        record.setMessage(message);
        record.setType(type);
        record.setCreateTime(LocalDateTime.now());
        record.setSessionId(sessionId);
        
        aiChatRecordDao.insert(record);
        return record;
    }
    
    @Override
    public List<Map<String, Object>> getChatHistory(Long userId, Integer pageNum, Integer pageSize) {
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        
        int offset = (pageNum - 1) * pageSize;
        List<AiChatRecord> records = aiChatRecordDao.selectByUserId(userId, pageSize, offset);
        
        return convertRecordsToMap(records);
    }
    
    
    @Override
    public List<String> getQuickQuestions() {
        return Arrays.asList(
            "今日有什么好物推荐？",
            "这周最热门的智能产品是什么？",
            "有哪些提高生活品质的好物？",
            "给我推荐一些厨房用品",
            "有适合送礼的商品吗？",
            "夏季穿搭有什么建议？",
            "如何提高工作效率？",
            "数字极简主义是什么？",
            "健康饮食有哪些原则？",
            "如何改善睡眠质量？"
        );
    }
    
    @Override
    @Transactional
    public boolean clearChatHistory(Long userId) {
        try {
            aiChatRecordDao.deleteByUserId(userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> getChatHistoryBySessionId(String sessionId) {
        List<AiChatRecord> records = aiChatRecordDao.selectBySessionId(sessionId);
        return convertRecordsToMap(records);
    }
    
    @Override
    public String createNewSession(Long userId) {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 将聊天记录转换为Map
     */
    private List<Map<String, Object>> convertRecordsToMap(List<AiChatRecord> records) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (AiChatRecord record : records) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", record.getId());
            map.put("type", record.getType());
            map.put("message", record.getMessage());
            map.put("timestamp", record.getCreateTime().format(DATETIME_FORMATTER));
            map.put("sessionId", record.getSessionId());
            result.add(map);
        }
        
        return result;
    }
} 