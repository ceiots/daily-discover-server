package com.example.user.application.assembler;

import com.example.user.application.dto.UserPointsLogDTO;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

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
    default UserPointsLog toDomain(UserPointsLogDTO userPointsLogDTO) {
        return createPointsLog(userPointsLogDTO);
    }

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
    default List<UserPointsLog> toDomain(List<UserPointsLogDTO> userPointsLogDTOList) {
        if (userPointsLogDTOList == null) {
            return null;
        }
        return userPointsLogDTOList.stream()
                .map(this::createPointsLog)
                .collect(Collectors.toList());
    }
    
    /**
     * 将Long类型转换为UserId
     */
    @Named("toUserId")
    default UserId toUserId(Long userId) {
        return userId != null ? new UserId(userId) : null;
    }
    
    /**
     * 创建积分记录
     */
    default UserPointsLog createPointsLog(UserPointsLogDTO dto) {
        if (dto == null) {
            return null;
        }
        
        UserId userId = toUserId(dto.getUserId());
        UserPointsLog pointsLog;
        
        switch (dto.getType()) {
            case 1: // 获取积分
                pointsLog = UserPointsLog.createGainLog(
                        userId,
                        dto.getPoints(),
                        dto.getBeforePoints(),
                        dto.getAfterPoints(),
                        dto.getSource(),
                        dto.getSourceId(),
                        dto.getDescription()
                );
                break;
            case 2: // 使用积分
                pointsLog = UserPointsLog.createUseLog(
                        userId,
                        dto.getPoints(),
                        dto.getBeforePoints(),
                        dto.getAfterPoints(),
                        dto.getSource(),
                        dto.getSourceId(),
                        dto.getDescription()
                );
                break;
            case 3: // 积分过期
                pointsLog = UserPointsLog.createExpireLog(
                        userId,
                        dto.getPoints(),
                        dto.getBeforePoints(),
                        dto.getAfterPoints(),
                        dto.getDescription()
                );
                break;
            default: // 调整积分
                pointsLog = UserPointsLog.createAdjustLog(
                        userId,
                        dto.getPoints(),
                        dto.getBeforePoints(),
                        dto.getAfterPoints(),
                        dto.getDescription()
                );
        }
        
        pointsLog.setId(dto.getId());
        if (dto.getExpireTime() != null) {
            pointsLog.setExpireTime(dto.getExpireTime());
        }
        
        return pointsLog;
    }
}