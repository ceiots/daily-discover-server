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
import java.util.stream.Collectors;

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
        return userLoginLogRepository.findByUserId(userId.getValue(), limit);
    }

    @Override
    public PageResult<UserLoginLog> getUserLoginLogPage(UserId userId, PageRequest pageRequest) {
        List<UserLoginLog> logs = userLoginLogRepository.findByUserId(userId.getValue(), null);
        
        int total = logs.size();
        int pageNum = pageRequest.getPageNum();
        int pageSize = pageRequest.getPageSize();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        if (fromIndex >= total) {
            return new PageResult<>(pageNum, pageSize, (long) total, List.of());
        }
        
        List<UserLoginLog> pageData = logs.subList(fromIndex, toIndex);
        
        return new PageResult<>(pageNum, pageSize, (long) total, pageData);
    }

    @Override
    public Optional<UserLoginLog> getLastLoginLog(UserId userId) {
        return userLoginLogRepository.findLastLoginLog(userId.getValue());
    }

    @Override
    public Long countLoginLogs(UserId userId) {
        return (long) userLoginLogRepository.countByUserId(userId.getValue());
    }

    @Override
    public Integer countFailureLoginLogs(UserId userId, Integer hours) {
        return userLoginLogRepository.countFailureByUserId(userId.getValue(), hours);
    }

    @Override
    @Transactional
    public boolean clearFailureLoginLogs(UserId userId) {
        return userLoginLogRepository.clearFailureByUserId(userId.getValue());
    }

    @Override
    public List<UserLoginLog> getUserLoginLogsByTimeRange(UserId userId, LocalDateTime startTime, LocalDateTime endTime) {
        List<UserLoginLog> logs = userLoginLogRepository.findByUserId(userId.getValue(), null);
        
        return logs.stream()
                .filter(log -> log.getCreateTime().isAfter(startTime) && log.getCreateTime().isBefore(endTime))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserLoginLog> getUserLoginLogsByDevice(UserId userId, String deviceId, Integer limit) {
        List<UserLoginLog> logs = userLoginLogRepository.findByUserId(userId.getValue(), null);
        
        return logs.stream()
                .filter(log -> deviceId.equals(log.getDeviceId()))
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .collect(Collectors.toList());
    }
}