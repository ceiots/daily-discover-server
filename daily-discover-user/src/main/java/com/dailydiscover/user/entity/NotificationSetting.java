package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户通知设置实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("notification_settings")
public class NotificationSetting {

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
     * 推送通知
     */
    @TableField("push_notifications")
    private Boolean pushNotifications;

    /**
     * 邮件通知
     */
    @TableField("email_notifications")
    private Boolean emailNotifications;

    /**
     * 短信通知
     */
    @TableField("sms_notifications")
    private Boolean smsNotifications;

    /**
     * 营销邮件
     */
    @TableField("marketing_emails")
    private Boolean marketingEmails;

    /**
     * 订单更新
     */
    @TableField("order_updates")
    private Boolean orderUpdates;

    /**
     * 价格提醒
     */
    @TableField("price_alerts")
    private Boolean priceAlerts;

    /**
     * 新品提醒
     */
    @TableField("new_product_alerts")
    private Boolean newProductAlerts;

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