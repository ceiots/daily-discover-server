package com.example.user.infrastructure.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员等级实体
 */
@Data
@Accessors(chain = true)
@TableName("member_level")
public class MemberLevelEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    private BigDecimal discount;

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
     * 是否包邮:0-否,1-是
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