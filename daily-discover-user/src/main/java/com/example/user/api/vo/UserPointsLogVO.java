package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户积分记录VO
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "用户积分记录信息")
public class UserPointsLogVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("类型:1-获取,2-消费,3-过期,4-调整")
    private Integer type;

    @ApiModelProperty("积分数量")
    private Integer points;

    @ApiModelProperty("变动前积分")
    private Integer beforePoints;

    @ApiModelProperty("变动后积分")
    private Integer afterPoints;

    @ApiModelProperty("来源:1-订单,2-签到,3-活动,4-邀请,5-系统")
    private Integer source;

    @ApiModelProperty("来源ID")
    private String sourceId;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("过期时间")
    private LocalDateTime expireTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}