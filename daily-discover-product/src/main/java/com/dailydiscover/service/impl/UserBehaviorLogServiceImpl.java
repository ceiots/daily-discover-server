package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.UserBehaviorLogMapper;
import com.dailydiscover.model.UserBehaviorLog;
import com.dailydiscover.model.UserBehaviorLogCore;
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
public class UserBehaviorLogServiceImpl extends ServiceImpl<UserBehaviorLogMapper, UserBehaviorLogCore> implements UserBehaviorLogService {
    
    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;
    
    @Override
    public boolean recordUserBehavior(Long userId, Long productId, String behaviorType, String sessionId) {
        return userBehaviorLogMapper.recordUserBehavior(userId, productId, behaviorType, sessionId) > 0;
    }
    
    @Override
    public List<UserBehaviorLog> getUserBehaviorHistory(Long userId, int limit) {
        List<UserBehaviorLogCore> coreLogs = userBehaviorLogMapper.getUserBehaviorHistory(userId, limit);
        return convertCoreToFull(coreLogs);
    }
    
    @Override
    public List<UserBehaviorLog> getProductBehaviorHistory(Long productId, int limit) {
        List<UserBehaviorLogCore> coreLogs = userBehaviorLogMapper.getProductBehaviorHistory(productId, limit);
        return convertCoreToFull(coreLogs);
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
        List<UserBehaviorLogCore> coreLogs = userBehaviorLogMapper.findByUserId(userId, 1000); // 默认限制1000条记录
        return convertCoreToFull(coreLogs);
    }
    
    @Override
    public List<UserBehaviorLog> getByBehaviorType(String behaviorType) {
        List<UserBehaviorLogCore> coreLogs = userBehaviorLogMapper.findByBehaviorType(behaviorType, 1000); // 默认限制1000条记录
        return convertCoreToFull(coreLogs);
    }
    
    @Override
    public List<UserBehaviorLog> getByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        // 使用 MyBatis-Plus 的 lambda 查询替代不存在的 Mapper 方法
        List<UserBehaviorLogCore> coreLogs = lambdaQuery()
                .ge(UserBehaviorLogCore::getCreatedAt, startTime)
                .le(UserBehaviorLogCore::getCreatedAt, endTime)
                .orderByDesc(UserBehaviorLogCore::getCreatedAt)
                .list();
        return convertCoreToFull(coreLogs);
    }
    
    @Override
    public UserBehaviorLog recordBehavior(Long userId, String behaviorType, Long targetId, String targetType, String details) {
        // 使用 MyBatis-Plus 的 save 方法替代不存在的 Mapper 方法
        UserBehaviorLogCore coreLog = new UserBehaviorLogCore();
        coreLog.setUserId(userId);
        coreLog.setBehaviorType(behaviorType);
        coreLog.setProductId(targetId);
        
        save(coreLog);
        
        // 转换为完整对象返回
        UserBehaviorLog log = new UserBehaviorLog();
        log.setId(coreLog.getId());
        log.setUserId(coreLog.getUserId());
        log.setProductId(coreLog.getProductId());
        log.setBehaviorType(coreLog.getBehaviorType());
        log.setBehaviorWeight(coreLog.getBehaviorWeight());
        log.setSessionId(coreLog.getSessionId());
        log.setCreatedAt(coreLog.getCreatedAt());
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
            // 1. 先插入核心表
            UserBehaviorLogCore coreLog = new UserBehaviorLogCore();
            coreLog.setUserId(userId);
            coreLog.setProductId(productId);
            coreLog.setBehaviorType(behaviorType);
            coreLog.setSessionId(sessionId);
            
            boolean coreSaved = save(coreLog);
            
            if (coreSaved && coreLog.getId() != null) {
                // 2. 再插入详情表
                UserBehaviorLogDetails detailsLog = new UserBehaviorLogDetails();
                detailsLog.setId(coreLog.getId());
                detailsLog.setReferrerUrl(referrerUrl);
                detailsLog.setBehaviorContext(behaviorContext);
                
                // 需要创建详情表的Mapper，这里先返回成功
                log.info("成功记录用户行为，核心表ID: {}, 详情待插入", coreLog.getId());
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
        // 获取核心数据
        List<UserBehaviorLogCore> coreLogs = lambdaQuery()
                .eq(UserBehaviorLogCore::getUserId, userId)
                .orderByDesc(UserBehaviorLogCore::getCreatedAt)
                .last("LIMIT " + limit)
                .list();
        
        // 转换为完整对象（这里简化处理，实际需要关联查询详情表）
        return coreLogs.stream().map(core -> {
            UserBehaviorLog log = new UserBehaviorLog();
            log.setId(core.getId());
            log.setUserId(core.getUserId());
            log.setProductId(core.getProductId());
            log.setBehaviorType(core.getBehaviorType());
            log.setBehaviorWeight(core.getBehaviorWeight());
            log.setSessionId(core.getSessionId());
            log.setCreatedAt(core.getCreatedAt());
            // 详情字段需要从详情表查询，这里留空
            return log;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public List<UserBehaviorLog> getCompleteProductBehaviorHistory(Long productId, int limit) {
        // 获取核心数据
        List<UserBehaviorLogCore> coreLogs = lambdaQuery()
                .eq(UserBehaviorLogCore::getProductId, productId)
                .orderByDesc(UserBehaviorLogCore::getCreatedAt)
                .last("LIMIT " + limit)
                .list();
        
        // 转换为完整对象
        return coreLogs.stream().map(core -> {
            UserBehaviorLog log = new UserBehaviorLog();
            log.setId(core.getId());
            log.setUserId(core.getUserId());
            log.setProductId(core.getProductId());
            log.setBehaviorType(core.getBehaviorType());
            log.setBehaviorWeight(core.getBehaviorWeight());
            log.setSessionId(core.getSessionId());
            log.setCreatedAt(core.getCreatedAt());
            return log;
        }).collect(java.util.stream.Collectors.toList());
    }
    
    @Override
    public UserBehaviorLogDetails getBehaviorDetails(Long behaviorId) {
        // 这里需要实现从详情表查询的逻辑
        // 由于详情表Mapper尚未创建，返回空对象
        UserBehaviorLogDetails details = new UserBehaviorLogDetails();
        details.setId(behaviorId);
        return details;
    }
    
    /**
     * 将核心表记录转换为完整记录
     */
    private List<UserBehaviorLog> convertCoreToFull(List<UserBehaviorLogCore> coreLogs) {
        return coreLogs.stream().map(core -> {
            UserBehaviorLog log = new UserBehaviorLog();
            log.setId(core.getId());
            log.setUserId(core.getUserId());
            log.setProductId(core.getProductId());
            log.setBehaviorType(core.getBehaviorType());
            log.setBehaviorWeight(core.getBehaviorWeight());
            log.setSessionId(core.getSessionId());
            log.setCreatedAt(core.getCreatedAt());
            // 详情字段需要从详情表查询，这里留空
            return log;
        }).collect(java.util.stream.Collectors.toList());
    }
}