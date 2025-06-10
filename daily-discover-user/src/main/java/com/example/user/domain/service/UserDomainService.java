package com.example.user.domain.service;

import com.example.user.domain.model.User;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;

import java.util.List;
import java.util.Optional;

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
     * @param userId 用户ID
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
     * 密码登录
     *
     * @param username 用户名/手机号/邮箱
     * @param password 密码
     * @param loginIp 登录IP
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @return 用户对象
     */
    Optional<User> loginByPassword(String username, String password, String loginIp, String deviceId, Integer deviceType);

    /**
     * 手机验证码登录
     *
     * @param mobile 手机号
     * @param code 验证码
     * @param loginIp 登录IP
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @return 用户对象
     */
    Optional<User> loginByMobileCode(String mobile, String code, String loginIp, String deviceId, Integer deviceType);

    /**
     * 第三方登录
     *
     * @param type 第三方类型
     * @param openId 开放ID
     * @param nickname 昵称
     * @param avatar 头像
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @return 用户对象
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
     * 更新用户详情
     *
     * @param userProfile 用户详情对象
     * @return 更新后的用户详情对象
     */
    UserProfile updateUserProfile(UserProfile userProfile);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param password 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(Long userId, String password);

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param mobile 手机号
     * @param code 验证码
     * @return 是否绑定成功
     */
    boolean bindMobile(Long userId, String mobile, String code);

    /**
     * 绑定邮箱
     *
     * @param userId 用户ID
     * @param email 邮箱
     * @param code 验证码
     * @return 是否绑定成功
     */
    boolean bindEmail(Long userId, String email, String code);

    /**
     * 绑定第三方账号
     *
     * @param userId 用户ID
     * @param type 第三方类型
     * @param openId 开放ID
     * @param unionId 联合ID
     * @return 是否绑定成功
     */
    boolean bindThirdParty(Long userId, String type, String openId, String unionId);

    /**
     * 解绑第三方账号
     *
     * @param userId 用户ID
     * @param type 第三方类型
     * @return 是否解绑成功
     */
    boolean unbindThirdParty(Long userId, String type);

    /**
     * 验证手机验证码
     *
     * @param mobile 手机号
     * @param code 验证码
     * @return 是否验证通过
     */
    boolean verifyMobileCode(String mobile, String code);

    /**
     * 验证邮箱验证码
     *
     * @param email 邮箱
     * @param code 验证码
     * @return 是否验证通过
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
} 