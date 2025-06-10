package com.example.user.application.assembler;

import com.example.user.application.dto.UserRelationshipDTO;
import com.example.user.domain.model.UserRelationship;
import com.example.user.domain.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

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
    default UserRelationship toDomain(UserRelationshipDTO dto) {
        if (dto == null) {
            return null;
        }
        
        UserId userId = longToUserId(dto.getUserId());
        UserId relatedUserId = longToUserId(dto.getRelatedUserId());
        UserRelationship relationship = UserRelationship.create(userId, relatedUserId, dto.getRelationType());
        
        if (dto.getId() != null) {
            relationship.setId(dto.getId());
        }
        
        if (dto.getRemark() != null) {
            relationship.setRemark(dto.getRemark());
        }
        
        return relationship;
    }

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userRelationshipList 用户关系领域模型列表
     * @return 用户关系DTO列表
     */
    default List<UserRelationshipDTO> toDTO(List<UserRelationship> userRelationshipList) {
        if (userRelationshipList == null) {
            return null;
        }
        
        return userRelationshipList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userRelationshipDTOList 用户关系DTO列表
     * @return 用户关系领域模型列表
     */
    default List<UserRelationship> toDomain(List<UserRelationshipDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        
        return dtoList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
    
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