package com.example.domain.model.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户领域模型
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密后）
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDateTime birthday;

    /**
     * 用户状态：0-禁用，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 用户角色列表
     */
    private List<Role> roles;

    // 构造函数
    public User() {
    }

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    // 领域行为
    /**
     * 修改密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param encoder     密码加密器
     * @return 是否修改成功
     */
    public boolean changePassword(String oldPassword, String newPassword, PasswordEncoder encoder) {
        if (encoder.matches(oldPassword, this.password)) {
            this.password = encoder.encode(newPassword);
            return true;
        }
        return false;
    }

    /**
     * 更新个人信息
     *
     * @param nickname 昵称
     * @param avatar   头像
     * @param gender   性别
     * @param birthday 生日
     */
    public void updateProfile(String nickname, String avatar, Integer gender, LocalDateTime birthday) {
        if (nickname != null) {
            this.nickname = nickname;
        }
        if (avatar != null) {
            this.avatar = avatar;
        }
        if (gender != null) {
            this.gender = gender;
        }
        if (birthday != null) {
            this.birthday = birthday;
        }
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 禁用账户
     */
    public void disable() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 启用账户
     */
    public void enable() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 记录登录
     */
    public void recordLogin() {
        this.lastLoginTime = LocalDateTime.now();
    }

    /**
     * 判断用户是否可用
     *
     * @return 是否可用
     */
    public boolean isEnabled() {
        return this.status == 1;
    }

    // Getter and Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
} 