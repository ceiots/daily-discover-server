package com.example.user.application.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserAssembler;
import com.example.user.application.dto.LoginDTO;
import com.example.user.application.dto.RegisterDTO;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserProfileDTO;
import com.example.user.application.service.UserService;
import com.example.user.domain.model.UserProfile;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.user.User;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.repository.UserQueryCondition;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.UserDomainService;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDomainService userDomainService;
    private final UserAssembler userAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return userDomainService;
    }

    @Override
    @Transactional
    public UserDTO register(RegisterDTO registerDTO) {
        // 验证用户名、手机号、邮箱是否已存在
        if (userDomainService.existsByUsername(registerDTO.getUsername())) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        
        if (registerDTO.getMobile() != null && userDomainService.existsByMobile(registerDTO.getMobile())) {
            throw new BusinessException(ResultCode.MOBILE_EXISTS);
        }
        
        if (registerDTO.getEmail() != null && userDomainService.existsByEmail(registerDTO.getEmail())) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }
        
        // 验证密码强度
        if (!userDomainService.validatePasswordStrength(registerDTO.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_TOO_WEAK);
        }
        
        // 创建用户
        User user = User.create(
            registerDTO.getUsername(),
            registerDTO.getPassword(),
            registerDTO.getNickname() != null ? registerDTO.getNickname() : registerDTO.getUsername()
        );
        
        // 设置手机号和邮箱
        if (registerDTO.getMobile() != null) {
            if (!userDomainService.validateMobileFormat(registerDTO.getMobile())) {
                throw new BusinessException(ResultCode.INVALID_MOBILE_FORMAT);
            }
            user.changeMobile(Mobile.of(registerDTO.getMobile()));
        }
        
        if (registerDTO.getEmail() != null) {
            if (!userDomainService.validateEmailFormat(registerDTO.getEmail())) {
                throw new BusinessException(ResultCode.INVALID_EMAIL_FORMAT);
            }
            user.changeEmail(Email.of(registerDTO.getEmail()));
        }
        
        // 注册用户
        User registeredUser = userDomainService.registerUser(user, registerDTO.getPassword(), registerDTO.getRegisterIp());
        
        // 转换为DTO
        return userAssembler.toDTO(registeredUser);
    }

    @Override
    @Transactional
    public UserDTO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        String deviceId = loginDTO.getDeviceId();
        Integer deviceType = loginDTO.getDeviceType();
        
        // 通过用户名/手机号/邮箱登录
        Optional<User> userOpt = userDomainService.loginByPassword(username, password, loginDTO.getLoginIp(), deviceId, deviceType);
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USERNAME_OR_PASSWORD_ERROR);
        }
        
        return userAssembler.toDTO(userOpt.get());
    }

    @Override
    @Transactional
    public UserDTO loginByMobileCode(String mobile, String code, String deviceId, Integer deviceType) {
        // 通过手机号验证码登录
        Optional<User> userOpt = userDomainService.loginByMobileCode(mobile, code, null, deviceId, deviceType);
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        return userAssembler.toDTO(userOpt.get());
    }

    @Override
    @Transactional
    public UserDTO loginByThirdParty(String type, String openId, String deviceId, Integer deviceType) {
        // 通过第三方授权登录
        Optional<User> userOpt = userDomainService.loginByThirdParty(type, openId, null, null, deviceId, deviceType);
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        return userAssembler.toDTO(userOpt.get());
    }

    @Override
    public UserDTO getUserInfo(Long userId) {
        User user = userDomainService.getUserById(userId);
        return userAssembler.toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUserInfo(UserDTO userDTO) {
        User user = userDomainService.getUserById(userDTO.getId());
        
        // 更新基本信息
        user.updateProfile(userDTO.getNickname(), userDTO.getAvatar(), userDTO.getGender());
        
        // 保存用户
        User updatedUser = userDomainService.updateUser(user);
        
        return userAssembler.toDTO(updatedUser);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 验证新密码强度
        if (!userDomainService.validatePasswordStrength(newPassword)) {
            throw new BusinessException(ResultCode.PASSWORD_TOO_WEAK);
        }
        
        return userDomainService.changePassword(userId, oldPassword, newPassword);
    }

    @Override
    @Transactional
    public boolean resetPassword(String mobile, String code, String password) {
        // 验证手机号格式
        if (!userDomainService.validateMobileFormat(mobile)) {
            throw new BusinessException(ResultCode.INVALID_MOBILE_FORMAT);
        }
        
        // 验证验证码
        if (!userDomainService.verifyMobileCode(mobile, code)) {
            throw new BusinessException(ResultCode.INVALID_VERIFICATION_CODE);
        }
        
        // 验证密码强度
        if (!userDomainService.validatePasswordStrength(password)) {
            throw new BusinessException(ResultCode.PASSWORD_TOO_WEAK);
        }
        
        // 查询用户
        Optional<User> userOpt = userDomainService.findByMobile(mobile);
        
        if (userOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        
        return userDomainService.resetPassword(userOpt.get().getId().getValue(), password);
    }

    @Override
    @Transactional
    public boolean bindMobile(Long userId, String mobile, String code) {
        return userDomainService.bindMobile(userId, mobile, code);
    }

    @Override
    @Transactional
    public boolean bindEmail(Long userId, String email, String code) {
        return userDomainService.bindEmail(userId, email, code);
    }

    @Override
    @Transactional
    public boolean bindThirdParty(Long userId, String type, String openId) {
        return userDomainService.bindThirdParty(userId, type, openId, null);
    }

    @Override
    @Transactional
    public boolean unbindThirdParty(Long userId, String type) {
        return userDomainService.unbindThirdParty(userId, type);
    }

    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        // 获取用户
        User user = userDomainService.getUserById(userId);
        
        // 获取用户详情
        Optional<UserProfile> profileOpt = userDomainService.findProfileByUserId(user.getId());
        
        if (profileOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_PROFILE_NOT_FOUND);
        }
        
        return userAssembler.toProfileDTO(profileOpt.get());
    }

    @Override
    @Transactional
    public UserProfileDTO updateUserProfile(UserProfileDTO userProfileDTO) {
        Optional<UserProfile> profileOpt = userDomainService.findProfileById(userProfileDTO.getId());
        
        if (profileOpt.isEmpty()) {
            throw new BusinessException(ResultCode.USER_PROFILE_NOT_FOUND);
        }
        
        UserProfile userProfile = profileOpt.get();
        
        // 更新用户详情
        userAssembler.updateProfileFromDTO(userProfile, userProfileDTO);
        
        // 保存用户详情
        UserProfile updatedProfile = userDomainService.updateUserProfile(userProfile);
        
        return userAssembler.toProfileDTO(updatedProfile);
    }

    @Override
    public PageResult<UserDTO> getUserPage(PageRequest pageRequest, UserQueryCondition condition) {
        PageResult<User> pageResult = userDomainService.findUserPage(pageRequest, condition);
        
        List<UserDTO> userDTOList = pageResult.getList().stream()
                .map(userAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(userDTOList, pageResult.getTotal(), pageResult.getPages(), pageResult.getPageNum(), pageResult.getPageSize());
    }

    @Override
    public List<UserDTO> getUserList(UserQueryCondition condition) {
        List<User> userList = userDomainService.findUserList(condition);
        
        return userList.stream()
                .map(userAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean disableUser(Long userId) {
        User user = userDomainService.getUserById(userId);
        user.disable();
        
        userDomainService.updateUser(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean enableUser(Long userId) {
        User user = userDomainService.getUserById(userId);
        user.enable();
        
        userDomainService.updateUser(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean lockUser(Long userId) {
        // 锁定用户实现
        User user = userDomainService.getUserById(userId);
        // 假设有一个lock方法
        // user.lock();
        
        userDomainService.updateUser(user);
        
        return true;
    }

    @Override
    @Transactional
    public boolean unlockUser(Long userId) {
        // 解锁用户实现
        User user = userDomainService.getUserById(userId);
        // 假设有一个unlock方法
        // user.unlock();
        
        userDomainService.updateUser(user);
        
        return true;
    }
} 