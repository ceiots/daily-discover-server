package com.example.application.assembler;

import com.example.application.dto.UserDTO;
import com.example.domain.model.user.Role;
import com.example.domain.model.user.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户模型转换器
 */
@Component
public class UserAssembler {
    /**
     * 领域模型转DTO
     *
     * @param user 用户领域模型
     * @return 用户DTO
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setPhone(user.getPhone());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setGender(user.getGender());
        dto.setBirthday(user.getBirthday());
        dto.setStatus(user.getStatus());
        dto.setCreateTime(user.getCreateTime());
        dto.setUpdateTime(user.getUpdateTime());
        dto.setLastLoginTime(user.getLastLoginTime());

        // 转换角色
        List<Role> roles = user.getRoles();
        if (roles != null && !roles.isEmpty()) {
            List<String> roleNames = roles.stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());
            dto.setRoles(roleNames);
        } else {
            dto.setRoles(Collections.emptyList());
        }

        return dto;
    }

    /**
     * DTO转领域模型
     *
     * @param dto 用户DTO
     * @return 用户领域模型
     */
    public User toDomain(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setAvatar(dto.getAvatar());
        user.setGender(dto.getGender());
        user.setBirthday(dto.getBirthday());
        user.setStatus(dto.getStatus());
        user.setCreateTime(dto.getCreateTime());
        user.setUpdateTime(dto.getUpdateTime());
        user.setLastLoginTime(dto.getLastLoginTime());

        // 角色转换在实际应用中需要通过角色服务查询完整的角色信息
        return user;
    }

    /**
     * 领域模型列表转DTO列表
     *
     * @param users 用户领域模型列表
     * @return 用户DTO列表
     */
    public List<UserDTO> toDTOList(List<User> users) {
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
} 