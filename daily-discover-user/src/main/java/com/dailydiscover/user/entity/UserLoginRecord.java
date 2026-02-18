package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户登录记录实体类（合并登录尝试和统计）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_login_records")
public class UserLoginRecord {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（可为空，表示匿名登录尝试）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 登录标识（手机号）
     */
    @TableField("identifier")
    private String identifier;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 设备信息
     */
    @TableField("device_info")
    private String deviceInfo;

    /**
     * 登录结果（success/failed）
     */
    @TableField("result")
    private String result;

    /**
     * 失败原因（如果失败）
     */
    @TableField("failure_reason")
    private String failureReason;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 登录结果枚举
     */
    public enum Result {
        SUCCESS("success"),
        FAILED("failed");

        private final String value;

        Result(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}