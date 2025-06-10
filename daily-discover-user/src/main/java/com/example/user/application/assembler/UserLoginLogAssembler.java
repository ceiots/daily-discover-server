package com.example.user.application.assembler;

import com.example.user.application.dto.UserLoginLogDTO;
import com.example.user.domain.model.user.UserLoginLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户登录日志数据转换器
 */
@Mapper(componentModel = "spring")
public interface UserLoginLogAssembler {

    UserLoginLogAssembler INSTANCE = Mappers.getMapper(UserLoginLogAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userLoginLog 用户登录日志领域模型
     * @return 用户登录日志DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserLoginLogDTO toDTO(UserLoginLog userLoginLog);

    /**
     * 将DTO转换为领域模型
     *
     * @param userLoginLogDTO 用户登录日志DTO
     * @return 用户登录日志领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    UserLoginLog toDomain(UserLoginLogDTO userLoginLogDTO);

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userLoginLogList 用户登录日志领域模型列表
     * @return 用户登录日志DTO列表
     */
    List<UserLoginLogDTO> toDTO(List<UserLoginLog> userLoginLogList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userLoginLogDTOList 用户登录日志DTO列表
     * @return 用户登录日志领域模型列表
     */
    List<UserLoginLog> toDomain(List<UserLoginLogDTO> userLoginLogDTOList);
}