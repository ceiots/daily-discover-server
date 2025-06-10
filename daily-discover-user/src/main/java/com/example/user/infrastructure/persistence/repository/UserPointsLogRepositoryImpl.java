package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.repository.UserPointsLogRepository;
import com.example.user.infrastructure.persistence.converter.UserPointsLogConverter;
import com.example.user.infrastructure.persistence.entity.UserPointsLogEntity;
import com.example.user.infrastructure.persistence.mapper.UserPointsLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public UserPointsLog findById(Long id) {
        UserPointsLogEntity entity = userPointsLogMapper.selectById(id);
        return entity != null ? userPointsLogConverter.manualToDomain(entity) : null;
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
    public List<UserPointsLog> findByUserId(UserId userId) {
        LambdaQueryWrapper<UserPointsLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsLogEntity::getUserId, userId.getValue())
                .orderByDesc(UserPointsLogEntity::getCreateTime);
        
        List<UserPointsLogEntity> entities = userPointsLogMapper.selectList(wrapper);
        return entities.stream()
                .map(userPointsLogConverter::manualToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<UserPointsLog> findByUserId(UserId userId, PageRequest pageRequest) {
        LambdaQueryWrapper<UserPointsLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsLogEntity::getUserId, userId.getValue())
                .orderByDesc(UserPointsLogEntity::getCreateTime);
        
        Page<UserPointsLogEntity> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<UserPointsLogEntity> iPage = userPointsLogMapper.selectPage(page, wrapper);
        
        List<UserPointsLog> pointsLogs = iPage.getRecords().stream()
                .map(userPointsLogConverter::manualToDomain)
                .collect(Collectors.toList());
        
        return new PageResult<>(
                pageRequest.getPageNum(),
                pageRequest.getPageSize(),
                iPage.getTotal(),
                pointsLogs
        );
    }
    
    @Override
    public UserPointsLog update(UserPointsLog pointsLog) {
        UserPointsLogEntity entity = userPointsLogConverter.toEntity(pointsLog);
        userPointsLogMapper.updateById(entity);
        return pointsLog;
    }
    
    @Override
    public boolean delete(Long id) {
        int result = userPointsLogMapper.deleteById(id);
        return result > 0;
    }
    
    // 以下是额外实现的方法，不在接口中定义，但在领域服务中使用
    
    public List<UserPointsLog> findByUserId(UserId userId, Integer limit) {
        LambdaQueryWrapper<UserPointsLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsLogEntity::getUserId, userId.getValue())
                .orderByDesc(UserPointsLogEntity::getCreateTime);
        
        if (limit != null && limit > 0) {
            wrapper.last("LIMIT " + limit);
        }
        
        List<UserPointsLogEntity> entities = userPointsLogMapper.selectList(wrapper);
        return entities.stream()
                .map(userPointsLogConverter::manualToDomain)
                .collect(Collectors.toList());
    }
    
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
                .map(userPointsLogConverter::manualToDomain)
                .collect(Collectors.toList());
    }
}