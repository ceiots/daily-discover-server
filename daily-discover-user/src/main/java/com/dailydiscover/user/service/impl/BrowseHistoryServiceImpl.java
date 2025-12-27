package com.dailydiscover.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.user.dto.BrowseHistoryResponse;
import com.dailydiscover.user.entity.BrowseHistory;
import com.dailydiscover.user.mapper.BrowseHistoryMapper;
import com.dailydiscover.user.service.BrowseHistoryService;
import com.dailydiscover.common.util.LogTracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户浏览历史服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    private final BrowseHistoryMapper browseHistoryMapper;

    @Override
    @Transactional
    public BrowseHistoryResponse addBrowseHistory(BrowseHistory browseHistory) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(browseHistory, null);
        
        try {
            browseHistory.setViewedAt(LocalDateTime.now());
            browseHistory.setCreatedAt(LocalDateTime.now());
            
            int result = browseHistoryMapper.insert(browseHistory);
            LogTracer.traceDatabaseQuery("INSERT INTO browse_history", new Object[]{browseHistory}, result);
            
            if (result <= 0) {
                throw new RuntimeException("添加浏览记录失败");
            }
            
            BrowseHistoryResponse response = convertToResponse(browseHistory);
            LogTracer.traceBusinessMethod(browseHistory, response);
            LogTracer.traceBusinessPerformance(startTime);
            
            return response;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    public List<BrowseHistoryResponse> getBrowseHistoryByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            QueryWrapper<BrowseHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .orderByDesc("viewed_at");
            
            List<BrowseHistory> browseHistories = browseHistoryMapper.selectList(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT * FROM browse_history WHERE user_id = ? ORDER BY viewed_at DESC", 
                new Object[]{userId}, browseHistories.size());
            
            List<BrowseHistoryResponse> responses = browseHistories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            LogTracer.traceBusinessMethod(userId, responses.size());
            LogTracer.traceBusinessPerformance(startTime);
            
            return responses;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean deleteBrowseHistory(Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(id, null);
        
        try {
            int result = browseHistoryMapper.deleteById(id);
            LogTracer.traceDatabaseQuery("DELETE FROM browse_history WHERE id = ?", new Object[]{id}, result);
            
            boolean success = result > 0;
            LogTracer.traceBusinessMethod(id, success);
            LogTracer.traceBusinessPerformance(startTime);
            
            return success;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean clearBrowseHistory(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            int result = browseHistoryMapper.deleteByUserId(userId);
            LogTracer.traceDatabaseQuery("DELETE FROM browse_history WHERE user_id = ?", new Object[]{userId}, result);
            
            boolean success = result >= 0;
            LogTracer.traceBusinessMethod(userId, success);
            LogTracer.traceBusinessPerformance(startTime);
            
            return success;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    @Override
    public int countBrowseHistory(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceBusinessMethod(userId, null);
        
        try {
            int count = browseHistoryMapper.countByUserId(userId);
            LogTracer.traceDatabaseQuery("SELECT COUNT(*) FROM browse_history WHERE user_id = ?", new Object[]{userId}, count);
            
            LogTracer.traceBusinessMethod(userId, count);
            LogTracer.traceBusinessPerformance(startTime);
            
            return count;
        } catch (Exception e) {
            LogTracer.traceBusinessException(e);
            throw e;
        }
    }

    /**
     * 将BrowseHistory实体转换为BrowseHistoryResponse DTO
     */
    private BrowseHistoryResponse convertToResponse(BrowseHistory browseHistory) {
        BrowseHistoryResponse response = new BrowseHistoryResponse();
        BeanUtils.copyProperties(browseHistory, response);
        return response;
    }
}