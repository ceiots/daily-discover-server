package com.example.user.application.assembler;

import com.example.user.application.dto.UserFollowDTO;
import com.example.user.domain.model.user.UserFollow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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
    @Mapping(target = "userId.value", source = "userId")
    @Mapping(target = "followUserId.value", source = "followUserId")
    UserFollow toDomain(UserFollowDTO userFollowDTO);

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
}