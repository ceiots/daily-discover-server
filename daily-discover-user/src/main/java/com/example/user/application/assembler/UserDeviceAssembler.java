package com.example.user.application.assembler;

import com.example.user.application.dto.UserDeviceDTO;
import com.example.user.domain.model.user.UserDevice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户设备数据转换器
 */
@Mapper(componentModel = "spring")
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
    @Mapping(target = "userId.value", source = "userId")
    UserDevice toDomain(UserDeviceDTO userDeviceDTO);

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
}