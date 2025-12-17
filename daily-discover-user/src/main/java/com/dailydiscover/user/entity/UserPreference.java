package com.dailydiscover.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户偏好设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_preferences")
public class UserPreference {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 偏好键
     */
    @TableField("preference_key")
    private String preferenceKey;

    /**
     * 偏好值
     */
    @TableField("preference_value")
    private String preferenceValue;

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