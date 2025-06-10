package com.example.user.application.assembler;

import com.example.user.application.dto.UserAccountDTO;
import com.example.user.application.dto.UserAccountLogDTO;
import com.example.user.domain.model.UserAccount;
import com.example.user.domain.model.UserAccountLog;
import com.example.user.domain.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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
    default UserAccount toDomain(UserAccountDTO userAccountDTO) {
        if (userAccountDTO == null) {
            return null;
        }
        
        UserId userId = longToUserId(userAccountDTO.getUserId());
        UserAccount userAccount = UserAccount.create(userId);
        
        if (userAccountDTO.getId() != null) {
            userAccount.setId(userAccountDTO.getId());
        }
        
        return userAccount;
    }

    /**
     * 将账户流水领域模型转换为DTO
     *
     * @param userAccountLog 账户流水领域模型
     * @return 账户流水DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserAccountLogDTO toLogDTO(UserAccountLog userAccountLog);

    /**
     * 将账户流水DTO转换为领域模型
     *
     * @param userAccountLogDTO 账户流水DTO
     * @return 账户流水领域模型
     */
    default UserAccountLog toDomainLog(UserAccountLogDTO dto) {
        if (dto == null) {
            return null;
        }
        
        UserAccountLog log = new UserAccountLog();
        log.setId(dto.getId());
        log.setUserId(longToUserId(dto.getUserId()));
        // 设置其他属性...
        
        return log;
    }

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userAccountLogList 账户流水领域模型列表
     * @return 账户流水DTO列表
     */
    default List<UserAccountLogDTO> toLogDTOList(List<UserAccountLog> userAccountLogList) {
        if (userAccountLogList == null) {
            return null;
        }
        
        return userAccountLogList.stream()
                .map(this::toLogDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userAccountLogDTOList 账户流水DTO列表
     * @return 账户流水领域模型列表
     */
    default List<UserAccountLog> toDomainLogList(List<UserAccountLogDTO> dtoList) {
        if (dtoList == null) {
            return null;
        }
        
        return dtoList.stream()
                .map(this::toDomainLog)
                .collect(Collectors.toList());
    }
    
    /**
     * 将Long转换为UserId
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
} 