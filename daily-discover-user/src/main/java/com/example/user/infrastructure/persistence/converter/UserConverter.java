package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.User;
import com.example.user.domain.model.UserRole;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.valueobject.Password;
import com.example.user.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体转换器
 */
@Component
public class UserConverter {

    /**
     * 将领域模型转换为实体
     *
     * @param user 用户领域模型
     * @return 用户实体
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        if (user.getId() != null) {
            entity.setId(user.getId().getValue());
        }
        entity.setUsername(user.getUsername());
        if (user.getPassword() != null) {
            entity.setPassword(user.getPassword().getEncodedValue());
            entity.setSalt(""); // 假设盐值存储在Password对象中或不需要单独存储
        }
        entity.setNickname(user.getNickname());
        if (user.getMobile() != null) {
            entity.setMobile(user.getMobile().getValue());
        }
        if (user.getEmail() != null) {
            entity.setEmail(user.getEmail().getValue());
        }
        entity.setAvatar(user.getAvatar());
        entity.setGender(user.getGender());
        entity.setStatus(user.getStatus());
        entity.setUserType(user.getUserType());
        entity.setLastLoginTime(user.getLastLoginTime());
        entity.setLastLoginIp(user.getLastLoginIp());
        entity.setRegisterTime(user.getCreateTime());
        entity.setCreateTime(user.getCreateTime());
        entity.setUpdateTime(user.getUpdateTime());
        entity.setVersion(user.getVersion());
        
        return entity;
    }

    /**
     * 将实体转换为领域模型
     *
     * @param entity 用户实体
     * @return 用户领域模型
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // 使用User.create工厂方法创建User对象
        // 注意：这里我们不传递原始密码，因为从数据库中获取的是已加密的密码
        User user = User.create(
            entity.getUsername(),
            null,
            entity.getNickname()
        );
        
        // 通过反射设置ID字段
        try {
            java.lang.reflect.Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, new UserId(entity.getId()));
            
            // 设置其他不可变字段
            if (entity.getPassword() != null) {
                java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
                passwordField.setAccessible(true);
                passwordField.set(user, Password.ofEncoded(entity.getPassword()));
            }
            
            // 设置创建时间和更新时间
            java.lang.reflect.Field createTimeField = User.class.getDeclaredField("createTime");
            createTimeField.setAccessible(true);
            createTimeField.set(user, entity.getCreateTime());
            
            java.lang.reflect.Field updateTimeField = User.class.getDeclaredField("updateTime");
            updateTimeField.setAccessible(true);
            updateTimeField.set(user, entity.getUpdateTime());
            
            // 设置版本号
            java.lang.reflect.Field versionField = User.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(user, entity.getVersion());
            
            // 设置删除标志
            java.lang.reflect.Field deletedField = User.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(user, 0); // 假设从数据库获取的用户都是未删除的
            
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to domain model", e);
        }
        
        // 使用领域模型提供的方法设置其他字段
        if (entity.getMobile() != null) {
            user.changeMobile(Mobile.of(entity.getMobile()));
        }
        
        if (entity.getEmail() != null) {
            user.changeEmail(Email.of(entity.getEmail()));
        }
        
        user.updateProfile(entity.getNickname(), entity.getAvatar(), entity.getGender());
        
        // 设置用户角色（如果有）
        // 注意：这里假设角色信息需要从其他地方获取
        
        return user;
    }
    
    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    public List<User> toDomainList(List<UserEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        List<User> users = new ArrayList<>(entities.size());
        for (UserEntity entity : entities) {
            users.add(toDomain(entity));
        }
        return users;
    }
    
    /**
     * 将领域模型列表转换为实体列表
     *
     * @param users 领域模型列表
     * @return 实体列表
     */
    public List<UserEntity> toEntityList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        List<UserEntity> entities = new ArrayList<>(users.size());
        for (User user : users) {
            entities.add(toEntity(user));
        }
        return entities;
    }
} 