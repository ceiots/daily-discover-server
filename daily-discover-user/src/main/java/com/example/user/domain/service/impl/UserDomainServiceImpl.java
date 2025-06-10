package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.common.util.PasswordEncoder;
import com.example.user.domain.event.UserRegisteredEvent;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.user.UserProfile;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.repository.UserQueryCondition;
import com.example.user.domain.repository.UserRepository;
import com.example.user.domain.repository.UserProfileRepository;
import com.example.user.domain.service.BaseDomainServiceImpl;
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

    @Override
    public Optional<User> loginByMobileCode(String mobile, String code, String loginIp, String deviceId, Integer deviceType) {
        // 验证手机验证码
        if (!verifyMobileCode(mobile, code)) {
            return Optional.empty();
        }
        
        // 查找用户
        Optional<User> userOpt = userRepository.findByMobile(Mobile.of(mobile));
        
        // 如果用户不存在，自动注册
        if (userOpt.isEmpty()) {
            User user = User.create(mobile, null, mobile);
            user.changeMobile(Mobile.of(mobile));
            user = registerUser(user, generateRandomPassword(), loginIp);
            userOpt = Optional.of(user);
        }
        
        // 更新登录信息
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updateLoginInfo(loginIp, deviceId, deviceType);
            userRepository.save(user);
        }
        
        return userOpt;
    }

    @Override
    public Optional<User> loginByThirdParty(String type, String openId, String nickname, String avatar, String deviceId, Integer deviceType) {
        // 查找绑定的用户
        Optional<User> userOpt = userRepository.findByThirdParty(type, openId);
        
        // 如果用户不存在，自动注册
        if (userOpt.isEmpty() && nickname != null) {
            User user = User.create(generateRandomUsername(), null, nickname);
            if (avatar != null) {
                user.setAvatar(avatar);
            }
            user = registerUser(user, generateRandomPassword(), null);
            user.bindThirdParty(type, openId, null);
            userRepository.save(user);
            userOpt = Optional.of(user);
        }
        
        // 更新登录信息
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updateLoginInfo(null, deviceId, deviceType);
            userRepository.save(user);
        }
        
        return userOpt;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        // 验证旧密码
        if (!verifyPassword(oldPassword, user.getPassword(), user.getSalt())) {
            return false;
        }
        
        // 设置新密码
        String salt = generateSalt();
        String encodedPassword = encryptPassword(newPassword, salt);
        user.setPassword(encodedPassword);
        user.setSalt(salt);
        
        userRepository.save(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long userId, String password) {
        User user = getUserById(userId);
        
        // 设置新密码
        String salt = generateSalt();
        String encodedPassword = encryptPassword(password, salt);
        user.setPassword(encodedPassword);
        user.setSalt(salt);
        
        userRepository.save(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean bindMobile(Long userId, String mobile, String code) {
        // 验证手机验证码
        if (!verifyMobileCode(mobile, code)) {
            return false;
        }
        
        // 检查手机号是否已被绑定
        if (userRepository.existsByMobile(Mobile.of(mobile))) {
            throw new BusinessException(ResultCode.MOBILE_EXISTS);
        }
        
        // 绑定手机号
        User user = getUserById(userId);
        user.changeMobile(Mobile.of(mobile));
        userRepository.save(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean bindEmail(Long userId, String email, String code) {
        // 验证邮箱验证码
        if (!verifyEmailCode(email, code)) {
            return false;
        }
        
        // 检查邮箱是否已被绑定
        if (userRepository.existsByEmail(Email.of(email))) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        // 绑定邮箱
        User user = getUserById(userId);
        user.changeEmail(Email.of(email));
        userRepository.save(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean bindThirdParty(Long userId, String type, String openId, String unionId) {
        // 检查第三方账号是否已被绑定
        if (userRepository.existsByThirdParty(type, openId)) {
            throw new BusinessException(ResultCode.THIRD_PARTY_EXISTS);
        }
        
        // 绑定第三方账号
        User user = getUserById(userId);
        user.bindThirdParty(type, openId, unionId);
        userRepository.save(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean unbindThirdParty(Long userId, String type) {
        // 解绑第三方账号
        User user = getUserById(userId);
        user.unbindThirdParty(type);
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean verifyMobileCode(String mobile, String code) {
        // TODO: 实现手机验证码验证逻辑
        return true; // 默认通过，实际项目中需要实现真正的验证逻辑
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        // TODO: 实现邮箱验证码验证逻辑
        return true; // 默认通过，实际项目中需要实现真正的验证逻辑
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

    /**
     * 生成随机用户名
     */
    private String generateRandomUsername() {
        return "user_" + System.currentTimeMillis();
    }

    /**
     * 生成随机密码
     */
    private String generateRandomPassword() {
        return "Aa123456";
    }
    
    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * 检查手机号是否存在
     */
    public boolean existsByMobile(String mobile) {
        return userRepository.existsByMobile(Mobile.of(mobile));
    }
    
    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(Email.of(email));
    }
    
    /**
     * 根据手机号查找用户
     */
    public Optional<User> findByMobile(String mobile) {
        return userRepository.findByMobile(Mobile.of(mobile));
    }
    
    /**
     * 根据用户ID查找用户详情
     */
    public Optional<UserProfile> findProfileByUserId(UserId userId) {
        return userProfileRepository.findByUserId(userId);
    }
    
    /**
     * 根据ID查找用户详情
     */
    public Optional<UserProfile> findProfileById(Long id) {
        return userProfileRepository.findById(id);
    }
    
    /**
     * 分页查询用户
     */
    public PageResult<User> findUserPage(PageRequest pageRequest, UserQueryCondition condition) {
        return userRepository.findPage(pageRequest, condition);
    }
    
    /**
     * 查询用户列表
     */
    public List<User> findUserList(UserQueryCondition condition) {
        return userRepository.findList(condition);
    }
} 