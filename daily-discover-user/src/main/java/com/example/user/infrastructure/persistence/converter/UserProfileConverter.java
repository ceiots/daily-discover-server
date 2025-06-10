package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserProfile;
import com.example.user.infrastructure.persistence.entity.UserProfileEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户详情实体转换器
 */
@Component
public class UserProfileConverter {

    /**
     * 将领域模型转换为实体
     *
     * @param userProfile 用户详情领域模型
     * @return 用户详情实体
     */
    public UserProfileEntity toEntity(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }
        
        UserProfileEntity entity = new UserProfileEntity();
        
        if (userProfile.getId() != null) {
            entity.setId(userProfile.getId());
        }
        
        if (userProfile.getUserId() != null) {
            entity.setUserId(userProfile.getUserId().getValue());
        }
        
        entity.setNickname(userProfile.getNickname());
        entity.setRealName(userProfile.getRealName());
        entity.setGender(userProfile.getGender());
        entity.setBirthday(userProfile.getBirthday());
        entity.setAvatar(userProfile.getAvatar());
        entity.setCoverImage(userProfile.getCoverImage());
        entity.setBio(userProfile.getBio());
        
        return entity;
    }

    /**
     * 将实体转换为领域模型
     *
     * @param entity 用户详情实体
     * @return 用户详情领域模型
     */
    public UserProfile toDomain(UserProfileEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // 使用静态工厂方法创建UserProfile对象
        UserProfile userProfile = UserProfile.create(
            new UserId(entity.getUserId()),
            entity.getNickname()
        );
        
        // 设置ID
        userProfile.setId(entity.getId());
        
        // 设置其他属性
        userProfile.setRealName(entity.getRealName());
        userProfile.setGender(entity.getGender());
        userProfile.setBirthday(entity.getBirthday());
        userProfile.setAvatar(entity.getAvatar());
        userProfile.setCoverImage(entity.getCoverImage());
        userProfile.setBio(entity.getBio());
        
        return userProfile;
    }
    
    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    public List<UserProfile> toDomainList(List<UserProfileEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
    /**
     * 将领域模型列表转换为实体列表
     *
     * @param userProfiles 领域模型列表
     * @return 实体列表
     */
    public List<UserProfileEntity> toEntityList(List<UserProfile> userProfiles) {
        if (userProfiles == null) {
            return null;
        }
        
        return userProfiles.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
} 