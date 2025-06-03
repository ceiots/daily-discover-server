package com.example.service;

import com.example.model.UserBrowsingHistory;

/**
 * 用户浏览历史服务接口
 */
public interface UserBrowsingHistoryService {
    
    /**
     * 记录用户浏览商品
     * @param browsingHistory 浏览历史记录
     */
    void recordBrowsing(UserBrowsingHistory browsingHistory);
    
    /**
     * 更新用户偏好
     * 根据浏览历史自动更新用户偏好
     * @param userId 用户ID
     */
    void updateUserPreferences(Long userId);
} 