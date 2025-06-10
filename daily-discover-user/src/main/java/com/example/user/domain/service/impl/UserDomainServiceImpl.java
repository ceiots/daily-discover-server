package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.result.ResultCode;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.repository.UserRepository;
import com.example.user.domain.service.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户领域服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserDomainServiceImpl implements UserDomainService {

    private final UserRepository userRepository;
    
    @Override
    public User getUserById(Long userId) {
        Optional<User> userOpt = userRepository.findById(new UserId(userId));
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return userOpt.get();
    }
    
    @Override
    public User getUserById(UserId userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return userOpt.get();
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
    public User registerUser(User user, String password, String registerIp) {
        // 验证用户名是否存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        
        // 验证手机号是否存在
        if (user.getMobile() != null && userRepository.existsByMobile(user.getMobile())) {
            throw new BusinessException(ResultCode.MOBILE_EXISTS);
        }
        
        // 验证邮箱是否存在
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        // 验证密码强度
        if (!validatePasswordStrength(password)) {
            throw new BusinessException(ResultCode.PASSWORD_TOO_WEAK);
        }
        
        // 设置密码
        user.setEncryptedPassword(encryptPassword(password));
        
        // 设置注册IP
        user.setRegisterIp(registerIp);
        
        // 保存用户
        User registeredUser = userRepository.save(user);
        
        // 发布用户注册事件
        DomainEventPublisher.publish(new UserRegisteredEvent(registeredUser.getId(), registeredUser.getUsername(), registerIp));
        
        return registeredUser;
    }

    @Override
    public Optional<User> loginByPassword(String username, String password, String loginIp, String deviceId, Integer deviceType) {
        // 查找用户
        Optional<User> userOpt;
        
        // 尝试通过用户名查找
        userOpt = userRepository.findByUsername(username);
        
        // 如果未找到，尝试通过手机号查找
        if (userOpt.isEmpty() && validateMobileFormat(username)) {
            userOpt = userRepository.findByMobile(Mobile.of(username));
        }
        
        // 如果未找到，尝试通过邮箱查找
        if (userOpt.isEmpty() && validateEmailFormat(username)) {
            userOpt = userRepository.findByEmail(Email.of(username));
        }
        
        // 如果未找到用户，返回空
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        User user = userOpt.get();
        
        // 验证用户状态
        if (!user.isEnabled()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 验证密码
        if (!verifyPassword(password, user.getEncryptedPassword())) {
            // 记录登录失败
            user.recordLoginFailure();
            userRepository.save(user);
            return Optional.empty();
        }
        
        // 记录登录成功
        user.recordLoginSuccess(loginIp, deviceId, deviceType);
        userRepository.save(user);
        
        return Optional.of(user);
    }

    @Override
    public Optional<User> loginByMobileCode(String mobile, String code, String loginIp, String deviceId, Integer deviceType) {
        // 验证手机号格式
        if (!validateMobileFormat(mobile)) {
            throw new BusinessException(ResultCode.INVALID_MOBILE_FORMAT);
        }
        
        // 验证验证码
        if (!verifyMobileCode(mobile, code)) {
            throw new BusinessException(ResultCode.INVALID_VERIFICATION_CODE);
        }
        
        // 查找用户
        Optional<User> userOpt = userRepository.findByMobile(Mobile.of(mobile));
        
        // 如果未找到用户，自动注册
        if (userOpt.isEmpty()) {
            User newUser = User.create(mobile, mobile, mobile);
            newUser.changeMobile(Mobile.of(mobile));
            newUser.setRegisterIp(loginIp);
            newUser = userRepository.save(newUser);
            userOpt = Optional.of(newUser);
        }
        
        User user = userOpt.get();
        
        // 验证用户状态
        if (!user.isEnabled()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 记录登录成功
        user.recordLoginSuccess(loginIp, deviceId, deviceType);
        userRepository.save(user);
        
        return Optional.of(user);
    }

    @Override
    public Optional<User> loginByThirdParty(String type, String openId, String nickname, String avatar, String deviceId, Integer deviceType) {
        // 查找用户
        Optional<User> userOpt = userRepository.findByThirdParty(type, openId);
        
        // 如果未找到用户，自动注册
        if (userOpt.isEmpty()) {
            // 生成随机用户名
            String username = generateRandomUsername();
            
            // 创建用户
            User newUser = User.create(username, username, nickname != null ? nickname : username);
            
            // 设置头像
            if (avatar != null) {
                newUser.updateAvatar(avatar);
            }
            
            // 绑定第三方账号
            newUser.bindThirdParty(type, openId, null);
            
            // 保存用户
            newUser = userRepository.save(newUser);
            userOpt = Optional.of(newUser);
        }
        
        User user = userOpt.get();
        
        // 验证用户状态
        if (!user.isEnabled()) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }
        
        // 记录登录成功
        user.recordLoginSuccess(null, deviceId, deviceType);
        userRepository.save(user);
        
        return Optional.of(user);
    }

    @Override
    public User updateUser(User user) {
        // 验证用户是否存在
        if (!userRepository.existsById(user.getId())) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 保存用户
        return userRepository.save(user);
    }

    @Override
    public UserProfile updateUserProfile(UserProfile userProfile) {
        // 验证用户是否存在
        if (!userRepository.existsById(userProfile.getUserId())) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 保存用户详情
        return userRepository.saveProfile(userProfile);
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 查找用户
        Optional<User> userOpt = userRepository.findById(new UserId(userId));
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        User user = userOpt.get();
        
        // 验证旧密码
        if (!verifyPassword(oldPassword, user.getEncryptedPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }
        
        // 验证新密码强度
        if (!validatePasswordStrength(newPassword)) {
            throw new BusinessException(ResultCode.PASSWORD_TOO_WEAK);
        }
        
        // 设置新密码
        user.setEncryptedPassword(encryptPassword(newPassword));
        
        // 保存用户
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean resetPassword(Long userId, String password) {
        // 查找用户
        Optional<User> userOpt = userRepository.findById(new UserId(userId));
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        User user = userOpt.get();
        
        // 验证密码强度
        if (!validatePasswordStrength(password)) {
            throw new BusinessException(ResultCode.PASSWORD_TOO_WEAK);
        }
        
        // 设置新密码
        user.setEncryptedPassword(encryptPassword(password));
        
        // 保存用户
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean bindMobile(Long userId, String mobile, String code) {
        // 验证手机号格式
        if (!validateMobileFormat(mobile)) {
            throw new BusinessException(ResultCode.INVALID_MOBILE_FORMAT);
        }
        
        // 验证验证码
        if (!verifyMobileCode(mobile, code)) {
            throw new BusinessException(ResultCode.INVALID_VERIFICATION_CODE);
        }
        
        // 查找用户
        Optional<User> userOpt = userRepository.findById(new UserId(userId));
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 检查手机号是否已被绑定
        if (userRepository.existsByMobile(Mobile.of(mobile))) {
            throw new BusinessException(ResultCode.MOBILE_EXISTS);
        }
        
        User user = userOpt.get();
        
        // 绑定手机号
        user.changeMobile(Mobile.of(mobile));
        
        // 保存用户
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean bindEmail(Long userId, String email, String code) {
        // 验证邮箱格式
        if (!validateEmailFormat(email)) {
            throw new BusinessException(ResultCode.INVALID_EMAIL_FORMAT);
        }
        
        // 验证验证码
        if (!verifyEmailCode(email, code)) {
            throw new BusinessException(ResultCode.INVALID_VERIFICATION_CODE);
        }
        
        // 查找用户
        Optional<User> userOpt = userRepository.findById(new UserId(userId));
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 检查邮箱是否已被绑定
        if (userRepository.existsByEmail(Email.of(email))) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        User user = userOpt.get();
        
        // 绑定邮箱
        user.changeEmail(Email.of(email));
        
        // 保存用户
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean bindThirdParty(Long userId, String type, String openId, String unionId) {
        // 查找用户
        Optional<User> userOpt = userRepository.findById(new UserId(userId));
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        // 检查第三方账号是否已被绑定
        if (userRepository.existsByThirdParty(type, openId)) {
            throw new BusinessException(ResultCode.THIRD_PARTY_BOUND);
        }
        
        User user = userOpt.get();
        
        // 绑定第三方账号
        user.bindThirdParty(type, openId, unionId);
        
        // 保存用户
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean unbindThirdParty(Long userId, String type) {
        // 查找用户
        Optional<User> userOpt = userRepository.findById(new UserId(userId));
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        User user = userOpt.get();
        
        // 解绑第三方账号
        user.unbindThirdParty(type);
        
        // 保存用户
        userRepository.save(user);
        
        return true;
    }

    @Override
    public boolean verifyMobileCode(String mobile, String code) {
        // TODO: 实现手机验证码验证逻辑
        // 这里简单实现，实际应该调用短信服务验证
        return "123456".equals(code);
    }

    @Override
    public boolean verifyEmailCode(String email, String code) {
        // TODO: 实现邮箱验证码验证逻辑
        // 这里简单实现，实际应该调用邮件服务验证
        return "123456".equals(code);
    }

    @Override
    public boolean validateMobileFormat(String mobile) {
        // 中国大陆手机号格式验证
        String regex = "^1[3-9]\\d{9}$";
        return Pattern.matches(regex, mobile);
    }

    @Override
    public boolean validateEmailFormat(String email) {
        // 邮箱格式验证
        String regex = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        return Pattern.matches(regex, email);
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        // 至少8位，包含大小写字母和数字
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        return Pattern.matches(regex, password);
    }
    
    /**
     * 加密密码
     *
     * @param password 明文密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        // TODO: 实现密码加密逻辑
        // 这里简单实现，实际应该使用BCrypt等加密算法
        return password + "_encrypted";
    }
    
    /**
     * 验证密码
     *
     * @param password          明文密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    private boolean verifyPassword(String password, String encryptedPassword) {
        // TODO: 实现密码验证逻辑
        // 这里简单实现，实际应该使用BCrypt等加密算法
        return (password + "_encrypted").equals(encryptedPassword);
    }
    
    /**
     * 生成随机用户名
     *
     * @return 随机用户名
     */
    private String generateRandomUsername() {
        // 生成随机用户名
        return "user_" + System.currentTimeMillis();
    }
} 