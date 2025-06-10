package com.example.user.api.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户详情视图对象
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "用户详情信息")
public class UserProfileVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("证件类型:1-身份证,2-护照,3-军官证")
    private Integer idCardType;

    @ApiModelProperty("证件号码")
    private String idCardNo;

    @ApiModelProperty("生日")
    private LocalDate birthday;

    @ApiModelProperty("年龄")
    private Integer age;

    @ApiModelProperty("学历:1-小学,2-初中,3-高中,4-大专,5-本科,6-硕士,7-博士")
    private Integer education;

    @ApiModelProperty("职业")
    private String profession;

    @ApiModelProperty("收入水平:1-低,2-中,3-高")
    private Integer incomeLevel;

    @ApiModelProperty("个人简介")
    private String bio;

    @ApiModelProperty("所在省")
    private String province;

    @ApiModelProperty("所在市")
    private String city;

    @ApiModelProperty("所在区")
    private String district;

    @ApiModelProperty("详细地址")
    private String address;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
} 