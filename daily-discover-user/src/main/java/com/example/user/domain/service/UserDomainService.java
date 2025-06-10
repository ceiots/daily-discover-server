package com.example.user.domain.service;

import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;

/**
 * 用户领域服务接口
 */
public interface UserDomainService {
    /**
     * 注册用户
     *
     * @param username 用户名
     * @param password 密码
     * @param nickname 昵称
     * @return 用户
     */
    User register(String username, String password, String nickname);

    /**
     * 手机号注册
     *
     * @param mobile 手机号
     * @param password 密码
     * @param nickname 昵称
     * @return 用户
     */
    User registerByMobile(Mobile mobile, String password, String nickname);

    /**
     * 邮箱注册
     *
     * @param email 邮箱
     * @param password 密码
     * @param nickname 昵称
     * @return 用户
     */
    User registerByEmail(Email email, String password, String nickname);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @param ip 登录IP
     * @return 用户
     */
    User login(String username, String password, String ip);

    /**
     * 手机号登录
     *
     * @param mobile 手机号
     * @param code 验证码
     * @param ip 登录IP
     * @return 用户
     */
    User loginByMobile(Mobile mobile, String code, String ip);

    /**
     * 邮箱登录
     *
     * @param email 邮箱
     * @param password 密码
     * @param ip 登录IP
     * @return 用户
     */
    User loginByEmail(Email email, String password, String ip);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(UserId userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean resetPassword(UserId userId, String newPassword);

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param mobile 手机号
     * @param code 验证码
     * @return 是否成功
     */
    boolean bindMobile(UserId userId, Mobile mobile, String code);

    /**
     * 绑定邮箱
     *
     * @param userId 用户ID
     * @param email 邮箱
     * @param code 验证码
     * @return 是否成功
     */
    boolean bindEmail(UserId userId, Email email, String code);

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param nickname 昵称
     * @param avatar 头像
     * @param gender 性别
     * @return 更新后的用户
     */
    User updateUserInfo(UserId userId, String nickname, String avatar, Integer gender);
} 