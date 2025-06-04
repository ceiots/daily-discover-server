package com.example.service;

import java.util.List;

public interface UserInterestService {
    /**
     * 检查用户是否需要冷启动推荐
     * @param userId 用户ID
     * @return 是否需要冷启动
     */
    boolean needColdStart(Long userId);
    
    /**
     * 保存用户兴趣标签
     * @param userId 用户ID
     * @param tagIds 标签ID列表
     * @return 是否保存成功
     */
    boolean saveUserInterests(Long userId, List<Long> tagIds);
    
    /**
     * 跳过冷启动流程
     * @param userId 用户ID
     * @return 是否操作成功
     */
    boolean skipColdStart(Long userId);
}