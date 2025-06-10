package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "会员信息")
public class MemberVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("会员ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("会员等级")
    private Integer memberLevel;

    @ApiModelProperty("会员等级名称")
    private String levelName;

    @ApiModelProperty("成长值")
    private Integer growthValue;

    @ApiModelProperty("积分")
    private Integer points;

    @ApiModelProperty("已使用积分")
    private Integer usedPoints;

    @ApiModelProperty("是否永久会员")
    private Boolean isForever;

    @ApiModelProperty("会员开始时间")
    private LocalDateTime startTime;

    @ApiModelProperty("会员结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty("状态：0-禁用，1-正常")
    private Integer status;

    @ApiModelProperty("免邮次数")
    private Integer freeShippingCount;

    @ApiModelProperty("免退次数")
    private Integer freeReturnCount;

    @ApiModelProperty("是否有效会员")
    private Boolean isValid;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 