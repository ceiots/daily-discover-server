package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.UserInterestProfileMapper;
import com.dailydiscover.model.UserInterestProfile;
import com.dailydiscover.service.UserInterestProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserInterestProfileServiceImpl extends ServiceImpl<UserInterestProfileMapper, UserInterestProfile> implements UserInterestProfileService {
    
    @Autowired
    private UserInterestProfileMapper userInterestProfileMapper;
    
    @Override
    public UserInterestProfile getByUserId(Long userId) {
        return userInterestProfileMapper.findByUserId(userId);
    }
    
    @Override
    public boolean createOrUpdateInterestProfile(Long userId, String interestTags, 
                                                 String behaviorPatterns, String discoveryPreferences) {
        UserInterestProfile profile = getByUserId(userId);
        if (profile == null) {
            profile = new UserInterestProfile();
            profile.setUserId(userId);
        }
        
        profile.setInterestTags(interestTags);
        profile.setBehaviorPatterns(behaviorPatterns);
        profile.setDiscoveryPreferences(discoveryPreferences);
        profile.setLastUpdated(java.time.LocalDateTime.now());
        
        return saveOrUpdate(profile);
    }
    
    @Override
    public boolean updateInterestTags(Long userId, String interestTags) {
        UserInterestProfile profile = getByUserId(userId);
        if (profile != null) {
            profile.setInterestTags(interestTags);
            profile.setLastUpdated(java.time.LocalDateTime.now());
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public boolean updateBehaviorPatterns(Long userId, String behaviorPatterns) {
        UserInterestProfile profile = getByUserId(userId);
        if (profile != null) {
            profile.setBehaviorPatterns(behaviorPatterns);
            profile.setLastUpdated(java.time.LocalDateTime.now());
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public java.util.List<Long> getUsersByInterestTags(String interestTags, int limit) {
        // 这里可以添加更复杂的相似度匹配逻辑
        return userInterestProfileMapper.findUsersByInterestTags(interestTags, limit);
    }
}