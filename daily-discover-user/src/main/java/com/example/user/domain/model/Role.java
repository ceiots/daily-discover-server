package com.example.user.domain.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色领域模型
 */
@Data
@Accessors(chain = true)
public class Role implements Serializable {
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
     * 是否启用
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }

    /**
     * 是否删除
     *
     * @return 是否删除
     */
    public boolean isDeleted() {
        return this.deleted != null && this.deleted == 1;
    }
} 