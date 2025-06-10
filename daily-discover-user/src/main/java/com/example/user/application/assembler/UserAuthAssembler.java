package com.example.user.application.assembler;

import com.example.user.application.dto.UserAuthDTO;
import com.example.user.domain.model.user.UserAuth;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户授权数据转换器
 */
@Mapper(componentModel = "spring")
public interface UserAuthAssembler {

    UserAuthAssembler INSTANCE = Mappers.getMapper(UserAuthAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userAuth 用户授权领域模型
     * @return 用户授权DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserAuthDTO toDTO(UserAuth userAuth);

    /**
     * 将DTO转换为领域模型
     *
     * @param userAuthDTO 用户授权DTO
     * @return 用户授权领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    UserAuth toDomain(UserAuthDTO userAuthDTO);

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userAuthList 用户授权领域模型列表
     * @return 用户授权DTO列表
     */
    List<UserAuthDTO> toDTO(List<UserAuth> userAuthList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userAuthDTOList 用户授权DTO列表
     * @return 用户授权领域模型列表
     */
    List<UserAuth> toDomain(List<UserAuthDTO> userAuthDTOList);
}