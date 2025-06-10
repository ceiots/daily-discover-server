package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户账户视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "用户账户信息")
public class UserAccountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("账户余额")
    private BigDecimal balance;

    @ApiModelProperty("冻结金额")
    private BigDecimal freezeAmount;

    @ApiModelProperty("积分")
    private Integer points;

    @ApiModelProperty("成长值")
    private Integer growthValue;

    @ApiModelProperty("状态:0-冻结,1-正常")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 