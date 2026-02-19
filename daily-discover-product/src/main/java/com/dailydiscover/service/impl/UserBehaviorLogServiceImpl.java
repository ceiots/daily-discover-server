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
        return userBehaviorLogMapper.findByUserId(userId, 1000); // 默认限制1000条记录
    }
    
    @Override
    public List<UserBehaviorLog> getByBehaviorType(String behaviorType) {
        return userBehaviorLogMapper.findByBehaviorType(behaviorType, 1000); // 默认限制1000条记录
    }
    
    @Override
    public List<UserBehaviorLog> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        // 使用 MyBatis-Plus 的 lambda 查询替代不存在的 Mapper 方法
        return lambdaQuery()
                .between(UserBehaviorLog::getCreatedAt, startTime, endTime)
                .orderByDesc(UserBehaviorLog::getCreatedAt)
                .list();
    }
    
    @Override
    public UserBehaviorLog recordBehavior(Long userId, String behaviorType, Long targetId, String targetType, String details) {
        // 使用 MyBatis-Plus 的 save 方法替代不存在的 Mapper 方法
        UserBehaviorLog log = new UserBehaviorLog();
        log.setUserId(userId);
        log.setBehaviorType(behaviorType);
        log.setProductId(targetId);
        log.setTargetType(targetType);
        log.setDetails(details);
        
        save(log);
        return log;
    }
    
    @Override
    public Map<String, Object> getUserBehaviorStats(Long userId) {
        // 实现用户行为统计逻辑
        List<UserBehaviorLog> logs = getByUserId(userId);
        
        long totalBehaviors = logs.size();
        long viewCount = logs.stream().filter(log -> "view".equals(log.getBehaviorType())).count();
        long clickCount = logs.stream().filter(log -> "click".equals(log.getBehaviorType())).count();
        
        return Map.of(
            "totalBehaviors", totalBehaviors,
            "viewCount", viewCount,
            "clickCount", clickCount
        );
    }
    
    @Override
    public List<Map<String, Object>> getHotProductBehaviorStats(Integer limit) {
        // 实现热门商品行为统计逻辑
        List<Long> popularProducts = getPopularProducts(limit);
        
        return popularProducts.stream()
                .map(productId -> {
                    List<UserBehaviorLog> productLogs = getProductBehaviorHistory(productId, 1000);
                    long viewCount = productLogs.stream().filter(log -> "view".equals(log.getBehaviorType())).count();
                    long clickCount = productLogs.stream().filter(log -> "click".equals(log.getBehaviorType())).count();
                    
                    return Map.of(
                        "productId", productId,
                        "viewCount", viewCount,
                        "clickCount", clickCount
                    );
                })
                .collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public Map<String, Object> getBehaviorTrendAnalysis(String behaviorType, LocalDateTime startTime, LocalDateTime endTime) {
        // 实现行为趋势分析逻辑
        List<UserBehaviorLog> logs = getByTimeRange(startTime, endTime);
        
        long totalBehaviors = logs.size();
        long targetBehaviors = logs.stream().filter(log -> behaviorType.equals(log.getBehaviorType())).count();
        double behaviorRate = totalBehaviors > 0 ? (double) targetBehaviors / totalBehaviors : 0.0;
        
        return Map.of(
            "totalBehaviors", totalBehaviors,
            "targetBehaviors", targetBehaviors,
            "behaviorRate", behaviorRate
        );
    }
}