package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.event.UserRegisteredEvent;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.UserProfile;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.valueobject.Password;
import com.example.user.domain.repository.UserQueryCondition;
import com.example.user.domain.repository.UserRepository;
import com.example.user.domain.repository.UserProfileRepository;
import com.example.user.domain.service.UserDomainService;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Override
    public User getUserById(Long userId) {
        return getUserById(new UserId(userId));
    }

    @Override
    public User getUserById(UserId userId) {
        return userRepository.findById(userId.getValue())
                .orElseThrow(() -> new BusinessException(ResultCode.USER_NOT_FOUND));
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByMobile(Mobile mobile) {
        return userRepository.findByMobile(mobile.getValue());
    }

    @Override
    public Optional<User> getUserByEmail(Email email) {
        return userRepository.findByEmail(email.getValue());
    }

    @Override
    @Transactional
    public User registerUser(User user, String password, String registerIp) {
        // 设置密码
        String salt = generateSalt();
        String encodedPassword = encryptPassword(password, salt);
        user.setPassword(Password.ofEncoded(encodedPassword));
        
        // 设置注册信息
        user.setRegisterInfo(registerIp, LocalDateTime.now());
        
        // 保存用户
        User savedUser = userRepository.save(user);
        
        // 创建用户详情
        UserProfile userProfile = UserProfile.create(savedUser.getId(), savedUser.getNickname());
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
            userOpt = userRepository.findByMobile(username);
        }
        
        if (userOpt.isEmpty() && validateEmailFormat(username)) {
            userOpt = userRepository.findByEmail(username);
        }
        
        // 验证密码
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (verifyPassword(password, user.getPassword().getEncodedValue())) {
                // 更新登录信息
                user.updateLoginInfo(loginIp, LocalDateTime.now());
                userRepository.update(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<User> loginByMobileCode(String mobile, String code, String loginIp, String deviceId, Integer deviceType) {
        // 验证手机验证码
        if (!verifyMobileCode(mobile, code)) {
            return Optional.empty();
        }
        
        // 查找用户
        Optional<User> userOpt = userRepository.findByMobile(mobile);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 更新登录信息
            user.updateLoginInfo(loginIp, LocalDateTime.now());
            userRepository.update(user);
            return Optional.of(user);
        }
        
        return Optional.empty();
    }

    @Override
    public Optional<User> loginByThirdParty(String type, String openId, String nickname, String avatar, String deviceId, Integer deviceType) {
        // 实现第三方登录逻辑
        // 这里只是一个简单的实现，实际应用中可能需要更复杂的逻辑
        return Optional.empty();
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.update(user);
    }

    @Override
    @Transactional
    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfileRepository.update(userProfile);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        if (!verifyPassword(oldPassword, user.getPassword().getEncodedValue())) {
            return false;
        }
        
        // 设置新密码
        String salt = generateSalt();
        String encodedPassword = encryptPassword(newPassword, salt);
        user.setPassword(Password.ofEncoded(encodedPassword));
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId, String password) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        String salt = generateSalt();
        String encodedPassword = encryptPassword(password, salt);
        user.setPassword(Password.ofEncoded(encodedPassword));
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public boolean bindMobile(Long userId, String mobile, String code) {
        // 验证手机验证码
        if (!verifyMobileCode(mobile, code)) {
            return false;
        }
        
        // 检查手机号是否已被使用
        Mobile mobileObj = Mobile.of(mobile);
        if (userRepository.findByMobile(mobile).isPresent()) {
            return false;
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.changeMobile(mobileObj.markVerified());
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public boolean bindEmail(Long userId, String email, String code) {
        // 验证邮箱验证码
        if (!verifyEmailCode(email, code)) {
            return false;
        }
        
        // 检查邮箱是否已被使用
        Email emailObj = Email.of(email);
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        user.changeEmail(emailObj.markVerified());
        userRepository.update(user);
        return true;
    }

    @Override
    @Transactional
    public boolean bindThirdParty(Long userId, String type, String openId, String unionId) {
        // 实现绑定第三方账号逻辑
        return false;
    }

    @Override
    @Transactional
    public boolean unbindThirdParty(Long userId, String type) {
        // 实现解绑第三方账号逻辑
        return false;
    }

    @Override
    public boolean verifyMobileCode(String mobile, String code) {
        // 实现手机验证码验证逻辑
        // 这里只是一个简单的实现，实际应用中应该调用短信验证服务
        return "123456".equals(code);
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        // 实现邮箱验证码验证逻辑
        // 这里只是一个简单的实现，实际应用中应该调用邮件验证服务
        return "123456".equals(code);
    }

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
    
    @Override
    public Optional<UserProfile> findProfileByUserId(UserId userId) {
        return userProfileRepository.findByUserId(userId.getValue());
    }
    
    @Override
    public Optional<UserProfile> findProfileById(Long id) {
        return userProfileRepository.findById(id);
    }
    
    @Override
    public PageResult<User> findPage(PageRequest pageRequest, UserQueryCondition condition) {
        return userRepository.findPage(pageRequest, condition);
    }
    
    @Override
    public List<User> findList(UserQueryCondition condition) {
        return userRepository.findList(condition);
    }
    
    /**
     * 验证密码
     */
    private boolean verifyPassword(String plainPassword, String encodedPassword) {
        return PASSWORD_ENCODER.matches(plainPassword, encodedPassword);
    }
}