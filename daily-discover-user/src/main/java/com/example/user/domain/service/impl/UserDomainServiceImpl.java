package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.result.ResultCode;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.valueobject.Password;
import com.example.user.domain.repository.UserRepository;
import com.example.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户领域服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        // 创建用户
        User user = User.create(username, password, nickname);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerByMobile(Mobile mobile, String password, String nickname) {
        // 检查手机号是否已存在
        if (userRepository.existsByMobile(mobile)) {
            throw new BusinessException(ResultCode.MOBILE_ALREADY_EXISTS);
        }

        // 创建用户
        User user = User.create(mobile.getValue(), password, nickname);
        user.changeMobile(mobile);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User registerByEmail(Email email, String password, String nickname) {
        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ResultCode.EMAIL_ALREADY_EXISTS);
        }

        // 创建用户
        User user = User.create(email.getValue(), password, nickname);
        user.changeEmail(email);
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password, String ip) {
        // 查询用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 检查用户状态
        if (!user.isEnabled()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 验证密码
        if (!user.getPassword().matches(password)) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 记录登录信息
        user.recordLogin(ip);
        return userRepository.update(user);
    }

    @Override
    public User loginByMobile(Mobile mobile, String code, String ip) {
        // 验证验证码（实际实现应该调用验证码服务）
        // 这里简化处理，假设验证码已验证通过

        // 查询用户
        Optional<User> userOpt = userRepository.findByMobile(mobile);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 检查用户状态
        if (!user.isEnabled()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 记录登录信息
        user.recordLogin(ip);
        return userRepository.update(user);
    }

    @Override
    public User loginByEmail(Email email, String password, String ip) {
        // 查询用户
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 检查用户状态
        if (!user.isEnabled()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 验证密码
        if (!user.getPassword().matches(password)) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 记录登录信息
        user.recordLogin(ip);
        return userRepository.update(user);
    }

    @Override
    @Transactional
    public boolean changePassword(UserId userId, String oldPassword, String newPassword) {
        // 查询用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 修改密码
        user.changePassword(oldPassword, newPassword);
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(UserId userId, String newPassword) {
        // 查询用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 重置密码（这里简化处理，实际应该有更安全的方式）
        user.changePassword(user.getPassword().getEncodedValue(), newPassword);
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public boolean bindMobile(UserId userId, Mobile mobile, String code) {
        // 验证验证码（实际实现应该调用验证码服务）
        // 这里简化处理，假设验证码已验证通过

        // 检查手机号是否已被其他用户绑定
        if (userRepository.existsByMobile(mobile)) {
            throw new BusinessException(ResultCode.MOBILE_ALREADY_EXISTS);
        }

        // 查询用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 绑定手机号
        user.changeMobile(mobile);
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public boolean bindEmail(UserId userId, Email email, String code) {
        // 验证验证码（实际实现应该调用验证码服务）
        // 这里简化处理，假设验证码已验证通过

        // 检查邮箱是否已被其他用户绑定
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ResultCode.EMAIL_ALREADY_EXISTS);
        }

        // 查询用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 绑定邮箱
        user.changeEmail(email);
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public User updateUserInfo(UserId userId, String nickname, String avatar, Integer gender) {
        // 查询用户
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User user = userOpt.get();
        // 更新用户信息
        user.updateProfile(nickname, avatar, gender);
        return userRepository.update(user);
    }
} 