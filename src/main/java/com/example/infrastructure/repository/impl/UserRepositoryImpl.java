package com.example.infrastructure.repository.impl;

import com.example.domain.model.user.User;
import com.example.domain.repository.UserRepository;
import com.example.infrastructure.repository.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储实现类
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.selectByUsername(username));
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return Optional.ofNullable(userMapper.selectByPhone(phone));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userMapper.selectByEmail(email));
    }

    @Override
    public User save(User user) {
        userMapper.insert(user);
        return user;
    }

    @Override
    public User update(User user) {
        userMapper.update(user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public List<User> findByPage(int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return userMapper.selectByPage(offset, pageSize);
    }

    @Override
    public long count() {
        return userMapper.count();
    }
} 