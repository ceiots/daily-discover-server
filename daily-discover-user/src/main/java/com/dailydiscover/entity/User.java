package com.dailydiscover.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * @author Daily Discover Team
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dd_user")
public class User {

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,20}$", message = "用户名只能包含字母、数字、下划线，长度4-20位")
    @TableField("username")
    private String username;

    /**
     * 密码（加密存储）
     */
    @NotBlank(message = "密码不能为空")
    @TableField("password")
    private String password;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @TableField("phone")
    private String phone;

    /**
     * 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    @TableField("gender")
    private Integer gender;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDateTime birthday;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 用户积分
     */
    @TableField("points")
    private Integer points;

    /**
     * 用户等级：新用户、青铜会员、白银会员、黄金会员、钻石会员、VIP
     */
    @TableField("level")
    private String level;

    /**
     * 会员类型：普通会员、青铜会员、白银会员、黄金会员、钻石会员、至尊会员
     */
    @TableField("membership")
    private String membership;

    /**
     * 收藏数量
     */
    @TableField("favorites_count")
    private Integer favoritesCount;

    /**
     * 待付款订单数
     */
    @TableField("orders_pending_payment")
    private Integer ordersPendingPayment;

    /**
     * 待发货订单数
     */
    @TableField("orders_pending_shipment")
    private Integer ordersPendingShipment;

    /**
     * 待收货订单数
     */
    @TableField("orders_pending_receipt")
    private Integer ordersPendingReceipt;

    /**
     * 已完成订单数
     */
    @TableField("orders_completed")
    private Integer ordersCompleted;

    /**
     * 用户状态：0-正常，1-禁用
     */
    @TableField("status")
    private Integer status;

    /**
     * 最后登录时间
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    @TableField("last_login_ip")
    private String lastLoginIp;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标志
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}