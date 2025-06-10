package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.UserAuth;
import com.example.user.infrastructure.persistence.entity.UserAuthEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户授权信息转换器
 */
@Component
public class UserAuthConverter {

    /**
     * 将领域模型转换为实体
     *
     * @param userAuth 用户授权信息领域模型
     * @return 用户授权信息实体
     */
    public UserAuthEntity toEntity(UserAuth userAuth) {
        if (userAuth == null) {
            return null;
        }

        UserAuthEntity entity = new UserAuthEntity();
        entity.setId(userAuth.getId());
        entity.setUserId(userAuth.getUserId().getValue());
        entity.setIdentityType(userAuth.getIdentityType());
        entity.setIdentifier(userAuth.getIdentifier());
        entity.setCredential(userAuth.getCredential());
        entity.setVerified(userAuth.getVerified());
        entity.setStatus(userAuth.getStatus());
        entity.setExpireTime(userAuth.getExpireTime());
        entity.setBindTime(userAuth.getBindTime());
        entity.setCreateTime(userAuth.getCreateTime());
        entity.setUpdateTime(userAuth.getUpdateTime());

        return entity;
    }

    /**
     * 将实体转换为领域模型
     *
     * @param entity 用户授权信息实体
     * @return 用户授权信息领域模型
     */
    public UserAuth toDomain(UserAuthEntity entity) {
        if (entity == null) {
            return null;
        }

        UserAuth userAuth = UserAuth.create(
                new UserId(entity.getUserId()),
                entity.getIdentityType(),
                entity.getIdentifier(),
                entity.getCredential()
        );

        // 设置ID和其他属性
        userAuth.setId(entity.getId());
        userAuth.setVerified(entity.getVerified());
        userAuth.setStatus(entity.getStatus());
        userAuth.setExpireTime(entity.getExpireTime());
        userAuth.setUpdateTime(entity.getUpdateTime());

        return userAuth;
    }

    /**
     * 将领域模型列表转换为实体列表
     *
     * @param userAuths 用户授权信息领域模型列表
     * @return 用户授权信息实体列表
     */
    public List<UserAuthEntity> toEntityList(List<UserAuth> userAuths) {
        if (userAuths == null) {
            return new ArrayList<>();
        }
        return userAuths.stream().map(this::toEntity).collect(Collectors.toList());
    }

    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 用户授权信息实体列表
     * @return 用户授权信息领域模型列表
     */
    public List<UserAuth> toDomainList(List<UserAuthEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
} 