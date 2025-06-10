package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 会员等级视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "会员等级信息")
public class MemberLevelVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("等级")
    private Integer level;

    @ApiModelProperty("等级名称")
    private String name;

    @ApiModelProperty("等级图标")
    private String icon;

    @ApiModelProperty("成长值下限")
    private Integer growthMin;

    @ApiModelProperty("成长值上限")
    private Integer growthMax;

    @ApiModelProperty("折扣")
    private BigDecimal discount;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("会员权益")
    private String benefits;

    @ApiModelProperty("状态:0-禁用,1-启用")
    private Integer status;

    @ApiModelProperty("是否包邮:0-否,1-是")
    private Boolean freeShipping;

    @ApiModelProperty("备注")
    private String note;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 