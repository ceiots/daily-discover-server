package com.example.user.domain.service;

import com.example.user.domain.model.UserAuth;
import com.example.user.domain.model.UserLoginLog;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.valueobject.Password;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户领域服务接口
 */
public interface UserDomainService extends BaseDomainService {

    /**
     * 根据用户ID获取用户
     *
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserById(Long userId);

    /**
     * 根据用户ID获取用户
     *
     * @param userId 用户ID对象
     * @return 用户对象
     */
    User getUserById(UserId userId);

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> getUserByUsername(String username);

    /**
     * 根据手机号获取用户
     *
     * @param mobile 手机号
     * @return 用户对象
     */
    Optional<User> getUserByMobile(Mobile mobile);

    /**
     * 根据邮箱获取用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    Optional<User> getUserByEmail(Email email);

    /**
     * 注册用户
     *
     * @param user 用户对象
     * @param password 密码
     * @param registerIp 注册IP
     * @return 注册后的用户对象
     */
    User registerUser(User user, String password, String registerIp);

    /**
     * 使用密码登录
     *
     * @param username 用户名/手机号/邮箱
     * @param password 密码
     * @param loginIp 登录IP
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @return 登录用户
     */
    Optional<User> loginByPassword(String username, String password, String loginIp, String deviceId, Integer deviceType);

    /**
     * 使用手机验证码登录
     *
     * @param mobile 手机号
     * @param code 验证码
     * @param loginIp 登录IP
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @return 登录用户
     */
    Optional<User> loginByMobileCode(String mobile, String code, String loginIp, String deviceId, Integer deviceType);

    /**
     * 使用第三方账号登录
     *
     * @param type 第三方类型
     * @param openId 第三方唯一标识
     * @param nickname 昵称
     * @param avatar 头像
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @return 登录用户
     */
    Optional<User> loginByThirdParty(String type, String openId, String nickname, String avatar, String deviceId, Integer deviceType);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return 更新后的用户对象
     */
    User updateUser(User user);

    /**
     * 更新用户详细信息
     *
     * @param userProfile 用户详细信息对象
     * @return 更新后的用户详细信息对象
     */
    UserProfile updateUserProfile(UserProfile userProfile);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param password 新密码
     * @return 是否成功
     */
    boolean resetPassword(Long userId, String password);

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param mobile 手机号
     * @param code 验证码
     * @return 是否成功
     */
    boolean bindMobile(Long userId, String mobile, String code);

    /**
     * 绑定邮箱
     *
     * @param userId 用户ID
     * @param email 邮箱
     * @param code 验证码
     * @return 是否成功
     */
    boolean bindEmail(Long userId, String email, String code);

    /**
     * 绑定第三方账号
     *
     * @param userId 用户ID
     * @param type 第三方类型
     * @param openId 第三方唯一标识
     * @param unionId 第三方联合标识
     * @return 是否成功
     */
    boolean bindThirdParty(Long userId, String type, String openId, String unionId);

    /**
     * 解绑第三方账号
     *
     * @param userId 用户ID
     * @param type 第三方类型
     * @return 是否成功
     */
    boolean unbindThirdParty(Long userId, String type);

    /**
     * 验证手机验证码
     *
     * @param mobile 手机号
     * @param code 验证码
     * @return 是否有效
     */
    boolean verifyMobileCode(String mobile, String code);

    /**
     * 验证邮箱验证码
     *
     * @param email 邮箱
     * @param code 验证码
     * @return 是否有效
     */
    boolean verifyEmailCode(String email, String code);

    /**
     * 验证手机号格式
     *
     * @param mobile 手机号
     * @return 是否有效
     */
    boolean validateMobileFormat(String mobile);

    /**
     * 验证邮箱格式
     *
     * @param email 邮箱
     * @return 是否有效
     */
    boolean validateEmailFormat(String email);

    /**
     * 验证密码强度
     *
     * @param password 密码
     * @return 是否符合要求
     */
    boolean validatePasswordStrength(String password);

    /**
     * 记录用户登录日志
     *
     * @param loginLog 登录日志对象
     * @return 保存后的登录日志对象
     */
    UserLoginLog recordLoginLog(UserLoginLog loginLog);
    
    /**
     * 验证密码强度
     * 
     * @param password 密码
     * @return 是否符合强度要求
     */
    default boolean validatePasswordStrength(String password) {
        // 至少8位，包含大小写字母和数字
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
        return Pattern.matches(regex, password);
    }
    
    /**
     * 验证手机号格式
     * 
     * @param mobile 手机号
     * @return 是否有效
     */
    default boolean isValidMobile(String mobile) {
        return Mobile.isValid(mobile);
    }
    
    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱
     * @return 是否有效
     */
    default boolean isValidEmail(String email) {
        return Email.isValid(email);
    }
    
    /**
     * 生成加密密码
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    default String encryptPassword(String rawPassword) {
        return Password.create(rawPassword).getValue();
    }
    
    /**
     * 验证密码
     * 
     * @param rawPassword 原始密码
     * @param encryptedPassword 加密后的密码
     * @return 是否匹配
     */
    default boolean matchPassword(String rawPassword, String encryptedPassword) {
        return Password.matches(rawPassword, encryptedPassword);
    }
} 