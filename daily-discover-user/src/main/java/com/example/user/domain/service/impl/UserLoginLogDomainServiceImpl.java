package com.example.user.domain.service.impl;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserLoginLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.repository.UserLoginLogRepository;
import com.example.user.domain.service.UserLoginLogDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户登录日志领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginLogDomainServiceImpl extends BaseDomainServiceImpl implements UserLoginLogDomainService {

    private final UserLoginLogRepository userLoginLogRepository;

    @Override
    @Transactional
    public UserLoginLog recordLoginLog(UserLoginLog userLoginLog) {
        return userLoginLogRepository.save(userLoginLog);
    }

    @Override
    public List<UserLoginLog> getUserLoginLogs(UserId userId, Integer limit) {
        return userLoginLogRepository.findByUserId(userId, limit);
    }

    @Override
    public PageResult<UserLoginLog> getUserLoginLogPage(UserId userId, PageRequest pageRequest) {
        return userLoginLogRepository.findPage(userId, pageRequest);
    }

    @Override
    public Optional<UserLoginLog> getLastLoginLog(UserId userId) {
        return userLoginLogRepository.findLastLogin(userId);
    }

    @Override
    public Long countLoginLogs(UserId userId) {
        return userLoginLogRepository.countByUserId(userId);
    }

    @Override
    public Integer countFailureLoginLogs(UserId userId, Integer hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        return userLoginLogRepository.countFailureLoginLogs(userId, startTime);
    }

    @Override
    @Transactional
    public boolean clearFailureLoginLogs(UserId userId) {
        return userLoginLogRepository.clearFailureLoginLogs(userId);
    }

    @Override
    public List<UserLoginLog> getUserLoginLogsByTimeRange(UserId userId, LocalDateTime startTime, LocalDateTime endTime) {
        return userLoginLogRepository.findByUserIdAndTimeRange(userId, startTime, endTime);
    }

    @Override
    public List<UserLoginLog> getUserLoginLogsByDevice(UserId userId, String deviceId, Integer limit) {
        return userLoginLogRepository.findByUserIdAndDeviceId(userId, deviceId, limit);
    }
}