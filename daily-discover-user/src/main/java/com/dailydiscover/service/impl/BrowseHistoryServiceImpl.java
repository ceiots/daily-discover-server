package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.dto.BrowseHistoryResponse;
import com.dailydiscover.entity.BrowseHistory;
import com.dailydiscover.mapper.BrowseHistoryMapper;
import com.dailydiscover.service.BrowseHistoryService;
import com.dailydiscover.util.LogTracer;
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
        LogTracer.traceMethod("BrowseHistoryService.addBrowseHistory", "开始添加浏览记录", browseHistory);
        
        try {
            browseHistory.setViewedAt(LocalDateTime.now());
            browseHistory.setCreatedAt(LocalDateTime.now());
            
            int result = browseHistoryMapper.insert(browseHistory);
            LogTracer.traceDatabaseQuery("INSERT", "添加浏览记录", browseHistory, result);
            
            if (result <= 0) {
                throw new RuntimeException("添加浏览记录失败");
            }
            
            BrowseHistoryResponse response = convertToResponse(browseHistory);
            LogTracer.traceMethod("BrowseHistoryService.addBrowseHistory", "浏览记录添加成功", response);
            LogTracer.tracePerformance("BrowseHistoryService.addBrowseHistory", startTime);
            
            return response;
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryService.addBrowseHistory", "添加浏览记录失败", e);
            throw e;
        }
    }

    @Override
    public List<BrowseHistoryResponse> getBrowseHistoryByUserId(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryService.getBrowseHistoryByUserId", "开始获取浏览历史", userId);
        
        try {
            QueryWrapper<BrowseHistory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .orderByDesc("viewed_at");
            
            List<BrowseHistory> browseHistories = browseHistoryMapper.selectList(queryWrapper);
            LogTracer.traceDatabaseQuery("SELECT", "查询用户浏览历史", queryWrapper, browseHistories.size());
            
            List<BrowseHistoryResponse> responses = browseHistories.stream()
                    .map(this::convertToResponse)
                    .collect(Collectors.toList());
            
            LogTracer.traceMethod("BrowseHistoryService.getBrowseHistoryByUserId", "获取浏览历史成功", responses.size());
            LogTracer.tracePerformance("BrowseHistoryService.getBrowseHistoryByUserId", startTime);
            
            return responses;
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryService.getBrowseHistoryByUserId", "获取浏览历史失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean deleteBrowseHistory(Long id) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryService.deleteBrowseHistory", "开始删除浏览记录", id);
        
        try {
            int result = browseHistoryMapper.deleteById(id);
            LogTracer.traceDatabaseQuery("DELETE", "删除浏览记录", id, result);
            
            boolean success = result > 0;
            LogTracer.traceMethod("BrowseHistoryService.deleteBrowseHistory", "删除浏览记录完成", success);
            LogTracer.tracePerformance("BrowseHistoryService.deleteBrowseHistory", startTime);
            
            return success;
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryService.deleteBrowseHistory", "删除浏览记录失败", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public boolean clearBrowseHistory(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryService.clearBrowseHistory", "开始清空浏览历史", userId);
        
        try {
            int result = browseHistoryMapper.deleteByUserId(userId);
            LogTracer.traceDatabaseQuery("DELETE", "清空用户浏览历史", userId, result);
            
            boolean success = result >= 0;
            LogTracer.traceMethod("BrowseHistoryService.clearBrowseHistory", "清空浏览历史完成", success);
            LogTracer.tracePerformance("BrowseHistoryService.clearBrowseHistory", startTime);
            
            return success;
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryService.clearBrowseHistory", "清空浏览历史失败", e);
            throw e;
        }
    }

    @Override
    public int countBrowseHistory(Long userId) {
        long startTime = System.currentTimeMillis();
        LogTracer.traceMethod("BrowseHistoryService.countBrowseHistory", "开始统计浏览历史数量", userId);
        
        try {
            int count = browseHistoryMapper.countByUserId(userId);
            LogTracer.traceDatabaseQuery("SELECT", "统计用户浏览历史数量", userId, count);
            
            LogTracer.traceMethod("BrowseHistoryService.countBrowseHistory", "统计浏览历史数量完成", count);
            LogTracer.tracePerformance("BrowseHistoryService.countBrowseHistory", startTime);
            
            return count;
        } catch (Exception e) {
            LogTracer.traceException("BrowseHistoryService.countBrowseHistory", "统计浏览历史数量失败", e);
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