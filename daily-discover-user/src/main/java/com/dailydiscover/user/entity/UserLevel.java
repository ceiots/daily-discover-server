package com.dailydiscover.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户等级配置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_levels")
public class UserLevel {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 等级名称
     */
    @TableField("level_name")
    private String levelName;

    /**
     * 最小积分要求
     */
    @TableField("min_points")
    private Integer minPoints;

    /**
     * 最大积分要求
     */
    @TableField("max_points")
    private Integer maxPoints;

    /**
     * 等级颜色
     */
    @TableField("color")
    private String color;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}