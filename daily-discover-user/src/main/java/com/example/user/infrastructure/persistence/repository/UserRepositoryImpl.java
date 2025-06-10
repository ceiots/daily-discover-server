package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.User;
import com.example.user.domain.model.UserAuth;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.repository.UserQueryCondition;
import com.example.user.domain.repository.UserRepository;
import com.example.user.infrastructure.persistence.converter.UserConverter;
import com.example.user.infrastructure.persistence.entity.UserEntity;
import com.example.user.infrastructure.persistence.entity.UserProfileEntity;
import com.example.user.infrastructure.persistence.mapper.UserMapper;
import com.example.user.infrastructure.persistence.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final UserConverter userConverter;

    @Override
    public Optional<User> findById(Long userId) {
        UserEntity entity = userMapper.selectById(userId);
        return Optional.ofNullable(entity).map(userConverter::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, username);
        UserEntity entity = userMapper.selectOne(queryWrapper);
        return Optional.ofNullable(entity).map(userConverter::toDomain);
    }

    @Override
    public Optional<User> findByMobile(String mobile) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getMobile, mobile);
        UserEntity entity = userMapper.selectOne(queryWrapper);
        return Optional.ofNullable(entity).map(userConverter::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getEmail, email);
        UserEntity entity = userMapper.selectOne(queryWrapper);
        return Optional.ofNullable(entity).map(userConverter::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userConverter.toEntity(user);
        userMapper.insert(entity);
        // 设置ID并返回
        user.setId(new UserId(entity.getId()));
        return user;
    }

    @Override
    public User update(User user) {
        UserEntity entity = userConverter.toEntity(user);
        userMapper.updateById(entity);
        return user;
    }

    @Override
    public boolean delete(Long userId) {
        return userMapper.deleteById(userId) > 0;
    }

    @Override
    public Optional<UserProfile> findProfileByUserId(Long userId) {
        LambdaQueryWrapper<UserProfileEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserProfileEntity::getUserId, userId);
        UserProfileEntity entity = userProfileMapper.selectOne(queryWrapper);
        
        // 这里需要实现UserProfileConverter，暂时返回空
        return Optional.empty();
    }

    @Override
    public UserProfile saveProfile(UserProfile userProfile) {
        // 这里需要实现UserProfileConverter，暂时返回原对象
        return userProfile;
    }

    @Override
    public UserProfile updateProfile(UserProfile userProfile) {
        // 这里需要实现UserProfileConverter，暂时返回原对象
        return userProfile;
    }

    @Override
    public List<UserAuth> findAuthsByUserId(Long userId) {
        // 这里需要实现UserAuthMapper和UserAuthConverter，暂时返回空列表
        return Collections.emptyList();
    }

    @Override
    public Optional<UserAuth> findAuthByIdentity(String identityType, String identifier) {
        // 这里需要实现UserAuthMapper和UserAuthConverter，暂时返回空
        return Optional.empty();
    }

    @Override
    public UserAuth saveAuth(UserAuth userAuth) {
        // 这里需要实现UserAuthMapper和UserAuthConverter，暂时返回原对象
        return userAuth;
    }

    @Override
    public UserAuth updateAuth(UserAuth userAuth) {
        // 这里需要实现UserAuthMapper和UserAuthConverter，暂时返回原对象
        return userAuth;
    }

    @Override
    public boolean deleteAuth(Long id) {
        // 这里需要实现UserAuthMapper，暂时返回false
        return false;
    }

    @Override
    public boolean deleteBatch(List<UserId> userIds) {
        List<Long> ids = userIds.stream().map(UserId::getValue).collect(Collectors.toList());
        return userMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean updateStatus(UserId userId, Integer status) {
        LambdaUpdateWrapper<UserEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(UserEntity::getId, userId.getValue())
                .set(UserEntity::getStatus, status);
        return userMapper.update(null, updateWrapper) > 0;
    }

    @Override
    public PageResult<User> findPage(PageRequest pageRequest, UserQueryCondition condition) {
        Page<UserEntity> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        LambdaQueryWrapper<UserEntity> queryWrapper = createQueryWrapper(condition);
        
        Page<UserEntity> resultPage = userMapper.selectPage(page, queryWrapper);
        
        List<User> users = resultPage.getRecords().stream()
                .map(userConverter::toDomain)
                .collect(Collectors.toList());
        
        return new PageResult<>(users, resultPage.getTotal(), pageRequest.getPageNum(), pageRequest.getPageSize());
    }

    @Override
    public List<User> findList(UserQueryCondition condition) {
        LambdaQueryWrapper<UserEntity> queryWrapper = createQueryWrapper(condition);
        List<UserEntity> entities = userMapper.selectList(queryWrapper);
        return entities.stream().map(userConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<User> findByIds(List<UserId> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> ids = userIds.stream().map(UserId::getValue).collect(Collectors.toList());
        List<UserEntity> entities = userMapper.selectBatchIds(ids);
        return entities.stream().map(userConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getUsername, username);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByMobile(Mobile mobile) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getMobile, mobile.getValue());
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByEmail(Email email) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserEntity::getEmail, email.getValue());
        return userMapper.selectCount(queryWrapper) > 0;
    }
    
    /**
     * 创建查询条件包装器
     *
     * @param condition 查询条件
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<UserEntity> createQueryWrapper(UserQueryCondition condition) {
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        
        if (condition == null) {
            return queryWrapper;
        }
        
        // 设置查询条件
        if (StringUtils.hasText(condition.getUsername())) {
            queryWrapper.like(UserEntity::getUsername, condition.getUsername());
        }
        
        if (StringUtils.hasText(condition.getNickname())) {
            queryWrapper.like(UserEntity::getNickname, condition.getNickname());
        }
        
        if (StringUtils.hasText(condition.getMobile())) {
            queryWrapper.like(UserEntity::getMobile, condition.getMobile());
        }
        
        if (StringUtils.hasText(condition.getEmail())) {
            queryWrapper.like(UserEntity::getEmail, condition.getEmail());
        }
        
        if (condition.getStatusList() != null && !condition.getStatusList().isEmpty()) {
            queryWrapper.in(UserEntity::getStatus, condition.getStatusList());
        }
        
        if (condition.getUserTypeList() != null && !condition.getUserTypeList().isEmpty()) {
            queryWrapper.in(UserEntity::getUserType, condition.getUserTypeList());
        }
        
        if (condition.getRegisterStartTime() != null) {
            queryWrapper.ge(UserEntity::getRegisterTime, condition.getRegisterStartTime());
        }
        
        if (condition.getRegisterEndTime() != null) {
            queryWrapper.le(UserEntity::getRegisterTime, condition.getRegisterEndTime());
        }
        
        if (condition.getLastLoginStartTime() != null) {
            queryWrapper.ge(UserEntity::getLastLoginTime, condition.getLastLoginStartTime());
        }
        
        if (condition.getLastLoginEndTime() != null) {
            queryWrapper.le(UserEntity::getLastLoginTime, condition.getLastLoginEndTime());
        }
        
        return queryWrapper;
    }
} 