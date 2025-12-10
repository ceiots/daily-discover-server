package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dailydiscover.dto.BrowseHistoryResponse;
import com.dailydiscover.entity.BrowseHistory;
import com.dailydiscover.mapper.BrowseHistoryMapper;
import com.dailydiscover.service.BrowseHistoryService;
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
        browseHistory.setViewedAt(LocalDateTime.now());
        browseHistory.setCreatedAt(LocalDateTime.now());
        
        int result = browseHistoryMapper.insert(browseHistory);
        if (result <= 0) {
            throw new RuntimeException("添加浏览记录失败");
        }
        
        return convertToResponse(browseHistory);
    }

    @Override
    public List<BrowseHistoryResponse> getBrowseHistoryByUserId(Long userId) {
        QueryWrapper<BrowseHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("viewed_at");
        
        List<BrowseHistory> browseHistories = browseHistoryMapper.selectList(queryWrapper);
        return browseHistories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteBrowseHistory(Long id) {
        int result = browseHistoryMapper.deleteById(id);
        return result > 0;
    }

    @Override
    @Transactional
    public boolean clearBrowseHistory(Long userId) {
        int result = browseHistoryMapper.deleteByUserId(userId);
        return result >= 0;
    }

    @Override
    public int countBrowseHistory(Long userId) {
        return browseHistoryMapper.countByUserId(userId);
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