package com.example.user.application.assembler;

import com.example.user.application.dto.UserBehaviorDTO;
import com.example.user.domain.model.UserBehavior;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户行为数据转换器
 */
@Mapper(componentModel = "spring")
public interface UserBehaviorAssembler {

    UserBehaviorAssembler INSTANCE = Mappers.getMapper(UserBehaviorAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userBehavior 用户行为领域模型
     * @return 用户行为DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserBehaviorDTO toDTO(UserBehavior userBehavior);

    /**
     * 将DTO转换为领域模型
     *
     * @param userBehaviorDTO 用户行为DTO
     * @return 用户行为领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    UserBehavior toDomain(UserBehaviorDTO userBehaviorDTO);

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userBehaviorList 用户行为领域模型列表
     * @return 用户行为DTO列表
     */
    List<UserBehaviorDTO> toDTO(List<UserBehavior> userBehaviorList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userBehaviorDTOList 用户行为DTO列表
     * @return 用户行为领域模型列表
     */
    List<UserBehavior> toDomain(List<UserBehaviorDTO> userBehaviorDTOList);
}