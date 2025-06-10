package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.User;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.UserAuth;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.repository.UserQueryCondition;
import com.example.user.domain.repository.UserRepository;
import com.example.user.infrastructure.persistence.converter.UserAuthConverter;
import com.example.user.infrastructure.persistence.converter.UserConverter;
import com.example.user.infrastructure.persistence.entity.UserAuthEntity;
import com.example.user.infrastructure.persistence.entity.UserEntity;
import com.example.user.infrastructure.persistence.entity.UserProfileEntity;
import com.example.user.infrastructure.persistence.mapper.UserAuthMapper;
import com.example.user.infrastructure.persistence.mapper.UserMapper;
import com.example.user.infrastructure.persistence.mapper.UserProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
    private final UserAuthMapper userAuthMapper;
    private final UserConverter userConverter;
    private final UserAuthConverter userAuthConverter;

    @Override
    public Optional<User> findById(Long userId) {
        UserEntity entity = userMapper.selectById(userId);
        return Optional.ofNullable(userConverter.toDomain(entity));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        UserEntity entity = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userConverter.toDomain(entity));
    }

    @Override
    public Optional<User> findByMobile(String mobile) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getMobile, mobile);
        UserEntity entity = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userConverter.toDomain(entity));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getEmail, email);
        UserEntity entity = userMapper.selectOne(wrapper);
        return Optional.ofNullable(userConverter.toDomain(entity));
    }

    @Override
    public User save(User user) {
        UserEntity entity = userConverter.toEntity(user);
        userMapper.insert(entity);
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
        LambdaQueryWrapper<UserProfileEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserProfileEntity::getUserId, userId);
        UserProfileEntity entity = userProfileMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(e -> {
            UserProfile profile = UserProfile.create(new UserId(e.getUserId()), null);
            profile.setId(e.getId());
            profile.setRealName(e.getRealName());
            profile.setGender(e.getGender());
            profile.setBirthday(e.getBirthday());
            profile.setBio(e.getBio());
            return profile;
        });
    }

    @Override
    public UserProfile saveProfile(UserProfile userProfile) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.setUserId(userProfile.getUserId().getValue());
        entity.setRealName(userProfile.getRealName());
        entity.setGender(userProfile.getGender());
        entity.setBirthday(userProfile.getBirthday());
        entity.setBio(userProfile.getBio());
        userProfileMapper.insert(entity);
        userProfile.setId(entity.getId());
        return userProfile;
    }

    @Override
    public UserProfile updateProfile(UserProfile userProfile) {
        UserProfileEntity entity = new UserProfileEntity();
        entity.setId(userProfile.getId());
        entity.setUserId(userProfile.getUserId().getValue());
        entity.setRealName(userProfile.getRealName());
        entity.setGender(userProfile.getGender());
        entity.setBirthday(userProfile.getBirthday());
        entity.setBio(userProfile.getBio());
        userProfileMapper.updateById(entity);
        return userProfile;
    }

    @Override
    public List<UserAuth> findAuthsByUserId(Long userId) {
        LambdaQueryWrapper<UserAuthEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthEntity::getUserId, userId);
        List<UserAuthEntity> entities = userAuthMapper.selectList(wrapper);
        return userAuthConverter.toDomainList(entities);
    }

    @Override
    public Optional<UserAuth> findAuthByIdentity(String identityType, String identifier) {
        LambdaQueryWrapper<UserAuthEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthEntity::getIdentityType, identityType)
                .eq(UserAuthEntity::getIdentifier, identifier);
        UserAuthEntity entity = userAuthMapper.selectOne(wrapper);
        return Optional.ofNullable(userAuthConverter.toDomain(entity));
    }

    @Override
    public UserAuth saveAuth(UserAuth userAuth) {
        UserAuthEntity entity = userAuthConverter.toEntity(userAuth);
        userAuthMapper.insert(entity);
        userAuth.setId(entity.getId());
        return userAuth;
    }

    @Override
    public UserAuth updateAuth(UserAuth userAuth) {
        UserAuthEntity entity = userAuthConverter.toEntity(userAuth);
        userAuthMapper.updateById(entity);
        return userAuth;
    }

    @Override
    public boolean deleteAuth(Long id) {
        return userAuthMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteBatch(List<UserId> userIds) {
        List<Long> ids = userIds.stream().map(UserId::getValue).collect(Collectors.toList());
        return userMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public boolean updateStatus(UserId userId, Integer status) {
        LambdaUpdateWrapper<UserEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserEntity::getId, userId.getValue())
                .set(UserEntity::getStatus, status);
        return userMapper.update(null, wrapper) > 0;
    }

    @Override
    public PageResult<User> findPage(PageRequest pageRequest, UserQueryCondition condition) {
        LambdaQueryWrapper<UserEntity> wrapper = createQueryWrapper(condition);
        Page<UserEntity> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<UserEntity> entityPage = userMapper.selectPage(page, wrapper);
        
        List<User> users = userConverter.toDomainList(entityPage.getRecords());
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), entityPage.getTotal(), users);
    }

    @Override
    public List<User> findList(UserQueryCondition condition) {
        LambdaQueryWrapper<UserEntity> wrapper = createQueryWrapper(condition);
        List<UserEntity> entities = userMapper.selectList(wrapper);
        return userConverter.toDomainList(entities);
    }

    @Override
    public List<User> findByIds(List<UserId> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ids = userIds.stream().map(UserId::getValue).collect(Collectors.toList());
        List<UserEntity> entities = userMapper.selectBatchIds(ids);
        return userConverter.toDomainList(entities);
    }

    @Override
    public boolean existsByUsername(String username) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByMobile(Mobile mobile) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getMobile, mobile.getValue());
        return userMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsByEmail(Email email) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getEmail, email.getValue());
        return userMapper.selectCount(wrapper) > 0;
    }

    /**
     * 创建查询条件包装器
     *
     * @param condition 查询条件
     * @return 查询条件包装器
     */
    private LambdaQueryWrapper<UserEntity> createQueryWrapper(UserQueryCondition condition) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        if (condition != null) {
            if (StringUtils.hasText(condition.getUsername())) {
                wrapper.like(UserEntity::getUsername, condition.getUsername());
            }
            if (StringUtils.hasText(condition.getMobile())) {
                wrapper.like(UserEntity::getMobile, condition.getMobile());
            }
            if (StringUtils.hasText(condition.getEmail())) {
                wrapper.like(UserEntity::getEmail, condition.getEmail());
            }
            if (condition.getStatusList() != null && !condition.getStatusList().isEmpty()) {
                wrapper.in(UserEntity::getStatus, condition.getStatusList());
            }
        }
        return wrapper;
    }
} 