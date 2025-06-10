package com.example.user.application.assembler;

import com.example.user.application.dto.UserDeviceDTO;
import com.example.user.domain.model.UserDevice;
import com.example.user.domain.model.id.UserId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户设备数据转换器
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserDeviceAssembler {

    UserDeviceAssembler INSTANCE = Mappers.getMapper(UserDeviceAssembler.class);

    /**
     * 将领域模型转换为DTO
     *
     * @param userDevice 用户设备领域模型
     * @return 用户设备DTO
     */
    @Mapping(source = "userId.value", target = "userId")
    UserDeviceDTO toDTO(UserDevice userDevice);

    /**
     * 将DTO转换为领域模型
     *
     * @param userDeviceDTO 用户设备DTO
     * @return 用户设备领域模型
     */
    default UserDevice toDomain(UserDeviceDTO dto) {
        if (dto == null) {
            return null;
        }
        
        // 使用UserDevice.create方法创建对象
        UserDevice device = UserDevice.create(
            longToUserId(dto.getUserId()),
            dto.getDeviceId(),
            dto.getDeviceType(),
            dto.getDeviceModel(),
            dto.getOsVersion(),
            dto.getAppVersion()
        );
        
        // 设置其他属性
        if (dto.getId() != null) {
            device.setId(dto.getId());
        }
        if (dto.getDeviceName() != null) {
            device.setDeviceName(dto.getDeviceName());
        }
        if (dto.getPushToken() != null) {
            device.setPushToken(dto.getPushToken());
        }
        
        return device;
    }

    /**
     * 将领域模型列表转换为DTO列表
     *
     * @param userDeviceList 用户设备领域模型列表
     * @return 用户设备DTO列表
     */
    List<UserDeviceDTO> toDTO(List<UserDevice> userDeviceList);

    /**
     * 将DTO列表转换为领域模型列表
     *
     * @param userDeviceDTOList 用户设备DTO列表
     * @return 用户设备领域模型列表
     */
    List<UserDevice> toDomain(List<UserDeviceDTO> userDeviceDTOList);
    
    /**
     * 将Long转换为UserId
     */
    @Named("longToUserId")
    default UserId longToUserId(Long id) {
        return id != null ? new UserId(id) : null;
    }
}