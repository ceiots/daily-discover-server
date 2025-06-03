package com.example.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.UserBehaviorDao;
import com.example.dao.UserPreferenceDao;
import com.example.model.UserBehavior;
import com.example.model.UserPreference;
import com.example.service.UserBehaviorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserBehaviorServiceImpl implements UserBehaviorService {

    @Autowired
    private UserBehaviorDao userBehaviorDao;
    
    @Autowired
    private UserPreferenceDao userPreferenceDao;
    
    @Override
    @Transactional
    public void recordBehavior(UserBehavior behavior) {
        // 设置行为时间
        if (behavior.getBehaviorTime() == null) {
            behavior.setBehaviorTime(new Date());
        }
        
        // 设置创建和更新时间
        behavior.setCreatedAt(new Date());
        behavior.setUpdatedAt(new Date());
        
        // 根据行为类型计算行为得分
        if (behavior.getBehaviorScore() == null) {
            behavior.setBehaviorScore(calculateBehaviorScore(behavior.getBehaviorType(), behavior.getDuration()));
        }
        
        // 保存行为记录
        userBehaviorDao.insert(behavior);
    }
    
    @Override
    public void recordViewBehavior(Long userId, Long productId, Long categoryId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setCategoryId(categoryId);
        behavior.setBehaviorType("VIEW");
        behavior.setBehaviorScore(1.0); // 浏览行为得分为1
        recordBehavior(behavior);
    }
    
    @Override
    public void recordClickBehavior(Long userId, Long productId, Long categoryId, String extraData) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setCategoryId(categoryId);
        behavior.setBehaviorType("CLICK");
        behavior.setExtraData(extraData);
        behavior.setBehaviorScore(2.0); // 点击行为得分为2
        recordBehavior(behavior);
    }
    
    @Override
    public void recordStayBehavior(Long userId, Long productId, Long categoryId, Double duration) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setCategoryId(categoryId);
        behavior.setBehaviorType("STAY");
        behavior.setDuration(duration);
        // 停留行为得分：每10秒0.5分
        double score = Math.round(duration / 10.0 * 0.5 * 10) / 10.0;
        behavior.setBehaviorScore(score);
        recordBehavior(behavior);
    }
    
    @Override
    public void recordBuyBehavior(Long userId, Long productId, Long categoryId, String extraData) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setCategoryId(categoryId);
        behavior.setBehaviorType("BUY");
        behavior.setExtraData(extraData);
        behavior.setBehaviorScore(10.0); // 购买行为得分为10
        recordBehavior(behavior);
    }
    
    @Override
    public void recordFavoriteBehavior(Long userId, Long productId, Long categoryId) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setCategoryId(categoryId);
        behavior.setBehaviorType("FAVORITE");
        behavior.setBehaviorScore(5.0); // 收藏行为得分为5
        recordBehavior(behavior);
    }
    
    @Override
    public void recordCommentBehavior(Long userId, Long productId, Long categoryId, String extraData) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setCategoryId(categoryId);
        behavior.setBehaviorType("COMMENT");
        behavior.setExtraData(extraData);
        behavior.setBehaviorScore(5.0); // 评价行为得分为5
        recordBehavior(behavior);
    }
    
    @Override
    public void recordShareBehavior(Long userId, Long productId, Long categoryId, String extraData) {
        UserBehavior behavior = new UserBehavior();
        behavior.setUserId(userId);
        behavior.setProductId(productId);
        behavior.setCategoryId(categoryId);
        behavior.setBehaviorType("SHARE");
        behavior.setExtraData(extraData);
        behavior.setBehaviorScore(8.0); // 分享行为得分为8
        recordBehavior(behavior);
    }
    
    @Override
    public List<UserBehavior> getUserBehaviorHistory(Long userId, int limit) {
        return userBehaviorDao.findByUserId(userId, limit);
    }
    
    @Override
    public List<UserBehavior> getUserBehaviorHistoryByType(Long userId, String behaviorType, int limit) {
        return userBehaviorDao.findByUserIdAndBehaviorType(userId, behaviorType, limit);
    }
    
    @Override
    public List<UserBehavior> getUserBehaviorHistoryByTimeRange(Long userId, Date startTime, Date endTime) {
        return userBehaviorDao.findByUserIdAndTimeRange(userId, startTime, endTime);
    }
    
    @Override
    @Transactional
    public void updateUserPreferences(Long userId) {
        // 获取用户所有行为记录
        List<UserBehavior> behaviors = userBehaviorDao.findByUserId(userId, 1000);
        
        // 按类别分组并计算每个类别的行为得分总和
        Map<Long, Double> categoryScores = behaviors.stream()
            .filter(b -> b.getCategoryId() != null)
            .collect(Collectors.groupingBy(
                UserBehavior::getCategoryId,
                Collectors.summingDouble(UserBehavior::getBehaviorScore)
            ));
        
        // 更新用户偏好
        categoryScores.forEach((categoryId, score) -> {
            // 根据行为得分计算偏好等级 (1-5)
            int preferenceLevel = calculatePreferenceLevel(score);
            
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
     * 根据行为类型和持续时间计算行为得分
     */
    private Double calculateBehaviorScore(String behaviorType, Double duration) {
        if (behaviorType == null) {
            return 0.0;
        }
        
        switch (behaviorType) {
            case "VIEW":
                return 1.0;
            case "CLICK":
                return 2.0;
            case "STAY":
                if (duration != null) {
                    return Math.round(duration / 10.0 * 0.5 * 10) / 10.0;
                }
                return 0.5;
            case "BUY":
                return 10.0;
            case "FAVORITE":
                return 5.0;
            case "COMMENT":
                return 5.0;
            case "SHARE":
                return 8.0;
            default:
                return 0.0;
        }
    }
    
    /**
     * 根据行为得分计算偏好等级
     * 0-10分: 1级
     * 11-30分: 2级
     * 31-60分: 3级
     * 61-100分: 4级
     * 100+分: 5级
     */
    private int calculatePreferenceLevel(Double score) {
        if (score <= 10) return 1;
        if (score <= 30) return 2;
        if (score <= 60) return 3;
        if (score <= 100) return 4;
        return 5;
    }
} 