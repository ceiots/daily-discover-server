package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserAuth;
import com.example.user.domain.repository.UserAuthRepository;
import com.example.user.infrastructure.persistence.converter.UserAuthConverter;
import com.example.user.infrastructure.persistence.entity.UserAuthEntity;
import com.example.user.infrastructure.persistence.mapper.UserAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户授权仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class UserAuthRepositoryImpl implements UserAuthRepository {

    private final UserAuthMapper userAuthMapper;
    private final UserAuthConverter userAuthConverter;

    @Override
    public List<UserAuth> findByUserId(UserId userId) {
        LambdaQueryWrapper<UserAuthEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthEntity::getUserId, userId.getValue());
        List<UserAuthEntity> entities = userAuthMapper.selectList(wrapper);
        return userAuthConverter.toDomainList(entities);
    }

    @Override
    public Optional<UserAuth> findByIdentity(String identityType, String identifier) {
        LambdaQueryWrapper<UserAuthEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthEntity::getIdentityType, identityType)
                .eq(UserAuthEntity::getIdentifier, identifier);
        UserAuthEntity entity = userAuthMapper.selectOne(wrapper);
        return Optional.ofNullable(userAuthConverter.toDomain(entity));
    }

    @Override
    public UserAuth save(UserAuth userAuth) {
        UserAuthEntity entity = userAuthConverter.toEntity(userAuth);
        userAuthMapper.insert(entity);
        userAuth.setId(entity.getId());
        return userAuth;
    }

    @Override
    public UserAuth update(UserAuth userAuth) {
        UserAuthEntity entity = userAuthConverter.toEntity(userAuth);
        userAuthMapper.updateById(entity);
        return userAuth;
    }

    @Override
    public boolean delete(Long id) {
        return userAuthMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByUserId(UserId userId) {
        LambdaQueryWrapper<UserAuthEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAuthEntity::getUserId, userId.getValue());
        return userAuthMapper.delete(wrapper) > 0;
    }
} 