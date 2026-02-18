package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.PromotionActivityMapper;
import com.dailydiscover.model.PromotionActivity;
import com.dailydiscover.service.PromotionActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PromotionActivityServiceImpl extends ServiceImpl<PromotionActivityMapper, PromotionActivity> implements PromotionActivityService {
    
    @Autowired
    private PromotionActivityMapper promotionActivityMapper;
    
    @Override
    public PromotionActivity getByActivityName(String activityName) {
        return lambdaQuery().eq(PromotionActivity::getActivityName, activityName).one();
    }
    
    @Override
    public List<PromotionActivity> getActiveActivities() {
        return promotionActivityMapper.findActiveActivities();
    }
    
    @Override
    public List<PromotionActivity> getUpcomingActivities() {
        return promotionActivityMapper.findUpcomingActivities();
    }
    
    @Override
    public List<PromotionActivity> getExpiredActivities() {
        return lambdaQuery()
                .lt(PromotionActivity::getEndTime, LocalDateTime.now())
                .list();
    }
    
    @Override
    public List<PromotionActivity> getByActivityType(String activityType) {
        return promotionActivityMapper.findByActivityType(activityType);
    }
    
    @Override
    public PromotionActivity createActivity(String activityName, String activityType, BigDecimal discountAmount,
                                          BigDecimal minOrderAmount, LocalDateTime startTime, LocalDateTime endTime, String rules) {
        PromotionActivity activity = new PromotionActivity();
        activity.setActivityName(activityName);
        activity.setActivityType(activityType);
        activity.setDiscountAmount(discountAmount);
        activity.setMinOrderAmount(minOrderAmount);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setRules(rules);
        activity.setStatus("active");
        
        save(activity);
        return activity;
    }
    
    @Override
    public boolean updateActivityStatus(Long activityId, String status) {
        PromotionActivity activity = getById(activityId);
        if (activity != null) {
            activity.setStatus(status);
            return updateById(activity);
        }
        return false;
    }
    
    @Override
    public boolean validateActivity(Long activityId, BigDecimal orderAmount) {
        PromotionActivity activity = getById(activityId);
        if (activity == null || !"active".equals(activity.getStatus())) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(activity.getStartTime()) || now.isAfter(activity.getEndTime())) {
            return false;
        }
        
        return orderAmount.compareTo(activity.getMinOrderAmount()) >= 0;
    }
    
    @Override
    public Map<String, Object> getActivityStats() {
        // 实现统计逻辑
        return Map.of(
            "total", count(),
            "active", lambdaQuery().eq(PromotionActivity::getStatus, "active").count(),
            "expired", lambdaQuery().lt(PromotionActivity::getEndTime, LocalDateTime.now()).count()
        );
    }
}