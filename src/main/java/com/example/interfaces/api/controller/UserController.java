package com.example.interfaces.api.controller;

import com.example.application.dto.UserDTO;
import com.example.application.service.UserApplicationService;
import com.example.common.model.PageResult;
import com.example.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserApplicationService userApplicationService;

    /**
     * 用户注册
     *
     * @param userDTO 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userApplicationService.register(userDTO);
        return Result.success(registeredUser);
    }

    /**
     * 用户登录
     *
     * @param loginParam 登录参数
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginParam) {
        String username = loginParam.get("username");
        String password = loginParam.get("password");

        UserDTO userDTO = userApplicationService.login(username, password);

        // 这里可以生成token等信息
        Map<String, Object> result = new HashMap<>();
        result.put("user", userDTO);
        // 实际项目中，这里应该生成JWT token
        result.put("token", "mock-token");

        return Result.success(result);
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public Result<UserDTO> getUserInfo(@PathVariable Long id) {
        UserDTO userDTO = userApplicationService.getUserById(id);
        return Result.success(userDTO);
    }

    /**
     * 更新用户信息
     *
     * @param id      用户ID
     * @param userDTO 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        userDTO.setId(id);
        UserDTO updatedUser = userApplicationService.updateUser(userDTO);
        return Result.success(updatedUser);
    }

    /**
     * 修改密码
     *
     * @param id          用户ID
     * @param passwordMap 密码信息
     * @return 修改结果
     */
    @PutMapping("/{id}/password")
    public Result<Boolean> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordMap) {
        String oldPassword = passwordMap.get("oldPassword");
        String newPassword = passwordMap.get("newPassword");

        boolean success = userApplicationService.changePassword(id, oldPassword, newPassword);
        return Result.success(success);
    }

    /**
     * 分页查询用户
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 用户列表
     */
    @GetMapping
    public Result<PageResult<UserDTO>> getUserList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<UserDTO> pageResult = userApplicationService.getUserByPage(pageNum, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 禁用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/disable")
    public Result<Boolean> disableUser(@PathVariable Long id) {
        boolean success = userApplicationService.disableUser(id);
        return Result.success(success);
    }

    /**
     * 启用用户
     *
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/enable")
    public Result<Boolean> enableUser(@PathVariable Long id) {
        boolean success = userApplicationService.enableUser(id);
        return Result.success(success);
    }
} 