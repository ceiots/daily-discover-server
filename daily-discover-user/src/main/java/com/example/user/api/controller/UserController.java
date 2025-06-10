package com.example.user.api.controller;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.common.result.Result;
import com.example.user.api.vo.LoginVO;
import com.example.user.api.vo.RegisterVO;
import com.example.user.api.vo.UserProfileVO;
import com.example.user.api.vo.UserVO;
import com.example.user.application.assembler.UserAssembler;
import com.example.user.application.dto.LoginDTO;
import com.example.user.application.dto.RegisterDTO;
import com.example.user.application.dto.UserDTO;
import com.example.user.application.dto.UserProfileDTO;
import com.example.user.application.service.UserService;
import com.example.user.domain.repository.UserQueryCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAssembler userAssembler;

    /**
     * 用户注册
     *
     * @param registerVO 注册参数
     * @param request    HTTP请求
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody @Valid RegisterVO registerVO, HttpServletRequest request) {
        // 构建注册DTO
        RegisterDTO registerDTO = new RegisterDTO()
                .setUsername(registerVO.getUsername())
                .setPassword(registerVO.getPassword())
                .setConfirmPassword(registerVO.getConfirmPassword())
                .setNickname(registerVO.getNickname())
                .setMobile(registerVO.getMobile())
                .setEmail(registerVO.getEmail())
                .setCode(registerVO.getCode())
                .setCodeType(registerVO.getCodeType())
                .setRegisterIp(request.getRemoteAddr())
                .setDeviceId(registerVO.getDeviceId())
                .setDeviceType(registerVO.getDeviceType())
                .setDeviceModel(registerVO.getDeviceModel())
                .setOsVersion(registerVO.getOsVersion())
                .setAppVersion(registerVO.getAppVersion());

        // 调用服务
        UserDTO userDTO = userService.register(registerDTO);

        // 转换为VO
        UserVO userVO = userAssembler.toVO(userDTO);

        return Result.success(userVO);
    }

    /**
     * 用户登录
     *
     * @param loginVO 登录参数
     * @param request HTTP请求
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<UserVO> login(@RequestBody @Valid LoginVO loginVO, HttpServletRequest request) {
        // 构建登录DTO
        LoginDTO loginDTO = new LoginDTO()
                .setUsername(loginVO.getUsername())
                .setPassword(loginVO.getPassword())
                .setDeviceId(loginVO.getDeviceId())
                .setDeviceType(loginVO.getDeviceType())
                .setDeviceModel(loginVO.getDeviceModel())
                .setOsVersion(loginVO.getOsVersion())
                .setAppVersion(loginVO.getAppVersion())
                .setLoginIp(request.getRemoteAddr());

        // 调用服务
        UserDTO userDTO = userService.login(loginDTO);

        // 转换为VO
        UserVO userVO = userAssembler.toVO(userDTO);

        return Result.success(userVO);
    }

    /**
     * 手机号验证码登录
     *
     * @param mobile     手机号
     * @param code       验证码
     * @param deviceId   设备ID
     * @param deviceType 设备类型
     * @return 登录结果
     */
    @PostMapping("/login/mobile")
    public Result<UserVO> loginByMobile(@RequestParam String mobile, @RequestParam String code,
                                       @RequestParam(required = false) String deviceId,
                                       @RequestParam(required = false, defaultValue = "5") Integer deviceType) {
        // 调用服务
        UserDTO userDTO = userService.loginByMobileCode(mobile, code, deviceId, deviceType);

        // 转换为VO
        UserVO userVO = userAssembler.toVO(userDTO);

        return Result.success(userVO);
    }

    /**
     * 第三方登录
     *
     * @param type       第三方类型
     * @param openId     开放ID
     * @param deviceId   设备ID
     * @param deviceType 设备类型
     * @return 登录结果
     */
    @PostMapping("/login/third-party")
    public Result<UserVO> loginByThirdParty(@RequestParam String type, @RequestParam String openId,
                                           @RequestParam(required = false) String deviceId,
                                           @RequestParam(required = false, defaultValue = "5") Integer deviceType) {
        // 调用服务
        UserDTO userDTO = userService.loginByThirdParty(type, openId, deviceId, deviceType);

        // 转换为VO
        UserVO userVO = userAssembler.toVO(userDTO);

        return Result.success(userVO);
    }

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/{userId}")
    public Result<UserVO> getUserInfo(@PathVariable Long userId) {
        // 调用服务
        UserDTO userDTO = userService.getUserInfo(userId);

        // 转换为VO
        UserVO userVO = userAssembler.toVO(userDTO);

        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     *
     * @param userId 用户ID
     * @param userVO 用户信息
     * @return 更新结果
     */
    @PutMapping("/{userId}")
    public Result<UserVO> updateUserInfo(@PathVariable Long userId, @RequestBody @Valid UserVO userVO) {
        // 构建DTO
        UserDTO userDTO = new UserDTO()
                .setId(userId)
                .setNickname(userVO.getNickname())
                .setAvatar(userVO.getAvatar())
                .setGender(userVO.getGender());

        // 调用服务
        UserDTO updatedUserDTO = userService.updateUserInfo(userDTO);

        // 转换为VO
        UserVO updatedUserVO = userAssembler.toVO(updatedUserDTO);

        return Result.success(updatedUserVO);
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PutMapping("/{userId}/password")
    public Result<Boolean> changePassword(@PathVariable Long userId, @RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        // 调用服务
        boolean result = userService.changePassword(userId, oldPassword, newPassword);

        return Result.success(result);
    }

    /**
     * 重置密码
     *
     * @param mobile   手机号
     * @param code     验证码
     * @param password 新密码
     * @return 重置结果
     */
    @PutMapping("/password/reset")
    public Result<Boolean> resetPassword(@RequestParam String mobile, @RequestParam String code,
                                        @RequestParam String password) {
        // 调用服务
        boolean result = userService.resetPassword(mobile, code, password);

        return Result.success(result);
    }

    /**
     * 绑定手机号
     *
     * @param userId 用户ID
     * @param mobile 手机号
     * @param code   验证码
     * @return 绑定结果
     */
    @PostMapping("/{userId}/mobile")
    public Result<Boolean> bindMobile(@PathVariable Long userId, @RequestParam String mobile,
                                     @RequestParam String code) {
        // 调用服务
        boolean result = userService.bindMobile(userId, mobile, code);

        return Result.success(result);
    }

    /**
     * 绑定邮箱
     *
     * @param userId 用户ID
     * @param email  邮箱
     * @param code   验证码
     * @return 绑定结果
     */
    @PostMapping("/{userId}/email")
    public Result<Boolean> bindEmail(@PathVariable Long userId, @RequestParam String email,
                                    @RequestParam String code) {
        // 调用服务
        boolean result = userService.bindEmail(userId, email, code);

        return Result.success(result);
    }

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("/{userId}/profile")
    public Result<UserProfileVO> getUserProfile(@PathVariable Long userId) {
        // 调用服务
        UserProfileDTO userProfileDTO = userService.getUserProfile(userId);

        // 转换为VO
        UserProfileVO userProfileVO = userAssembler.toProfileVO(userProfileDTO);

        return Result.success(userProfileVO);
    }

    /**
     * 更新用户详情
     *
     * @param userId        用户ID
     * @param userProfileVO 用户详情
     * @return 更新结果
     */
    @PutMapping("/{userId}/profile")
    public Result<UserProfileVO> updateUserProfile(@PathVariable Long userId, @RequestBody @Valid UserProfileVO userProfileVO) {
        // 构建DTO
        UserProfileDTO userProfileDTO = new UserProfileDTO()
                .setId(userProfileVO.getId())
                .setUserId(userId)
                .setRealName(userProfileVO.getRealName())
                .setIdCardType(userProfileVO.getIdCardType())
                .setIdCardNo(userProfileVO.getIdCardNo())
                .setBirthday(userProfileVO.getBirthday())
                .setAge(userProfileVO.getAge())
                .setEducation(userProfileVO.getEducation())
                .setProfession(userProfileVO.getProfession())
                .setIncomeLevel(userProfileVO.getIncomeLevel())
                .setBio(userProfileVO.getBio())
                .setProvince(userProfileVO.getProvince())
                .setCity(userProfileVO.getCity())
                .setDistrict(userProfileVO.getDistrict())
                .setAddress(userProfileVO.getAddress())
                .setRemark(userProfileVO.getRemark());

        // 调用服务
        UserProfileDTO updatedUserProfileDTO = userService.updateUserProfile(userProfileDTO);

        // 转换为VO
        UserProfileVO updatedUserProfileVO = userAssembler.toProfileVO(updatedUserProfileDTO);

        return Result.success(updatedUserProfileVO);
    }

    /**
     * 分页查询用户
     *
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @param username 用户名
     * @param mobile   手机号
     * @param email    邮箱
     * @param status   状态
     * @return 用户分页结果
     */
    @GetMapping
    public Result<PageResult<UserVO>> getUserPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize,
                                                @RequestParam(required = false) String username,
                                                @RequestParam(required = false) String mobile,
                                                @RequestParam(required = false) String email,
                                                @RequestParam(required = false) Integer status) {
        // 构建分页请求
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageNum(pageNum);
        pageRequest.setPageSize(pageSize);

        // 构建查询条件
        UserQueryCondition condition = new UserQueryCondition();
        condition.setUsername(username);
        condition.setMobile(mobile);
        condition.setEmail(email);
        if (status != null) {
            List<Integer> statusList = new ArrayList<>();
            statusList.add(status);
            condition.setStatusList(statusList);
        }

        // 调用服务
        PageResult<UserDTO> pageResult = userService.getUserPage(pageRequest, condition);

        // 转换为VO
        List<UserVO> userVOList = pageResult.getList().stream()
                .map(userAssembler::toVO)
                .collect(Collectors.toList());

        // 构建分页结果
        PageResult<UserVO> voPageResult = new PageResult<>();
        voPageResult.setList(userVOList);
        voPageResult.setTotal(pageResult.getTotal());
        voPageResult.setPages(pageResult.getPages());
        voPageResult.setPageNum(pageResult.getPageNum());
        voPageResult.setPageSize(pageResult.getPageSize());

        return Result.success(voPageResult);
    }

    /**
     * 禁用用户
     *
     * @param userId 用户ID
     * @return 禁用结果
     */
    @PutMapping("/{userId}/disable")
    public Result<Boolean> disableUser(@PathVariable Long userId) {
        // 调用服务
        boolean result = userService.disableUser(userId);

        return Result.success(result);
    }

    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return 启用结果
     */
    @PutMapping("/{userId}/enable")
    public Result<Boolean> enableUser(@PathVariable Long userId) {
        // 调用服务
        boolean result = userService.enableUser(userId);

        return Result.success(result);
    }
} 