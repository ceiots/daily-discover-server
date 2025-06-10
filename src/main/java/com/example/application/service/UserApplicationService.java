package com.example.application.service;

import com.example.application.assembler.UserAssembler;
import com.example.application.dto.UserDTO;
import com.example.common.exception.BusinessException;
import com.example.common.model.PageResult;
import com.example.domain.model.user.User;
import com.example.domain.repository.UserRepository;
import com.example.infrastructure.security.PasswordEncoderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户应用服务
 */
@Service
public class UserApplicationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAssembler userAssembler;

    @Autowired
    private PasswordEncoderImpl passwordEncoder;

    /**
     * 用户注册
     *
     * @param userDTO 用户信息
     * @return 注册后的用户信息
     */
    @Transactional
    public UserDTO register(UserDTO userDTO) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(userDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new BusinessException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (userDTO.getPhone() != null) {
            Optional<User> userByPhone = userRepository.findByPhone(userDTO.getPhone());
            if (userByPhone.isPresent()) {
                throw new BusinessException("手机号已被注册");
            }
        }

        // 检查邮箱是否已存在
        if (userDTO.getEmail() != null) {
            Optional<User> userByEmail = userRepository.findByEmail(userDTO.getEmail());
            if (userByEmail.isPresent()) {
                throw new BusinessException("邮箱已被注册");
            }
        }

        // 转换为领域模型
        User user = userAssembler.toDomain(userDTO);

        // 设置默认值
        user.setStatus(1); // 正常状态
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 密码加密
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // 保存用户
        User savedUser = userRepository.save(user);

        // 转换为DTO并返回
        return userAssembler.toDTO(savedUser);
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录用户信息
     */
    public UserDTO login(String username, String password) {
        // 查询用户
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new BusinessException("用户名或密码错误");
        }

        User user = optionalUser.get();

        // 检查用户状态
        if (!user.isEnabled()) {
            throw new BusinessException("账户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // 更新登录时间
        user.recordLogin();
        userRepository.update(user);

        // 转换为DTO并返回
        return userAssembler.toDTO(user);
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     */
    public UserDTO getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new BusinessException("用户不存在");
        }

        return userAssembler.toDTO(optionalUser.get());
    }

    /**
     * 更新用户信息
     *
     * @param userDTO 用户信息
     * @return 更新后的用户信息
     */
    @Transactional
    public UserDTO updateUser(UserDTO userDTO) {
        // 查询用户
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        if (!optionalUser.isPresent()) {
            throw new BusinessException("用户不存在");
        }

        User user = optionalUser.get();

        // 更新用户信息
        user.updateProfile(
                userDTO.getNickname(),
                userDTO.getAvatar(),
                userDTO.getGender(),
                userDTO.getBirthday()
        );

        // 保存更新
        User updatedUser = userRepository.update(user);

        // 转换为DTO并返回
        return userAssembler.toDTO(updatedUser);
    }

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Transactional
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new BusinessException("用户不存在");
        }

        User user = optionalUser.get();

        // 修改密码
        boolean success = user.changePassword(oldPassword, newPassword, passwordEncoder);
        if (!success) {
            throw new BusinessException("原密码错误");
        }

        // 保存更新
        userRepository.update(user);

        return true;
    }

    /**
     * 分页查询用户
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public PageResult<UserDTO> getUserByPage(int pageNum, int pageSize) {
        // 查询总数
        long total = userRepository.count();

        // 分页查询
        List<User> users = userRepository.findByPage(pageNum, pageSize);

        // 转换为DTO
        List<UserDTO> userDTOs = userAssembler.toDTOList(users);

        // 返回分页结果
        return new PageResult<>(total, userDTOs, pageNum, pageSize);
    }

    /**
     * 禁用用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional
    public boolean disableUser(Long userId) {
        // 查询用户
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new BusinessException("用户不存在");
        }

        User user = optionalUser.get();

        // 禁用用户
        user.disable();

        // 保存更新
        userRepository.update(user);

        return true;
    }

    /**
     * 启用用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional
    public boolean enableUser(Long userId) {
        // 查询用户
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new BusinessException("用户不存在");
        }

        User user = optionalUser.get();

        // 启用用户
        user.enable();

        // 保存更新
        userRepository.update(user);

        return true;
    }
} 