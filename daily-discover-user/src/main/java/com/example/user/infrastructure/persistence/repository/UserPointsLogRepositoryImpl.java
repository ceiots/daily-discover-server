package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserPointsLog;
import com.example.user.domain.repository.UserPointsLogRepository;
import com.example.user.infrastructure.persistence.converter.UserPointsLogConverter;
import com.example.user.infrastructure.persistence.entity.UserPointsLogEntity;
import com.example.user.infrastructure.persistence.mapper.UserPointsLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户积分记录仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class UserPointsLogRepositoryImpl implements UserPointsLogRepository {

    private final UserPointsLogMapper userPointsLogMapper;
    private final UserPointsLogConverter userPointsLogConverter;

    @Override
    public Optional<UserPointsLog> findById(Long id) {
        UserPointsLogEntity entity = userPointsLogMapper.selectById(id);
        return Optional.ofNullable(entity)
                .map(userPointsLogConverter::toDomain);
    }

    @Override
    public UserPointsLog save(UserPointsLog pointsLog) {
        UserPointsLogEntity entity = userPointsLogConverter.toEntity(pointsLog);
        if (entity.getId() == null) {
            userPointsLogMapper.insert(entity);
        } else {
            userPointsLogMapper.updateById(entity);
        }
        pointsLog.setId(entity.getId());
        return pointsLog;
    }

    @Override
    public List<UserPointsLog> findByUserId(UserId userId, Integer limit) {
        LambdaQueryWrapper<UserPointsLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsLogEntity::getUserId, userId.getValue())
                .orderByDesc(UserPointsLogEntity::getCreateTime);
        
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        List<UserPointsLogEntity> entities = userPointsLogMapper.selectList(wrapper);
        return entities.stream()
                .map(userPointsLogConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserPointsLog> findPage(UserId userId, PageRequest pageRequest) {
        LambdaQueryWrapper<UserPointsLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsLogEntity::getUserId, userId.getValue())
                .orderByDesc(UserPointsLogEntity::getCreateTime);
        
        Page<UserPointsLogEntity> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<UserPointsLogEntity> iPage = userPointsLogMapper.selectPage(page, wrapper);
        
        List<UserPointsLog> pointsLogs = iPage.getRecords().stream()
                .map(userPointsLogConverter::toDomain)
                .collect(Collectors.toList());
        
        return new PageResult<>(
                pageRequest.getPageNum(),
                pageRequest.getPageSize(),
                iPage.getTotal(),
                pointsLogs
        );
    }
    
    @Override
    public List<UserPointsLog> findByUserIdAndType(UserId userId, Integer type, Integer limit) {
        LambdaQueryWrapper<UserPointsLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsLogEntity::getUserId, userId.getValue());
        
        if (type != null) {
            wrapper.eq(UserPointsLogEntity::getType, type);
        }
        
        wrapper.orderByDesc(UserPointsLogEntity::getCreateTime);
        
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        List<UserPointsLogEntity> entities = userPointsLogMapper.selectList(wrapper);
        return entities.stream()
                .map(userPointsLogConverter::toDomain)
                .collect(Collectors.toList());
    }
}