package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.infrastructure.persistence.entity.UserPointsLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户积分记录实体转换器
 */
@Mapper(componentModel = "spring")
public interface UserPointsLogConverter {

    UserPointsLogConverter INSTANCE = Mappers.getMapper(UserPointsLogConverter.class);

    /**
     * 将领域模型转换为实体
     *
     * @param userPointsLog 用户积分记录领域模型
     * @return 用户积分记录实体
     */
    @Mapping(target = "userId", source = "userId.value")
    UserPointsLogEntity toEntity(UserPointsLog userPointsLog);

    /**
     * 将实体转换为领域模型
     *
     * @param entity 用户积分记录实体
     * @return 用户积分记录领域模型
     */
    default UserPointsLog toDomain(UserPointsLogEntity entity) {
        return manualToDomain(entity);
    }
    
    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    default List<UserPointsLog> toDomainList(List<UserPointsLogEntity> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::manualToDomain)
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 将领域模型列表转换为实体列表
     *
     * @param pointsLogs 领域模型列表
     * @return 实体列表
     */
    List<UserPointsLogEntity> toEntityList(List<UserPointsLog> pointsLogs);
    
    /**
     * 手动实现的领域模型转换方法
     *
     * @param entity 用户积分记录实体
     * @return 用户积分记录领域模型
     */
    default UserPointsLog manualToDomain(UserPointsLogEntity entity) {
        if (entity == null) {
            return null;
        }
        
        UserId userId = new UserId(entity.getUserId());
        
        UserPointsLog pointsLog;
        if (entity.getType() == 1) {
            // 获取积分
            pointsLog = UserPointsLog.createGainLog(
                    userId,
                    entity.getPoints(),
                    entity.getBeforePoints(),
                    entity.getAfterPoints(),
                    entity.getSource(),
                    entity.getSourceId(),
                    entity.getDescription()
            );
        } else if (entity.getType() == 2) {
            // 使用积分
            pointsLog = UserPointsLog.createUseLog(
                    userId,
                    entity.getPoints(),
                    entity.getBeforePoints(),
                    entity.getAfterPoints(),
                    entity.getSource(),
                    entity.getSourceId(),
                    entity.getDescription()
            );
        } else if (entity.getType() == 3) {
            // 积分过期
            pointsLog = UserPointsLog.createExpireLog(
                    userId,
                    entity.getPoints(),
                    entity.getBeforePoints(),
                    entity.getAfterPoints(),
                    entity.getDescription()
            );
        } else {
            // 调整积分
            pointsLog = UserPointsLog.createAdjustLog(
                    userId,
                    entity.getPoints(),
                    entity.getBeforePoints(),
                    entity.getAfterPoints(),
                    entity.getDescription()
            );
        }
        
        pointsLog.setId(entity.getId());
        if (entity.getExpireTime() != null) {
            pointsLog.setExpireTime(entity.getExpireTime());
        }
        
        return pointsLog;
    }
} 