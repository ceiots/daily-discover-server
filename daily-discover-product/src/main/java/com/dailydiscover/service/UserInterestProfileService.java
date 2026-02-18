package com.dailydiscover.service;

import com.dailydiscover.model.UserInterestProfile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户兴趣画像服务接口
 */
public interface UserInterestProfileService extends IService<UserInterestProfile> {
    
    /**
     * 根据用户ID查询兴趣画像
     * @param userId 用户ID
     * @return 兴趣画像
     */
    UserInterestProfile getByUserId(Long userId);
    
    /**
     * 创建或更新用户兴趣画像
     * @param userId 用户ID
     * @param interestTags 兴趣标签
     * @param behaviorPatterns 行为模式
     * @param discoveryPreferences 发现偏好
     * @return 是否成功
     */
    boolean createOrUpdateInterestProfile(Long userId, String interestTags, 
                                         String behaviorPatterns, String discoveryPreferences);
    
    /**
     * 更新用户兴趣标签
     * @param userId 用户ID
     * @param interestTags 兴趣标签
     * @return 是否成功
     */
    boolean updateInterestTags(Long userId, String interestTags);
    
    /**
     * 更新用户行为模式
     * @param userId 用户ID
     * @param behaviorPatterns 行为模式
     * @return 是否成功
     */
    boolean updateBehaviorPatterns(Long userId, String behaviorPatterns);
    
    /**
     * 根据兴趣标签推荐用户
     * @param interestTags 兴趣标签
     * @param limit 限制数量
     * @return 用户ID列表
     */
    java.util.List<Long> getUsersByInterestTags(String interestTags, int limit);
}