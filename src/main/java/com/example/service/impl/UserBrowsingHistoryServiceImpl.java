package com.example.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.UserBrowsingHistoryDao;
import com.example.dao.UserPreferenceDao;
import com.example.model.UserBrowsingHistory;
import com.example.model.UserPreference;
import com.example.service.UserBrowsingHistoryService;

@Service
public class UserBrowsingHistoryServiceImpl implements UserBrowsingHistoryService {

    @Autowired
    private UserBrowsingHistoryDao userBrowsingHistoryDao;
    
    @Autowired
    private UserPreferenceDao userPreferenceDao;
    
    @Override
    @Transactional
    public void recordBrowsing(UserBrowsingHistory browsingHistory) {
        // 设置创建时间
        if (browsingHistory.getCreatedAt() == null) {
            browsingHistory.setCreatedAt(new Date());
        }
        
        // 设置浏览时间
        if (browsingHistory.getBrowseTime() == null) {
            browsingHistory.setBrowseTime(new Date());
        }
        
        // 检查是否已存在该用户对该商品的浏览记录
        UserBrowsingHistory existingRecord = userBrowsingHistoryDao.findByUserIdAndProductId(
            browsingHistory.getUserId(), 
            browsingHistory.getProductId()
        );
        
        if (existingRecord != null) {
            // 更新现有记录
            existingRecord.setBrowseTime(browsingHistory.getBrowseTime());
            existingRecord.setBrowseCount(existingRecord.getBrowseCount() + 1);
            existingRecord.setUpdatedAt(new Date());
            userBrowsingHistoryDao.update(existingRecord);
        } else {
            // 创建新记录
            browsingHistory.setBrowseCount(1);
            browsingHistory.setUpdatedAt(new Date());
            userBrowsingHistoryDao.insert(browsingHistory);
        }
    }
    
    @Override
    @Transactional
    public void updateUserPreferences(Long userId) {
        // 获取用户最近的浏览历史
        List<UserBrowsingHistory> histories = userBrowsingHistoryDao.findByUserId(userId);
        
        // 按类别分组并计算每个类别的浏览次数
        Map<Long, Integer> categoryBrowseCounts = histories.stream()
            .collect(Collectors.groupingBy(
                UserBrowsingHistory::getCategoryId,
                Collectors.summingInt(UserBrowsingHistory::getBrowseCount)
            ));
        
        // 更新用户偏好
        categoryBrowseCounts.forEach((categoryId, count) -> {
            // 根据浏览次数计算偏好等级 (1-5)
            int preferenceLevel = calculatePreferenceLevel(count);
            
            // 查找现有偏好
            List<UserPreference> existingPreferences = userPreferenceDao.findByUserIdAndCategoryId(userId, categoryId);
            
            if (!existingPreferences.isEmpty()) {
                // 更新现有偏好
                UserPreference preference = existingPreferences.get(0);
                preference.setPreferenceLevel(preferenceLevel);
                preference.setUpdatedAt(new Date());
                userPreferenceDao.update(preference);
            } else {
                // 创建新偏好
                UserPreference preference = new UserPreference();
                preference.setUserId(userId);
                preference.setCategoryId(categoryId);
                preference.setPreferenceLevel(preferenceLevel);
                preference.setCreatedAt(new Date());
                preference.setUpdatedAt(new Date());
                userPreferenceDao.insert(preference);
            }
        });
    }
    
    /**
     * 根据浏览次数计算偏好等级
     * 1-5次: 1级
     * 6-10次: 2级
     * 11-20次: 3级
     * 21-30次: 4级
     * 31+次: 5级
     */
    private int calculatePreferenceLevel(int browseCount) {
        if (browseCount <= 5) return 1;
        if (browseCount <= 10) return 2;
        if (browseCount <= 20) return 3;
        if (browseCount <= 30) return 4;
        return 5;
    }
} 