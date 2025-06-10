package com.example.user.application.assembler;

import com.example.user.application.dto.UserAccountDTO;
import com.example.user.application.dto.UserAccountLogDTO;
import com.example.user.domain.model.UserAccount;
import com.example.user.domain.model.UserAccountLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户账户对象转换器
 * 使用MapStruct实现DTO与领域模型的自动转换
 */
@Mapper(componentModel = "spring")
public interface UserAccountAssembler {

    UserAccountAssembler INSTANCE = Mappers.getMapper(UserAccountAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userAccount 用户账户领域模型
     * @return 用户账户DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserAccountDTO toDTO(UserAccount userAccount);

    /**
     * 将DTO转换为领域模型
     *
     * @param userAccountDTO 用户账户DTO
     * @return 用户账户领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    UserAccount toDomain(UserAccountDTO userAccountDTO);

    /**
     * 将账户流水领域模型转换为DTO
     *
     * @param userAccountLog 账户流水领域模型
     * @return 账户流水DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserAccountLogDTO toDTO(UserAccountLog userAccountLog);

    /**
     * 将账户流水DTO转换为领域模型
     *
     * @param userAccountLogDTO 账户流水DTO
     * @return 账户流水领域模型
     */
    @Mapping(target = "userId.value", source = "userId")
    UserAccountLog toDomain(UserAccountLogDTO userAccountLogDTO);

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userAccountLogList 账户流水领域模型列表
     * @return 账户流水DTO列表
     */
    List<UserAccountLogDTO> toDTO(List<UserAccountLog> userAccountLogList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userAccountLogDTOList 账户流水DTO列表
     * @return 账户流水领域模型列表
     */
    List<UserAccountLog> toDomain(List<UserAccountLogDTO> userAccountLogDTOList);
    
    /**
     * 将账户流水领域模型转换为DTO (别名方法，与toDTO功能相同)
     *
     * @param userAccountLog 账户流水领域模型
     * @return 账户流水DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserAccountLogDTO toLogDTO(UserAccountLog userAccountLog);
} 