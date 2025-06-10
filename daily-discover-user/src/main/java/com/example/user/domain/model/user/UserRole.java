package com.example.user.domain.model.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户角色实体
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色编码
     */
    private String code;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

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
     * 创建角色
     */
    public static UserRole create(String code, String name, String description) {
        UserRole role = new UserRole();
        role.code = code;
        role.name = name;
        role.description = description;
        role.status = 1;
        role.sort = 0;
        role.createTime = LocalDateTime.now();
        role.updateTime = LocalDateTime.now();
        role.deleted = 0;
        return role;
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
     * 禁用角色
     */
    public void disable() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 启用角色
     */
    public void enable() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新角色信息
     */
    public void update(String name, String description, Integer sort) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        if (sort != null) {
            this.sort = sort;
        }
        this.updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRole role = (UserRole) o;
        return Objects.equals(id, role.id) || Objects.equals(code, role.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }
} 