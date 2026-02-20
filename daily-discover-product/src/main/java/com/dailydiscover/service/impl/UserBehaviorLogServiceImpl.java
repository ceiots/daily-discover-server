package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.UserBehaviorLogMapper;
import com.dailydiscover.model.UserBehaviorLog;
import com.dailydiscover.model.UserBehaviorLogDetails;
import com.dailydiscover.dto.ProductViewCountDTO;
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
        return userBehaviorLogMapper.recordUserBehavior(userId, productId, behaviorType, sessionId) > 0;
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
    public List<ProductViewCountDTO> getPopularProducts(int limit) {
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
        // 调用 Mapper 层根据时间范围查询
        return userBehaviorLogMapper.findByTimeRange(startTime, endTime);
    }
    
    @Override
    public UserBehaviorLog recordBehavior(Long userId, String behaviorType, Long targetId, String targetType, String details) {
        // 调用 Mapper 层记录用户行为
        int result = userBehaviorLogMapper.recordUserBehavior(userId, targetId, behaviorType, null);
        
        if (result > 0) {
            // 返回新创建的用户行为记录
            List<UserBehaviorLog> logs = userBehaviorLogMapper.findByUserId(userId, 1);
            return logs.isEmpty() ? null : logs.get(0);
        }
        return null;
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
        List<ProductViewCountDTO> popularProducts = getPopularProducts(limit);
        
        return popularProducts.stream()
                .map(dto -> {
                    List<UserBehaviorLog> productLogs = getProductBehaviorHistory(dto.getProductId(), 1000);
                    long viewCount = dto.getViewCount(); // 直接从DTO获取视图计数
                    long clickCount = productLogs.stream().filter(log -> "click".equals(log.getBehaviorType())).count();
                    
                    return Map.of(
                        "productId", (Object) dto.getProductId(),
                        "viewCount", (Object) viewCount,
                        "clickCount", (Object) clickCount
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
    
    @Override
    public boolean recordUserBehaviorWithDetails(Long userId, Long productId, String behaviorType, 
                                                String sessionId, String referrerUrl, String behaviorContext) {
        try {
            // 调用 Mapper 层记录用户行为
            int result = userBehaviorLogMapper.recordUserBehavior(userId, productId, behaviorType, sessionId);
            
            if (result > 0) {
                log.info("成功记录用户行为，用户ID: {}, 商品ID: {}, 行为类型: {}", userId, productId, behaviorType);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("记录用户行为详情失败", e);
            return false;
        }
    }
    
    @Override
    public List<UserBehaviorLog> getCompleteUserBehaviorHistory(Long userId, int limit) {
        // 调用 Mapper 层获取用户行为历史数据
        return userBehaviorLogMapper.findByUserId(userId, limit);
    }
    
    @Override
    public List<UserBehaviorLog> getCompleteProductBehaviorHistory(Long productId, int limit) {
        // 调用 Mapper 层获取商品行为历史数据
        return userBehaviorLogMapper.findByProductId(productId, limit);
    }
    
    @Override
    public UserBehaviorLogDetails getBehaviorDetails(Long behaviorId) {
        // 这里需要实现从详情表查询的逻辑
        // 由于详情表Mapper尚未创建，返回空对象
        UserBehaviorLogDetails details = new UserBehaviorLogDetails();
        details.setId(behaviorId);
        return details;
    }
    

}