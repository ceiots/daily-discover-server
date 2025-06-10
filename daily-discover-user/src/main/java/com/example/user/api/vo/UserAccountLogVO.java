package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户账户日志视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "用户账户日志信息")
public class UserAccountLogVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("类型:1-充值,2-消费,3-退款,4-提现,5-调整")
    private Integer type;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("变动前余额")
    private BigDecimal beforeBalance;

    @ApiModelProperty("变动后余额")
    private BigDecimal afterBalance;

    @ApiModelProperty("来源:1-订单,2-活动,3-系统")
    private Integer source;

    @ApiModelProperty("来源ID")
    private String sourceId;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
} 