package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.user.UserAccountLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.infrastructure.persistence.entity.UserAccountLogEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户账户流水实体转换器
 */
@Component
public class UserAccountLogConverter {

    /**
     * 将领域模型转换为实体
     *
     * @param log 用户账户流水领域模型
     * @return 用户账户流水实体
     */
    public UserAccountLogEntity toEntity(UserAccountLog log) {
        if (log == null) {
            return null;
        }

        UserAccountLogEntity entity = new UserAccountLogEntity();
        if (log.getId() != null) {
            entity.setId(log.getId());
        }
        if (log.getUserId() != null) {
            entity.setUserId(log.getUserId().getValue());
        }
        entity.setType(log.getType());
        entity.setAmount(log.getAmount());
        entity.setPoints(log.getPoints());
        entity.setGrowth(log.getGrowth());
        entity.setBeforeAmount(log.getBeforeAmount());
        entity.setAfterAmount(log.getAfterAmount());
        entity.setSource(log.getSource());
        entity.setSourceId(log.getSourceId());
        entity.setRemark(log.getRemark());
        entity.setCreateTime(log.getCreateTime());
        
        return entity;
    }

    /**
     * 将实体转换为领域模型
     *
     * @param entity 用户账户流水实体
     * @return 用户账户流水领域模型
     */
    public UserAccountLog toDomain(UserAccountLogEntity entity) {
        if (entity == null) {
            return null;
        }
        
        // 创建领域对象
        UserAccountLog log = new UserAccountLog();
        
        try {
            // 使用反射设置私有字段
            Field idField = UserAccountLog.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(log, entity.getId());
            
            Field userIdField = UserAccountLog.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(log, new UserId(entity.getUserId()));
            
            Field typeField = UserAccountLog.class.getDeclaredField("type");
            typeField.setAccessible(true);
            typeField.set(log, entity.getType());
            
            Field amountField = UserAccountLog.class.getDeclaredField("amount");
            amountField.setAccessible(true);
            amountField.set(log, entity.getAmount());
            
            Field pointsField = UserAccountLog.class.getDeclaredField("points");
            pointsField.setAccessible(true);
            pointsField.set(log, entity.getPoints());
            
            Field growthField = UserAccountLog.class.getDeclaredField("growth");
            growthField.setAccessible(true);
            growthField.set(log, entity.getGrowth());
            
            Field beforeAmountField = UserAccountLog.class.getDeclaredField("beforeAmount");
            beforeAmountField.setAccessible(true);
            beforeAmountField.set(log, entity.getBeforeAmount());
            
            Field afterAmountField = UserAccountLog.class.getDeclaredField("afterAmount");
            afterAmountField.setAccessible(true);
            afterAmountField.set(log, entity.getAfterAmount());
            
            Field sourceField = UserAccountLog.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            sourceField.set(log, entity.getSource());
            
            Field sourceIdField = UserAccountLog.class.getDeclaredField("sourceId");
            sourceIdField.setAccessible(true);
            sourceIdField.set(log, entity.getSourceId());
            
            Field remarkField = UserAccountLog.class.getDeclaredField("remark");
            remarkField.setAccessible(true);
            remarkField.set(log, entity.getRemark());
            
            Field createTimeField = UserAccountLog.class.getDeclaredField("createTime");
            createTimeField.setAccessible(true);
            createTimeField.set(log, entity.getCreateTime());
            
        } catch (Exception e) {
            throw new RuntimeException("Error converting entity to domain model", e);
        }
        
        return log;
    }
    
    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    public List<UserAccountLog> toDomainList(List<UserAccountLogEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        List<UserAccountLog> logs = new ArrayList<>(entities.size());
        for (UserAccountLogEntity entity : entities) {
            logs.add(toDomain(entity));
        }
        return logs;
    }
    
    /**
     * 将领域模型列表转换为实体列表
     *
     * @param logs 领域模型列表
     * @return 实体列表
     */
    public List<UserAccountLogEntity> toEntityList(List<UserAccountLog> logs) {
        if (logs == null) {
            return null;
        }
        
        List<UserAccountLogEntity> entities = new ArrayList<>(logs.size());
        for (UserAccountLog log : logs) {
            entities.add(toEntity(log));
        }
        return entities;
    }
} 