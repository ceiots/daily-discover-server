package com.example.user.api.controller;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.common.result.Result;
import com.example.user.api.vo.LoginVO;
import com.example.user.api.vo.RegisterVO;
import com.example.user.api.vo.UserProfileVO;
import com.example.user.api.vo.UserVO;
import com.example.user.application.dto.LoginDTO;
import com.example.user.application.dto.RegisterDTO;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserProfileDTO;
import com.example.user.application.service.UserService;
import com.example.user.domain.repository.UserQueryCondition;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<UserVO> register(@Valid @RequestBody RegisterVO registerVO) {
        RegisterDTO registerDTO = new RegisterDTO();
        BeanUtils.copyProperties(registerVO, registerDTO);
        UserDTO userDTO = userService.register(registerDTO);
        UserVO userVO = convertToUserVO(userDTO);
        return Result.success(userVO);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserVO> login(@Valid @RequestBody LoginVO loginVO) {
        LoginDTO loginDTO = new LoginDTO();
        BeanUtils.copyProperties(loginVO, loginDTO);
        UserDTO userDTO = userService.login(loginDTO);
        UserVO userVO = convertToUserVO(userDTO);
        return Result.success(userVO);
    }

    @ApiOperation("手机号验证码登录")
    @PostMapping("/login/mobile")
    public Result<UserVO> loginByMobileCode(
            @ApiParam("手机号") @RequestParam String mobile,
            @ApiParam("验证码") @RequestParam String code,
            @ApiParam("设备ID") @RequestParam(required = false) String deviceId,
            @ApiParam("设备类型") @RequestParam(required = false) Integer deviceType) {
        UserDTO userDTO = userService.loginByMobileCode(mobile, code, deviceId, deviceType);
        UserVO userVO = convertToUserVO(userDTO);
        return Result.success(userVO);
    }

    @ApiOperation("第三方登录")
    @PostMapping("/login/third-party")
    public Result<UserVO> loginByThirdParty(
            @ApiParam("第三方类型") @RequestParam String type,
            @ApiParam("开放ID") @RequestParam String openId,
            @ApiParam("设备ID") @RequestParam(required = false) String deviceId,
            @ApiParam("设备类型") @RequestParam(required = false) Integer deviceType) {
        UserDTO userDTO = userService.loginByThirdParty(type, openId, deviceId, deviceType);
        UserVO userVO = convertToUserVO(userDTO);
        return Result.success(userVO);
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserInfo(userId);
        UserVO userVO = convertToUserVO(userDTO);
        return Result.success(userVO);
    }

    @ApiOperation("更新用户信息")
    @PutMapping("/{userId}")
    public Result<UserVO> updateUserInfo(@PathVariable Long userId, @Valid @RequestBody UserVO userVO) {
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(userVO, userDTO);
        userDTO.setId(userId);
        UserDTO updatedUserDTO = userService.updateUserInfo(userDTO);
        UserVO updatedUserVO = convertToUserVO(updatedUserDTO);
        return Result.success(updatedUserVO);
    }

    @ApiOperation("修改密码")
    @PostMapping("/{userId}/password")
    public Result<Boolean> changePassword(
            @PathVariable Long userId,
            @ApiParam("旧密码") @RequestParam String oldPassword,
            @ApiParam("新密码") @RequestParam String newPassword) {
        boolean result = userService.changePassword(userId, oldPassword, newPassword);
        return Result.success(result);
    }

    @ApiOperation("重置密码")
    @PostMapping("/password/reset")
    public Result<Boolean> resetPassword(
            @ApiParam("手机号") @RequestParam String mobile,
            @ApiParam("验证码") @RequestParam String code,
            @ApiParam("新密码") @RequestParam String password) {
        boolean result = userService.resetPassword(mobile, code, password);
        return Result.success(result);
    }

    @ApiOperation("绑定手机号")
    @PostMapping("/{userId}/mobile")
    public Result<Boolean> bindMobile(
            @PathVariable Long userId,
            @ApiParam("手机号") @RequestParam String mobile,
            @ApiParam("验证码") @RequestParam String code) {
        boolean result = userService.bindMobile(userId, mobile, code);
        return Result.success(result);
    }

    @ApiOperation("绑定邮箱")
    @PostMapping("/{userId}/email")
    public Result<Boolean> bindEmail(
            @PathVariable Long userId,
            @ApiParam("邮箱") @RequestParam String email,
            @ApiParam("验证码") @RequestParam String code) {
        boolean result = userService.bindEmail(userId, email, code);
        return Result.success(result);
    }

    @ApiOperation("绑定第三方账号")
    @PostMapping("/{userId}/third-party")
    public Result<Boolean> bindThirdParty(
            @PathVariable Long userId,
            @ApiParam("第三方类型") @RequestParam String type,
            @ApiParam("开放ID") @RequestParam String openId) {
        boolean result = userService.bindThirdParty(userId, type, openId);
        return Result.success(result);
    }

    @ApiOperation("解绑第三方账号")
    @DeleteMapping("/{userId}/third-party")
    public Result<Boolean> unbindThirdParty(
            @PathVariable Long userId,
            @ApiParam("第三方类型") @RequestParam String type) {
        boolean result = userService.unbindThirdParty(userId, type);
        return Result.success(result);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{userId}/profile")
    public Result<UserProfileVO> getUserProfile(@PathVariable Long userId) {
        UserProfileDTO userProfileDTO = userService.getUserProfile(userId);
        UserProfileVO userProfileVO = convertToUserProfileVO(userProfileDTO);
        return Result.success(userProfileVO);
    }

    @ApiOperation("更新用户详情")
    @PutMapping("/{userId}/profile")
    public Result<UserProfileVO> updateUserProfile(@PathVariable Long userId, @Valid @RequestBody UserProfileVO userProfileVO) {
        UserProfileDTO userProfileDTO = new UserProfileDTO();
        BeanUtils.copyProperties(userProfileVO, userProfileDTO);
        userProfileDTO.setUserId(userId);
        UserProfileDTO updatedUserProfileDTO = userService.updateUserProfile(userProfileDTO);
        UserProfileVO updatedUserProfileVO = convertToUserProfileVO(updatedUserProfileDTO);
        return Result.success(updatedUserProfileVO);
    }

    @ApiOperation("分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserVO>> getUserPage(
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("手机号") @RequestParam(required = false) String mobile,
            @ApiParam("邮箱") @RequestParam(required = false) String email,
            @ApiParam("状态") @RequestParam(required = false) Integer status) {
        PageRequest pageRequest = new PageRequest(pageNum, pageSize);
        UserQueryCondition condition = new UserQueryCondition();
        condition.setUsername(username);
        condition.setMobile(mobile);
        condition.setEmail(email);
        if (status != null) {
            condition.setStatusList(List.of(status));
        }
        PageResult<UserDTO> pageResult = userService.getUserPage(pageRequest, condition);
        
        List<UserVO> userVOList = pageResult.getList().stream()
                .map(this::convertToUserVO)
                .collect(Collectors.toList());
        
        return Result.success(new PageResult<>(userVOList, pageResult.getTotal(), pageResult.getPages(), pageResult.getPageNum(), pageResult.getPageSize()));
    }

    @ApiOperation("禁用用户")
    @PutMapping("/{userId}/disable")
    public Result<Boolean> disableUser(@PathVariable Long userId) {
        boolean result = userService.disableUser(userId);
        return Result.success(result);
    }

    @ApiOperation("启用用户")
    @PutMapping("/{userId}/enable")
    public Result<Boolean> enableUser(@PathVariable Long userId) {
        boolean result = userService.enableUser(userId);
        return Result.success(result);
    }

    @ApiOperation("锁定用户")
    @PutMapping("/{userId}/lock")
    public Result<Boolean> lockUser(@PathVariable Long userId) {
        boolean result = userService.lockUser(userId);
        return Result.success(result);
    }

    @ApiOperation("解锁用户")
    @PutMapping("/{userId}/unlock")
    public Result<Boolean> unlockUser(@PathVariable Long userId) {
        boolean result = userService.unlockUser(userId);
        return Result.success(result);
    }

    /**
     * 将UserDTO转换为UserVO
     */
    private UserVO convertToUserVO(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDTO, userVO);
        return userVO;
    }

    /**
     * 将UserProfileDTO转换为UserProfileVO
     */
    private UserProfileVO convertToUserProfileVO(UserProfileDTO userProfileDTO) {
        if (userProfileDTO == null) {
            return null;
        }
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(userProfileDTO, userProfileVO);
        return userProfileVO;
    }
} 