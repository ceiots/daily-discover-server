package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "用户信息")
public class UserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("性别：0-未知，1-男，2-女")
    private Integer gender;

    @ApiModelProperty("状态：0-禁用，1-正常，2-锁定")
    private Integer status;

    @ApiModelProperty("用户类型：1-普通用户，2-商家，3-官方账号")
    private Integer userType;

    @ApiModelProperty("注册时间")
    private LocalDateTime registerTime;

    @ApiModelProperty("最后登录时间")
    private LocalDateTime lastLoginTime;

    @ApiModelProperty("最后登录IP")
    private String lastLoginIp;

    @ApiModelProperty("是否会员")
    private Boolean isMember;

    @ApiModelProperty("会员等级")
    private Integer memberLevel;

    @ApiModelProperty("会员到期时间")
    private LocalDateTime memberExpireTime;

    @ApiModelProperty("是否永久会员")
    private Boolean isForeverMember;

    @ApiModelProperty("积分")
    private Integer points;

    @ApiModelProperty("成长值")
    private Integer growthValue;

    @ApiModelProperty("账户余额")
    private String balance;

    @ApiModelProperty("用户角色列表")
    private List<String> roles;

    @ApiModelProperty("用户权限列表")
    private List<String> permissions;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 