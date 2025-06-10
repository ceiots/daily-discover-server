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
 * 用户账户流水实体
 */
@Data
@Accessors(chain = true)
@TableName("user_account_log")
public class UserAccountLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 类型:1-收入,2-支出,3-冻结,4-解冻,5-积分,6-成长值
     */
    private Integer type;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 变动积分
     */
    private Integer points;

    /**
     * 变动成长值
     */
    private Integer growth;

    /**
     * 变动前金额
     */
    private BigDecimal beforeAmount;

    /**
     * 变动后金额
     */
    private BigDecimal afterAmount;

    /**
     * 来源:1-订单,2-退款,3-充值,4-提现,5-系统调整,6-签到,7-活动
     */
    private Integer source;

    /**
     * 来源ID
     */
    private String sourceId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 