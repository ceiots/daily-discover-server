package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.user.UserAccount;
import com.example.user.domain.model.id.UserId;
import com.example.user.infrastructure.persistence.entity.UserAccountEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户账户实体转换器
 */
@Component
public class UserAccountConverter {

    /**
     * 将领域模型转换为实体
     *
     * @param account 用户账户领域模型
     * @return 用户账户实体
     */
    public UserAccountEntity toEntity(UserAccount account) {
        if (account == null) {
            return null;
        }

        UserAccountEntity entity = new UserAccountEntity();
        if (account.getId() != null) {
            entity.setId(account.getId());
        }
        if (account.getUserId() != null) {
            entity.setUserId(account.getUserId().getValue());
        }
        entity.setBalance(account.getBalance());
        entity.setFreezeAmount(account.getFreezeAmount());
        entity.setPoints(account.getPoints());
        entity.setGrowthValue(account.getGrowthValue());
        entity.setStatus(account.getStatus());
        entity.setVersion(account.getVersion());
        entity.setCreateTime(account.getCreateTime());
        entity.setUpdateTime(account.getUpdateTime());
        
        return entity;
    }

    /**
     * 将实体转换为领域模型
     *
     * @param entity 用户账户实体
     * @return 用户账户领域模型
     */
    public UserAccount toDomain(UserAccountEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // 使用工厂方法创建对象
        UserAccount account = UserAccount.create(new UserId(entity.getUserId()));
        
        // 通过反射设置其他字段
        try {
            // 设置ID
            Field idField = UserAccount.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(account, entity.getId());
            
            // 设置余额和冻结金额
            Field balanceField = UserAccount.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(account, entity.getBalance());
            
            Field freezeAmountField = UserAccount.class.getDeclaredField("freezeAmount");
            freezeAmountField.setAccessible(true);
            freezeAmountField.set(account, entity.getFreezeAmount());
            
            // 设置积分和成长值
            Field pointsField = UserAccount.class.getDeclaredField("points");
            pointsField.setAccessible(true);
            pointsField.set(account, entity.getPoints());
            
            Field growthValueField = UserAccount.class.getDeclaredField("growthValue");
            growthValueField.setAccessible(true);
            growthValueField.set(account, entity.getGrowthValue());
            
            // 设置状态
            Field statusField = UserAccount.class.getDeclaredField("status");
            statusField.setAccessible(true);
            statusField.set(account, entity.getStatus());
            
            // 设置版本号
            Field versionField = UserAccount.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(account, entity.getVersion());
            
            // 设置创建时间和更新时间
            Field createTimeField = UserAccount.class.getDeclaredField("createTime");
            createTimeField.setAccessible(true);
            createTimeField.set(account, entity.getCreateTime());
            
            Field updateTimeField = UserAccount.class.getDeclaredField("updateTime");
            updateTimeField.setAccessible(true);
            updateTimeField.set(account, entity.getUpdateTime());
            
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to domain model", e);
        }
        
        return account;
    }
    
    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    public List<UserAccount> toDomainList(List<UserAccountEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        List<UserAccount> accounts = new ArrayList<>(entities.size());
        for (UserAccountEntity entity : entities) {
            accounts.add(toDomain(entity));
        }
        return accounts;
    }
    
    /**
     * 将领域模型列表转换为实体列表
     *
     * @param accounts 领域模型列表
     * @return 实体列表
     */
    public List<UserAccountEntity> toEntityList(List<UserAccount> accounts) {
        if (accounts == null) {
            return null;
        }
        
        List<UserAccountEntity> entities = new ArrayList<>(accounts.size());
        for (UserAccount account : accounts) {
            entities.add(toEntity(account));
        }
        return entities;
    }
} 