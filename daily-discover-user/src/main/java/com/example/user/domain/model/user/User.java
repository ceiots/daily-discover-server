package com.example.user.domain.model.user;

import com.example.common.exception.BusinessException;
import com.example.user.infrastructure.common.result.ResultCode;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.valueobject.Password;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 用户聚合根
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private UserId id;
    
    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(UserId id) {
        this.id = id;
    }

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private Password password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private Mobile mobile;

    /**
     * 邮箱
     */
    private Email email;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 用户类型：1-普通用户，2-商家，3-管理员
     */
    private Integer userType;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;
    
    /**
     * 注册时间
     */
    private LocalDateTime registerTime;
    
    /**
     * 注册IP
     */
    private String registerIp;

    /**
     * 用户角色列表
     */
    private List<UserRole> roles;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标志：0-未删除，1-已删除
     */
    private Integer deleted;

    /**
     * 版本号，用于乐观锁
     */
    private Integer version;

    /**
     * 创建用户
     */
    public static User create(String username, String rawPassword, String nickname) {
        User user = new User();
        user.username = username;
        user.password = Password.create(rawPassword);
        user.nickname = nickname;
        user.gender = 0;
        user.status = 1;
        user.userType = 1;
        user.roles = new ArrayList<>();
        user.createTime = LocalDateTime.now();
        user.updateTime = LocalDateTime.now();
        user.deleted = 0;
        user.version = 0;
        return user;
    }

    /**
     * 添加角色
     */
    public void addRole(UserRole role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        if (!roles.contains(role)) {
            roles.add(role);
            updateTime = LocalDateTime.now();
        }
    }

    /**
     * 移除角色
     */
    public void removeRole(UserRole role) {
        if (roles != null) {
            roles.remove(role);
            updateTime = LocalDateTime.now();
        }
    }

    /**
     * 获取角色列表
     */
    public List<UserRole> getRoles() {
        return roles != null ? Collections.unmodifiableList(roles) : Collections.emptyList();
    }

    /**
     * 更新密码
     */
    public void changePassword(String oldPassword, String newPassword) {
        if (!password.matches(oldPassword)) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR, "密码错误");
        }
        password = Password.create(newPassword);
        updateTime = LocalDateTime.now();
        version++;
    }
    
    /**
     * 设置密码
     */
    public void setPassword(Password password) {
        this.password = password;
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 更新手机号
     */
    public void changeMobile(Mobile newMobile) {
        this.mobile = newMobile;
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 更新邮箱
     */
    public void changeEmail(Email newEmail) {
        this.email = newEmail;
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 更新基本信息
     */
    public void updateProfile(String nickname, String avatar, Integer gender) {
        if (nickname != null && !nickname.isEmpty()) {
            this.nickname = nickname;
        }
        if (avatar != null && !avatar.isEmpty()) {
            this.avatar = avatar;
        }
        if (gender != null) {
            this.gender = gender;
        }
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 设置注册信息
     */
    public void setRegisterInfo(String registerIp, LocalDateTime registerTime) {
        this.registerIp = registerIp;
        this.registerTime = registerTime;
        updateTime = LocalDateTime.now();
    }
    
    /**
     * 更新登录信息
     */
    public void updateLoginInfo(String loginIp, LocalDateTime loginTime) {
        this.lastLoginIp = loginIp;
        this.lastLoginTime = loginTime;
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 禁用用户
     */
    public void disable() {
        this.status = 0;
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 启用用户
     */
    public void enable() {
        this.status = 1;
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 记录登录信息
     */
    public void recordLogin(String ip) {
        this.lastLoginTime = LocalDateTime.now();
        this.lastLoginIp = ip;
        updateTime = LocalDateTime.now();
        version++;
    }

    /**
     * 是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否删除
     */
    public boolean isDeleted() {
        return this.deleted != null && this.deleted == 1;
    }

    /**
     * 标记为删除
     */
    public void markDeleted() {
        this.deleted = 1;
        updateTime = LocalDateTime.now();
        version++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 
 