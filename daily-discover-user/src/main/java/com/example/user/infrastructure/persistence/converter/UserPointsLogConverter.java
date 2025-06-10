package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.infrastructure.persistence.entity.UserPointsLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

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
    @Mapping(target = "userId", expression = "java(new UserId(entity.getUserId()))")
    UserPointsLog toDomain(UserPointsLogEntity entity);
} 