package com.example.user.application.assembler;

import com.example.user.application.dto.UserFollowDTO;
import com.example.user.domain.model.UserFollow;
import com.example.user.domain.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户关注数据转换器
 */
@Mapper(componentModel = "spring")
public interface UserFollowAssembler {

    UserFollowAssembler INSTANCE = Mappers.getMapper(UserFollowAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userFollow 用户关注领域模型
     * @return 用户关注DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    @Mapping(source = "followUserId.value", target = "followUserId")
    UserFollowDTO toDTO(UserFollow userFollow);

    /**
     * 将DTO转换为领域模型
     *
     * @param userFollowDTO 用户关注DTO
     * @return 用户关注领域模型
     */
    default UserFollow toDomain(UserFollowDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // 使用UserFollow.create方法创建对象
        UserFollow follow = UserFollow.create(
            longToUserId(dto.getUserId()),
            longToUserId(dto.getFollowUserId())
        );
        
        // 设置其他属性
        if (dto.getId() != null) {
            follow.setId(dto.getId());
        }
        
        return follow;
    }

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userFollowList 用户关注领域模型列表
     * @return 用户关注DTO列表
     */
    List<UserFollowDTO> toDTO(List<UserFollow> userFollowList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userFollowDTOList 用户关注DTO列表
     * @return 用户关注领域模型列表
     */
    List<UserFollow> toDomain(List<UserFollowDTO> userFollowDTOList);
    
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