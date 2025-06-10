package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.user.domain.model.UserAccount;
import com.example.user.domain.model.UserAccountLog;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.repository.UserAccountRepository;
import com.example.user.infrastructure.persistence.converter.UserAccountConverter;
import com.example.user.infrastructure.persistence.converter.UserAccountLogConverter;
import com.example.user.infrastructure.persistence.converter.UserPointsLogConverter;
import com.example.user.infrastructure.persistence.entity.UserAccountEntity;
import com.example.user.infrastructure.persistence.entity.UserAccountLogEntity;
import com.example.user.infrastructure.persistence.entity.UserPointsLogEntity;
import com.example.user.infrastructure.persistence.mapper.UserAccountLogMapper;
import com.example.user.infrastructure.persistence.mapper.UserAccountMapper;
import com.example.user.infrastructure.persistence.mapper.UserPointsLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户账户仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final UserAccountMapper userAccountMapper;
    private final UserAccountLogMapper userAccountLogMapper;
    private final UserPointsLogMapper userPointsLogMapper;
    private final UserAccountConverter userAccountConverter;
    private final UserAccountLogConverter userAccountLogConverter;
    private final UserPointsLogConverter userPointsLogConverter;

    @Override
    public Optional<UserAccount> findById(Long id) {
        UserAccountEntity entity = userAccountMapper.selectById(id);
        return Optional.ofNullable(entity).map(userAccountConverter::toDomain);
    }

    @Override
    public Optional<UserAccount> findByUserId(Long userId) {
        LambdaQueryWrapper<UserAccountEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAccountEntity::getUserId, userId);
        UserAccountEntity entity = userAccountMapper.selectOne(queryWrapper);
        return Optional.ofNullable(entity).map(userAccountConverter::toDomain);
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        UserAccountEntity entity = userAccountConverter.toEntity(userAccount);
        userAccountMapper.insert(entity);
        // 从数据库中重新获取完整的账户信息
        return findById(entity.getId()).orElse(userAccount);
    }

    @Override
    public UserAccount update(UserAccount userAccount) {
        UserAccountEntity entity = userAccountConverter.toEntity(userAccount);
        userAccountMapper.updateById(entity);
        return userAccount;
    }

    @Override
    public boolean updateBalance(Long userId, BigDecimal amount) {
        // 这里使用乐观锁更新，防止并发问题
        LambdaUpdateWrapper<UserAccountEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAccountEntity::getUserId, userId)
                .setSql("balance = balance + " + amount);
        return userAccountMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public boolean freezeBalance(Long userId, BigDecimal amount) {
        // 这里使用乐观锁更新，防止并发问题
        LambdaUpdateWrapper<UserAccountEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAccountEntity::getUserId, userId)
                .ge(UserAccountEntity::getBalance, amount)
                .setSql("balance = balance - " + amount)
                .setSql("freeze_amount = freeze_amount + " + amount);
        return userAccountMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public boolean unfreezeBalance(Long userId, BigDecimal amount) {
        // 这里使用乐观锁更新，防止并发问题
        LambdaUpdateWrapper<UserAccountEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAccountEntity::getUserId, userId)
                .ge(UserAccountEntity::getFreezeAmount, amount)
                .setSql("balance = balance + " + amount)
                .setSql("freeze_amount = freeze_amount - " + amount);
        return userAccountMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public boolean updatePoints(Long userId, Integer points) {
        // 这里使用乐观锁更新，防止并发问题
        LambdaUpdateWrapper<UserAccountEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAccountEntity::getUserId, userId)
                .setSql("points = points + " + points);
        return userAccountMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public boolean updateGrowth(Long userId, Integer growth) {
        // 这里使用乐观锁更新，防止并发问题
        LambdaUpdateWrapper<UserAccountEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserAccountEntity::getUserId, userId)
                .setSql("growth_value = growth_value + " + growth);
        return userAccountMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public UserAccountLog saveAccountLog(UserAccountLog accountLog) {
        UserAccountLogEntity entity = userAccountLogConverter.toEntity(accountLog);
        userAccountLogMapper.insert(entity);
        // 设置ID并返回
        accountLog.setId(entity.getId());
        return accountLog;
    }

    @Override
    public List<UserAccountLog> findAccountLogs(Long userId, Integer type, Integer limit) {
        LambdaQueryWrapper<UserAccountLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserAccountLogEntity::getUserId, userId);
        
        if (type != null) {
            queryWrapper.eq(UserAccountLogEntity::getType, type);
        }
        
        queryWrapper.orderByDesc(UserAccountLogEntity::getCreateTime);
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        List<UserAccountLogEntity> entities = userAccountLogMapper.selectList(queryWrapper);
        return userAccountLogConverter.toDomainList(entities);
    }

    @Override
    public UserPointsLog savePointsLog(UserPointsLog pointsLog) {
        UserPointsLogEntity entity = userPointsLogConverter.toEntity(pointsLog);
        userPointsLogMapper.insert(entity);
        // 设置ID并返回
        pointsLog.setId(entity.getId());
        return pointsLog;
    }

    @Override
    public List<UserPointsLog> findPointsLogs(Long userId, Integer type, Integer limit) {
        LambdaQueryWrapper<UserPointsLogEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserPointsLogEntity::getUserId, userId);
        
        if (type != null) {
            queryWrapper.eq(UserPointsLogEntity::getType, type);
        }
        
        queryWrapper.orderByDesc(UserPointsLogEntity::getCreateTime);
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        
        List<UserPointsLogEntity> entities = userPointsLogMapper.selectList(queryWrapper);
        return entities.stream()
                .map(userPointsLogConverter::toDomain)
                .collect(Collectors.toList());
    }
} 