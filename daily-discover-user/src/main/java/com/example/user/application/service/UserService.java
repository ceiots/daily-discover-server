package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.LoginDTO;
import com.example.user.application.dto.RegisterDTO;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserProfileDTO;
import com.example.user.domain.repository.UserQueryCondition;

import java.util.List;

/**
 * 用户应用服务接口
 */
public interface UserService extends BaseApplicationService {

    /**
     * 用户注册
     *
     * @param registerDTO 注册参数
     * @return 用户DTO
     */
    UserDTO register(RegisterDTO registerDTO);

    /**
     * 用户登录
     *
     * @param loginDTO 登录参数
     * @return 用户DTO
     */
    UserDTO login(LoginDTO loginDTO);

    /**
     * 手机号验证码登录
     *
     * @param mobile     手机号
     * @param code       验证码
     * @param deviceId   设备ID
     * @param deviceType 设备类型
     * @return 用户DTO
     */
    UserDTO loginByMobileCode(String mobile, String code, String deviceId, Integer deviceType);

    /**
     * 第三方登录
     *
     * @param type       第三方类型
     * @param openId     开放ID
     * @param deviceId   设备ID
     * @param deviceType 设备类型
     * @return 用户DTO
     */
    UserDTO loginByThirdParty(String type, String openId, String deviceId, Integer deviceType);

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户DTO
     */
    UserDTO getUserInfo(Long userId);

    /**
     * 更新用户信息
     *
     * @param userDTO 用户DTO
     * @return 更新后的用户DTO
     */
    UserDTO updateUserInfo(UserDTO userDTO);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置密码
     *
     * @param mobile   手机号
     * @param code     验证码
     * @param password 新密码
     * @return 是否重置成功
     */
    boolean resetPassword(String mobile, String code, String password);

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param mobile 手机号
     * @param code   验证码
     * @return 是否绑定成功
     */
    boolean bindMobile(Long userId, String mobile, String code);

    /**
     * 绑定邮箱
     *
     * @param userId 用户ID
     * @param email  邮箱
     * @param code   验证码
     * @return 是否绑定成功
     */
    boolean bindEmail(Long userId, String email, String code);

    /**
     * 绑定第三方账号
     *
     * @param userId 用户ID
     * @param type   第三方类型
     * @param openId 开放ID
     * @return 是否绑定成功
     */
    boolean bindThirdParty(Long userId, String type, String openId);

    /**
     * 解绑第三方账号
     *
     * @param userId 用户ID
     * @param type   第三方类型
     * @return 是否解绑成功
     */
    boolean unbindThirdParty(Long userId, String type);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情DTO
     */
    UserProfileDTO getUserProfile(Long userId);

    /**
     * 更新用户详情
     *
     * @param userProfileDTO 用户详情DTO
     * @return 更新后的用户详情DTO
     */
    UserProfileDTO updateUserProfile(UserProfileDTO userProfileDTO);

    /**
     * 分页查询用户
     *
     * @param pageRequest 分页参数
     * @param condition   查询条件
     * @return 用户分页结果
     */
    PageResult<UserDTO> getUserPage(PageRequest pageRequest, UserQueryCondition condition);

    /**
     * 查询用户列表
     *
     * @param condition 查询条件
     * @return 用户列表
     */
    List<UserDTO> getUserList(UserQueryCondition condition);

    /**
     * 禁用用户
     *
     * @param userId 用户ID
     * @return 是否禁用成功
     */
    boolean disableUser(Long userId);

    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return 是否启用成功
     */
    boolean enableUser(Long userId);

    /**
     * 锁定用户
     *
     * @param userId 用户ID
     * @return 是否锁定成功
     */
    boolean lockUser(Long userId);

    /**
     * 解锁用户
     *
     * @param userId 用户ID
     * @return 是否解锁成功
     */
    boolean unlockUser(Long userId);
} 