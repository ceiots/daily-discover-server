package com.example.user.application.assembler;

import com.example.user.application.dto.UserRelationshipDTO;
import com.example.user.domain.model.UserRelationship;
import com.example.user.domain.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
    @Mapping(source = "userId", target = "userId", qualifiedByName = "userIdToLong")
    @Mapping(source = "relatedUserId", target = "relatedUserId", qualifiedByName = "userIdToLong")
    UserRelationshipDTO toDTO(UserRelationship userRelationship);

    /**
     * 将DTO转换为领域模型
     *
     * @param userRelationshipDTO 用户关系DTO
     * @return 用户关系领域模型
     */
    @Mapping(source = "userId", target = "userId", qualifiedByName = "longToUserId")
    @Mapping(source = "relatedUserId", target = "relatedUserId", qualifiedByName = "longToUserId")
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
    
    /**
     * 将UserId转换为Long
     */
    @Named("userIdToLong")
    default Long userIdToLong(UserId userId) {
        return userId != null ? userId.getValue() : null;
    }

    /**
     * 将Long转换为UserId
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
}