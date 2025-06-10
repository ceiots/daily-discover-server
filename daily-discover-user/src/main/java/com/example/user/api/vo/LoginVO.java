package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "登录请求")
public class LoginVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名/手机号/邮箱", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty("设备ID")
    private String deviceId;

    @ApiModelProperty("设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC")
    private Integer deviceType;

    @ApiModelProperty("设备型号")
    private String deviceModel;

    @ApiModelProperty("系统版本")
    private String osVersion;

    @ApiModelProperty("APP版本")
    private String appVersion;
} 