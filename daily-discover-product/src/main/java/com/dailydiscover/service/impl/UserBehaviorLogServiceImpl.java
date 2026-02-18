package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.UserBehaviorLogMapper;
import com.dailydiscover.model.UserBehaviorLog;
import com.dailydiscover.service.UserBehaviorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserBehaviorLogServiceImpl extends ServiceImpl<UserBehaviorLogMapper, UserBehaviorLog> implements UserBehaviorLogService {
    
    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;
    
    @Override
    public boolean recordUserBehavior(Long userId, Long productId, String behaviorType, String sessionId) {
        return userBehaviorLogMapper.recordUserBehavior(userId, productId, behaviorType, sessionId);
    }
    
    @Override
    public List<UserBehaviorLog> getUserBehaviorHistory(Long userId, int limit) {
        return userBehaviorLogMapper.getUserBehaviorHistory(userId, limit);
    }
    
    @Override
    public List<UserBehaviorLog> getProductBehaviorHistory(Long productId, int limit) {
        return userBehaviorLogMapper.getProductBehaviorHistory(productId, limit);
    }
    
    @Override
    public List<Long> getRecentlyViewedProducts(Long userId, int limit) {
        return userBehaviorLogMapper.getRecentlyViewedProducts(userId, limit);
    }
    
    @Override
    public List<Long> getPopularProducts(int limit) {
        return userBehaviorLogMapper.getPopularProducts(limit);
    }
    
    @Override
    public List<UserBehaviorLog> getByUserId(Long userId) {
        return userBehaviorLogMapper.getByUserId(userId);
    }
    
    @Override
    public List<UserBehaviorLog> getByBehaviorType(String behaviorType) {
        return userBehaviorLogMapper.getByBehaviorType(behaviorType);
    }
    
    @Override
    public List<UserBehaviorLog> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return userBehaviorLogMapper.getByTimeRange(startTime, endTime);
    }
    
    @Override
    public UserBehaviorLog recordBehavior(Long userId, String behaviorType, Long targetId, String targetType, String details) {
        return userBehaviorLogMapper.recordBehavior(userId, behaviorType, targetId, targetType, details);
    }
    
    @Override
    public Map<String, Object> getUserBehaviorStats(Long userId) {
        return userBehaviorLogMapper.getUserBehaviorStats(userId);
    }
    
    @Override
    public List<Map<String, Object>> getHotProductBehaviorStats(Integer limit) {
        return userBehaviorLogMapper.getHotProductBehaviorStats(limit);
    }
    
    @Override
    public Map<String, Object> getBehaviorTrendAnalysis(String behaviorType, LocalDateTime startTime, LocalDateTime endTime) {
        return userBehaviorLogMapper.getBehaviorTrendAnalysis(behaviorType, startTime, endTime);
    }
}