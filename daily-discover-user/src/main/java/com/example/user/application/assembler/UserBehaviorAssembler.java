package com.example.user.application.assembler;

import com.example.user.application.dto.UserBehaviorDTO;
import com.example.user.domain.model.UserBehavior;
import com.example.user.domain.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
    @Mapping(source = "userId", target = "userId", qualifiedByName = "userIdToLong")
    UserBehaviorDTO toDTO(UserBehavior userBehavior);

    /**
     * 将DTO转换为领域模型
     *
     * @param userBehaviorDTO 用户行为DTO
     * @return 用户行为领域模型
     */
    default UserBehavior toDomain(UserBehaviorDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // 使用UserBehavior.create方法创建对象
        UserBehavior behavior = UserBehavior.create(
            longToUserId(dto.getUserId()), 
            dto.getBehaviorType(), 
            dto.getTargetId(), 
            dto.getTargetType(),
            dto.getDeviceType(),
            dto.getDeviceId(),
            dto.getIp()
        );
        
        // 设置其他属性
        if (dto.getId() != null) {
            behavior.setId(dto.getId());
        }
        // DTO中没有behaviorData字段，不需要设置
        
        return behavior;
    }

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
    
    /**
     * 将UserId转换为Long
     *
     * @param userId 用户ID对象
     * @return 用户ID值
     */
    @Named("userIdToLong")
    default Long userIdToLong(UserId userId) {
        return userId != null ? userId.getValue() : null;
    }

    /**
     * 将Long转换为UserId
     *
     * @param id 用户ID值
     * @return 用户ID对象
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
}