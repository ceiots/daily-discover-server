package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员等级DTO
 */
@Data
@Accessors(chain = true)
public class MemberLevelDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 等级ID
     */
    private Long id;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String name;

    /**
     * 等级图标
     */
    private String icon;

    /**
     * 成长值下限
     */
    private Integer growthMin;

    /**
     * 成长值上限
     */
    private Integer growthMax;

    /**
     * 折扣
     */
    private String discount;

    /**
     * 描述
     */
    private String description;

    /**
     * 会员权益
     */
    private String benefits;

    /**
     * 状态:0-禁用,1-启用
     */
    private Integer status;

    /**
     * 是否包邮
     */
    private Boolean freeShipping;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 