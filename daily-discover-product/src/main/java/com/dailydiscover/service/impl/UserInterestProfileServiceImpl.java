package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.UserInterestProfileMapper;
import com.dailydiscover.model.UserInterestProfile;
import com.dailydiscover.service.UserInterestProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public UserInterestProfile createProfile(Long userId, String interests, String preferences) {
        UserInterestProfile profile = new UserInterestProfile();
        profile.setUserId(userId);
        profile.setInterests(interests);
        profile.setPreferences(preferences);
        
        save(profile);
        return profile;
    }
    
    @Override
    public boolean updateInterests(Long userId, String interests) {
        UserInterestProfile profile = getByUserId(userId);
        if (profile != null) {
            profile.setInterests(interests);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public boolean updatePreferences(Long userId, String preferences) {
        UserInterestProfile profile = getByUserId(userId);
        if (profile != null) {
            profile.setPreferences(preferences);
            return updateById(profile);
        }
        return false;
    }
    
    @Override
    public List<UserInterestProfile> getSimilarProfiles(Long userId, Integer limit) {
        UserInterestProfile currentProfile = getByUserId(userId);
        if (currentProfile == null || currentProfile.getInterests() == null) {
            return java.util.Collections.emptyList();
        }
        
        // 这里可以添加更复杂的相似度匹配逻辑
        return lambdaQuery()
                .ne(UserInterestProfile::getUserId, userId)
                .like(UserInterestProfile::getInterests, currentProfile.getInterests().split(",")[0])
                .last("LIMIT " + limit)
                .list();
    }
}