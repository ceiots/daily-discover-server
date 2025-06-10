package com.example.user.application.assembler;

import com.example.user.application.dto.UserRelationshipDTO;
import com.example.user.domain.model.UserRelationship;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户关系数据转换器
 */
@Mapper(componentModel = "spring")
public interface UserRelationshipAssembler {

    UserRelationshipAssembler INSTANCE = Mappers.getMapper(UserRelationshipAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userRelationship 用户关系领域模型
     * @return 用户关系DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "relatedUserId.value", target = "relatedUserId")
    UserRelationshipDTO toDTO(UserRelationship userRelationship);

    /**
     * 将DTO转换为领域模型
     *
     * @param userRelationshipDTO 用户关系DTO
     * @return 用户关系领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    @Mapping(target = "relatedUserId.value", source = "relatedUserId")
    UserRelationship toDomain(UserRelationshipDTO userRelationshipDTO);

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userRelationshipList 用户关系领域模型列表
     * @return 用户关系DTO列表
     */
    List<UserRelationshipDTO> toDTO(List<UserRelationship> userRelationshipList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userRelationshipDTOList 用户关系DTO列表
     * @return 用户关系领域模型列表
     */
    List<UserRelationship> toDomain(List<UserRelationshipDTO> userRelationshipDTOList);
}