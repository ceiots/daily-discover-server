package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.member.MemberLevel;
import com.example.user.infrastructure.persistence.entity.MemberLevelEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 会员等级实体转换器
 */
@Mapper(componentModel = "spring")
public interface MemberLevelConverter {

    MemberLevelConverter INSTANCE = Mappers.getMapper(MemberLevelConverter.class);

    /**
     * 将领域模型转换为实体
     *
     * @param memberLevel 会员等级领域模型
     * @return 会员等级实体
     */
    MemberLevelEntity toEntity(MemberLevel memberLevel);

    /**
     * 将实体转换为领域模型
     *
     * @param entity 会员等级实体
     * @return 会员等级领域模型
     */
    MemberLevel toDomain(MemberLevelEntity entity);
} 