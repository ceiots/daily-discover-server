package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 注册请求视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "注册请求")
public class RegisterVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$", message = "用户名必须为4-16位字母、数字或下划线")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$", message = "密码必须包含大小写字母和数字，长度为8-20位")
    private String password;

    @ApiModelProperty(value = "确认密码", required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    @ApiModelProperty("邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("验证码类型:1-手机,2-邮箱")
    private Integer codeType;

    @ApiModelProperty("注册IP")
    private String registerIp;

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