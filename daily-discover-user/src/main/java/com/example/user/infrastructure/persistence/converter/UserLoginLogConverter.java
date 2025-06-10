package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserLoginLog;
import com.example.user.infrastructure.persistence.entity.UserLoginLogEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户登录日志转换器
 */
@Component
public class UserLoginLogConverter {

    /**
     * 将领域模型转换为实体
     *
     * @param loginLog 用户登录日志领域模型
     * @return 用户登录日志实体
     */
    public UserLoginLogEntity toEntity(UserLoginLog loginLog) {
        if (loginLog == null) {
            return null;
        }
        
        UserLoginLogEntity entity = new UserLoginLogEntity();
        entity.setId(loginLog.getId());
        entity.setUserId(loginLog.getUserId().getValue());
        entity.setLoginTime(loginLog.getLoginTime());
        entity.setLoginIp(loginLog.getLoginIp());
        entity.setLoginType(loginLog.getLoginType());
        entity.setDeviceType(loginLog.getDeviceType());
        entity.setDeviceId(loginLog.getDeviceId());
        entity.setDeviceModel(loginLog.getDeviceModel());
        entity.setOsVersion(loginLog.getOsVersion());
        entity.setAppVersion(loginLog.getAppVersion());
        entity.setLocation(loginLog.getLocation());
        entity.setStatus(loginLog.getStatus());
        entity.setRemark(loginLog.getRemark());
        entity.setCreateTime(loginLog.getCreateTime());
        
        return entity;
    }

    /**
     * 将实体转换为领域模型
     *
     * @param entity 用户登录日志实体
     * @return 用户登录日志领域模型
     */
    public UserLoginLog toDomain(UserLoginLogEntity entity) {
        if (entity == null) {
            return null;
        }
        
        UserLoginLog loginLog = new UserLoginLog();
        loginLog.setId(entity.getId());
        loginLog.setUserId(new UserId(entity.getUserId()));
        loginLog.setLoginTime(entity.getLoginTime());
        loginLog.setLoginIp(entity.getLoginIp());
        loginLog.setLoginType(entity.getLoginType());
        loginLog.setDeviceType(entity.getDeviceType());
        loginLog.setDeviceId(entity.getDeviceId());
        loginLog.setDeviceModel(entity.getDeviceModel());
        loginLog.setOsVersion(entity.getOsVersion());
        loginLog.setAppVersion(entity.getAppVersion());
        loginLog.setLocation(entity.getLocation());
        loginLog.setStatus(entity.getStatus());
        loginLog.setRemark(entity.getRemark());
        loginLog.setCreateTime(entity.getCreateTime());
        
        return loginLog;
    }

    /**
     * 将领域模型列表转换为实体列表
     *
     * @param loginLogs 用户登录日志领域模型列表
     * @return 用户登录日志实体列表
     */
    public List<UserLoginLogEntity> toEntityList(List<UserLoginLog> loginLogs) {
        if (loginLogs == null) {
            return null;
        }
        return loginLogs.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 用户登录日志实体列表
     * @return 用户登录日志领域模型列表
     */
    public List<UserLoginLog> toDomainList(List<UserLoginLogEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }
}