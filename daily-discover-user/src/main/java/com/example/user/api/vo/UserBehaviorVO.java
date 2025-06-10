package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户行为VO
 */
@Data
@ApiModel("用户行为")
public class UserBehaviorVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @ApiModelProperty(value = "行为类型", required = true)
    @NotNull(message = "行为类型不能为空")
    private Integer behaviorType;

    @ApiModelProperty(value = "目标ID", required = true)
    @NotNull(message = "目标ID不能为空")
    private Long targetId;

    @ApiModelProperty(value = "目标类型", required = true)
    @NotNull(message = "目标类型不能为空")
    private Integer targetType;

    @ApiModelProperty("行为数据")
    private String behaviorData;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}