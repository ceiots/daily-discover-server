package com.example.user.application.assembler;

import com.example.user.application.dto.UserPointsLogDTO;
import com.example.user.domain.model.UserPointsLog;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户积分记录数据转换器
 */
@Mapper(componentModel = "spring")
public interface UserPointsLogAssembler {

    UserPointsLogAssembler INSTANCE = Mappers.getMapper(UserPointsLogAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userPointsLog 用户积分记录领域模型
     * @return 用户积分记录DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserPointsLogDTO toDTO(UserPointsLog userPointsLog);

    /**
     * 将DTO转换为领域模型
     *
     * @param userPointsLogDTO 用户积分记录DTO
     * @return 用户积分记录领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    UserPointsLog toDomain(UserPointsLogDTO userPointsLogDTO);

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userPointsLogList 用户积分记录领域模型列表
     * @return 用户积分记录DTO列表
     */
    List<UserPointsLogDTO> toDTO(List<UserPointsLog> userPointsLogList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userPointsLogDTOList 用户积分记录DTO列表
     * @return 用户积分记录领域模型列表
     */
    List<UserPointsLog> toDomain(List<UserPointsLogDTO> userPointsLogDTOList);
}