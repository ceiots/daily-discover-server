package com.example.service.impl;

import com.example.service.UserInterestService;
import com.example.mapper.UserInterestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class UserInterestServiceImpl implements UserInterestService {

    @Autowired
    private UserInterestMapper userInterestMapper;
    
    @Override
    @Cacheable(value = "userColdStartCache", key = "#userId", unless = "#result == null")
    public boolean needColdStart(Long userId) {
        // 检查用户是否已经选择了兴趣标签
        Integer count = userInterestMapper.countUserInterests(userId);
        return count == null || count == 0;
    }
    
    @Override
    @Transactional
    public boolean saveUserInterests(Long userId, List<Long> tagIds) {
        if (userId == null || tagIds == null || tagIds.isEmpty()) {
            return false;
        }
        
        // 先删除用户现有的兴趣标签
        userInterestMapper.deleteUserInterests(userId);
        
        // 保存新的兴趣标签
        for (Long tagId : tagIds) {
            userInterestMapper.insertUserInterest(userId, tagId);
        }
        
        // 更新用户冷启动状态为已完成
        userInterestMapper.updateColdStartStatus(userId, true);
        
        return true;
    }
    
    @Override
    public boolean skipColdStart(Long userId) {
        if (userId == null) {
            return false;
        }
        
        // 更新用户冷启动状态为已完成，即使没有选择任何标签
        return userInterestMapper.updateColdStartStatus(userId, true) > 0;
    }
}