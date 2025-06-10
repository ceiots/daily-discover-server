package com.example.user.application.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserLoginLogAssembler;
import com.example.user.application.dto.UserLoginLogDTO;
import com.example.user.application.service.UserLoginLogService;
import com.example.user.domain.model.UserLoginLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserLoginLogDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户登录日志应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserLoginLogServiceImpl implements UserLoginLogService {

    private final UserLoginLogDomainService userLoginLogDomainService;
    private final UserLoginLogAssembler userLoginLogAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userLoginLogDomainService;
    }

    @Override
    @Transactional
    public UserLoginLogDTO recordLoginLog(UserLoginLogDTO userLoginLogDTO) {
        UserLoginLog userLoginLog = userLoginLogAssembler.toDomain(userLoginLogDTO);
        UserLoginLog savedLog = userLoginLogDomainService.recordLoginLog(userLoginLog);
        return userLoginLogAssembler.toDTO(savedLog);
    }

    @Override
    public List<UserLoginLogDTO> getUserLoginLogs(Long userId, Integer limit) {
        List<UserLoginLog> logs = userLoginLogDomainService.getUserLoginLogs(new UserId(userId), limit);
        return logs.stream()
                .map(userLoginLogAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserLoginLogDTO> getUserLoginLogPage(Long userId, PageRequest pageRequest) {
        PageResult<UserLoginLog> pageResult = userLoginLogDomainService.getUserLoginLogPage(
                new UserId(userId), pageRequest);
        
        List<UserLoginLogDTO> logDTOs = pageResult.getList().stream()
                .map(userLoginLogAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), 
                pageResult.getTotal(), logDTOs);
    }

    @Override
    public UserLoginLogDTO getLastLoginLog(Long userId) {
        Optional<UserLoginLog> logOpt = userLoginLogDomainService.getLastLoginLog(new UserId(userId));
        return logOpt.map(userLoginLogAssembler::toDTO).orElse(null);
    }

    @Override
    public Long countLoginLogs(Long userId) {
        return userLoginLogDomainService.countLoginLogs(new UserId(userId));
    }

    @Override
    public Integer countFailureLoginLogs(Long userId, Integer hours) {
        return userLoginLogDomainService.countFailureLoginLogs(new UserId(userId), hours);
    }

    @Override
    @Transactional
    public boolean clearFailureLoginLogs(Long userId) {
        return userLoginLogDomainService.clearFailureLoginLogs(new UserId(userId));
    }

    @Override
    public List<UserLoginLogDTO> getUserLoginLogsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<UserLoginLog> logs = userLoginLogDomainService.getUserLoginLogsByTimeRange(
                new UserId(userId), startTime, endTime);
        return logs.stream()
                .map(userLoginLogAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserLoginLogDTO> getUserLoginLogsByDevice(Long userId, String deviceId, Integer limit) {
        List<UserLoginLog> logs = userLoginLogDomainService.getUserLoginLogsByDevice(
                new UserId(userId), deviceId, limit);
        return logs.stream()
                .map(userLoginLogAssembler::toDTO)
                .collect(Collectors.toList());
    }
}