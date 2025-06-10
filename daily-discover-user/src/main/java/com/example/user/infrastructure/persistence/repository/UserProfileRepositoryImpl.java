package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserProfile;
import com.example.user.domain.repository.UserProfileRepository;
import com.example.user.infrastructure.persistence.entity.UserProfileEntity;
import com.example.user.infrastructure.persistence.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户个人资料仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final UserProfileMapper userProfileMapper;

    @Override
    public Optional<UserProfile> findByUserId(Long userId) {
        LambdaQueryWrapper<UserProfileEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfileEntity::getUserId, userId);
        UserProfileEntity entity = userProfileMapper.selectOne(wrapper);
        
        if (entity == null) {
            return Optional.empty();
        }
        
        UserProfile profile = UserProfile.create(new UserId(entity.getUserId()), entity.getNickname());
        profile.setId(entity.getId());
        profile.setRealName(entity.getRealName());
        profile.setGender(entity.getGender());
        profile.setBirthday(entity.getBirthday());
        profile.setBio(entity.getBio());
        profile.setAvatar(entity.getAvatar());
        profile.setCoverImage(entity.getCoverImage());
        profile.setUpdateTime(entity.getUpdateTime());
        
        return Optional.of(profile);
    }

    @Override
    public UserProfile save(UserProfile userProfile) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.setUserId(userProfile.getUserId().getValue());
        entity.setNickname(userProfile.getNickname());
        entity.setRealName(userProfile.getRealName());
        entity.setGender(userProfile.getGender());
        entity.setBirthday(userProfile.getBirthday());
        entity.setBio(userProfile.getBio());
        entity.setAvatar(userProfile.getAvatar());
        entity.setCoverImage(userProfile.getCoverImage());
        
        userProfileMapper.insert(entity);
        userProfile.setId(entity.getId());
        
        return userProfile;
    }

    @Override
    public UserProfile update(UserProfile userProfile) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.setId(userProfile.getId());
        entity.setUserId(userProfile.getUserId().getValue());
        entity.setNickname(userProfile.getNickname());
        entity.setRealName(userProfile.getRealName());
        entity.setGender(userProfile.getGender());
        entity.setBirthday(userProfile.getBirthday());
        entity.setBio(userProfile.getBio());
        entity.setAvatar(userProfile.getAvatar());
        entity.setCoverImage(userProfile.getCoverImage());
        
        userProfileMapper.updateById(entity);
        
        return userProfile;
    }

    @Override
    public boolean delete(Long id) {
        return userProfileMapper.deleteById(id) > 0;
    }
} 