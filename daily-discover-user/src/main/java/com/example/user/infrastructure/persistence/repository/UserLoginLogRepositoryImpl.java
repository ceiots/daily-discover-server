package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.user.domain.model.UserLoginLog;
import com.example.user.domain.repository.UserLoginLogRepository;
import com.example.user.infrastructure.persistence.converter.UserLoginLogConverter;
import com.example.user.infrastructure.persistence.entity.UserLoginLogEntity;
import com.example.user.infrastructure.persistence.mapper.UserLoginLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户登录日志仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class UserLoginLogRepositoryImpl implements UserLoginLogRepository {

    private final UserLoginLogMapper userLoginLogMapper;
    private final UserLoginLogConverter userLoginLogConverter;

    @Override
    public Optional<UserLoginLog> findById(Long id) {
        UserLoginLogEntity entity = userLoginLogMapper.selectById(id);
        return Optional.ofNullable(entity).map(userLoginLogConverter::toDomain);
    }

    @Override
    public UserLoginLog save(UserLoginLog loginLog) {
        UserLoginLogEntity entity = userLoginLogConverter.toEntity(loginLog);
        userLoginLogMapper.insert(entity);
        loginLog.setId(entity.getId());
        return loginLog;
    }

    @Override
    public List<UserLoginLog> findByUserId(Long userId, Integer limit) {
        LambdaQueryWrapper<UserLoginLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLoginLogEntity::getUserId, userId)
                .orderByDesc(UserLoginLogEntity::getLoginTime);
        
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        List<UserLoginLogEntity> entities = userLoginLogMapper.selectList(wrapper);
        return userLoginLogConverter.toDomainList(entities);
    }

    @Override
    public Optional<UserLoginLog> findLastLoginLog(Long userId) {
        LambdaQueryWrapper<UserLoginLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLoginLogEntity::getUserId, userId)
                .eq(UserLoginLogEntity::getStatus, 1) // 成功登录
                .orderByDesc(UserLoginLogEntity::getLoginTime)
                .last("LIMIT 1");
        
        UserLoginLogEntity entity = userLoginLogMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(userLoginLogConverter::toDomain);
    }

    @Override
    public Integer countByUserId(Long userId) {
        LambdaQueryWrapper<UserLoginLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLoginLogEntity::getUserId, userId);
        return Math.toIntExact(userLoginLogMapper.selectCount(wrapper));
    }

    @Override
    public Integer countFailureByUserId(Long userId, Integer hours) {
        LambdaQueryWrapper<UserLoginLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLoginLogEntity::getUserId, userId)
                .eq(UserLoginLogEntity::getStatus, 0); // 失败登录
        
        if (hours != null && hours > 0) {
            LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
            wrapper.ge(UserLoginLogEntity::getLoginTime, startTime);
        }
        
        return Math.toIntExact(userLoginLogMapper.selectCount(wrapper));
    }

    @Override
    public boolean clearFailureByUserId(Long userId) {
        LambdaQueryWrapper<UserLoginLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLoginLogEntity::getUserId, userId)
                .eq(UserLoginLogEntity::getStatus, 0); // 失败登录
        
        return userLoginLogMapper.delete(wrapper) >= 0;
    }
}