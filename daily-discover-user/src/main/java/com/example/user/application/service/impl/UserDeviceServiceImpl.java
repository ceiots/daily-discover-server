package com.example.user.application.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserDeviceAssembler;
import com.example.user.application.dto.UserDeviceDTO;
import com.example.user.application.service.UserDeviceService;
import com.example.user.domain.model.UserDevice;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserDeviceDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户设备应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserDeviceServiceImpl implements UserDeviceService {

    private final UserDeviceDomainService userDeviceDomainService;
    private final UserDeviceAssembler userDeviceAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userDeviceDomainService;
    }

    @Override
    public UserDeviceDTO getUserDevice(Long userId, String deviceId) {
        Optional<UserDevice> deviceOpt = userDeviceDomainService.getUserDevice(new UserId(userId), deviceId);
        return deviceOpt.map(userDeviceAssembler::toDTO).orElse(null);
    }

    @Override
    public List<UserDeviceDTO> getUserDevices(Long userId) {
        List<UserDevice> devices = userDeviceDomainService.getUserDevices(new UserId(userId));
        return devices.stream()
                .map(userDeviceAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserDeviceDTO> getUserDevicePage(Long userId, PageRequest pageRequest) {
        PageResult<UserDevice> pageResult = userDeviceDomainService.getUserDevicePage(new UserId(userId), pageRequest);
        
        List<UserDeviceDTO> deviceDTOs = pageResult.getList().stream()
                .map(userDeviceAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(deviceDTOs, pageResult.getTotal(), pageResult.getPages(), 
                pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    @Override
    @Transactional
    public UserDeviceDTO registerDevice(UserDeviceDTO userDeviceDTO) {
        UserDevice userDevice = userDeviceAssembler.toDomain(userDeviceDTO);
        UserDevice savedDevice = userDeviceDomainService.registerDevice(userDevice);
        return userDeviceAssembler.toDTO(savedDevice);
    }

    @Override
    @Transactional
    public UserDeviceDTO updateDevice(UserDeviceDTO userDeviceDTO) {
        UserDevice userDevice = userDeviceAssembler.toDomain(userDeviceDTO);
        UserDevice updatedDevice = userDeviceDomainService.updateDevice(userDevice);
        return userDeviceAssembler.toDTO(updatedDevice);
    }

    @Override
    @Transactional
    public UserDeviceDTO updatePushToken(Long userId, String deviceId, String pushToken) {
        UserDevice updatedDevice = userDeviceDomainService.updatePushToken(new UserId(userId), deviceId, pushToken);
        return userDeviceAssembler.toDTO(updatedDevice);
    }

    @Override
    @Transactional
    public boolean disableDevice(Long userId, String deviceId) {
        return userDeviceDomainService.disableDevice(new UserId(userId), deviceId);
    }

    @Override
    @Transactional
    public boolean enableDevice(Long userId, String deviceId) {
        return userDeviceDomainService.enableDevice(new UserId(userId), deviceId);
    }

    @Override
    @Transactional
    public boolean deleteDevice(Long userId, String deviceId) {
        return userDeviceDomainService.deleteDevice(new UserId(userId), deviceId);
    }
}