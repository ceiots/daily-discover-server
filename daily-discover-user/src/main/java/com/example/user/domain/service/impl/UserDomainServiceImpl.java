package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.event.UserRegisteredEvent;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.User;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.repository.UserQueryCondition;
import com.example.user.domain.repository.UserRepository;
import com.example.user.domain.repository.UserProfileRepository;
import com.example.user.domain.service.UserDomainService;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl extends BaseDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    // 正则表达式
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");

    @Override
    public User getUserById(Long userId) {
        return getUserById(new UserId(userId));
    }

    @Override
    public User getUserById(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByMobile(Mobile mobile) {
        return userRepository.findByMobile(mobile);
    }

    @Override
    public Optional<User> getUserByEmail(Email email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User registerUser(User user, String password, String registerIp) {
        // 设置密码
        String salt = generateSalt();
        String encodedPassword = encryptPassword(password, salt);
        user.setPassword(encodedPassword);
        user.setSalt(salt);
        
        // 设置注册信息
        user.setRegisterTime(LocalDateTime.now());
        user.setRegisterIp(registerIp);
        user.setStatus(1); // 正常状态
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 创建用户详情
        UserProfile userProfile = UserProfile.create(savedUser.getId());
        userProfileRepository.save(userProfile);
        
        // 发布用户注册事件
        eventPublisher.publishEvent(new UserRegisteredEvent(savedUser.getId(), savedUser.getUsername(), registerIp));
        
        return savedUser;
    }

    @Override
    public Optional<User> loginByPassword(String username, String password, String loginIp, String deviceId, Integer deviceType) {
        // 尝试通过用户名、手机号、邮箱查找用户
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty() && validateMobileFormat(username)) {
            userOpt = userRepository.findByMobile(Mobile.of(username));
        }
        
        if (userOpt.isEmpty() && validateEmailFormat(username)) {
            userOpt = userRepository.findByEmail(Email.of(username));
        }
        
        // 验证密码
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (verifyPassword(password, user.getPassword(), user.getSalt())) {
                // 更新登录信息
                user.updateLoginInfo(loginIp, deviceId, deviceType);
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    // ... [other methods remain unchanged]

    @Override
    public boolean validateMobileFormat(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    @Override
    public boolean validateEmailFormat(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    // ... [other methods remain unchanged]
}