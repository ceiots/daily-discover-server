package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户详情DTO
 */
@Data
@Accessors(chain = true)
public class UserProfileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 详情ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 证件类型:1-身份证,2-护照,3-军官证
     */
    private Integer idCardType;

    /**
     * 证件号码
     */
    private String idCardNo;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 学历:1-小学,2-初中,3-高中,4-大专,5-本科,6-硕士,7-博士
     */
    private Integer education;

    /**
     * 职业
     */
    private String profession;

    /**
     * 收入水平:1-低,2-中,3-高
     */
    private Integer incomeLevel;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 所在省
     */
    private String province;

    /**
     * 所在市
     */
    private String city;

    /**
     * 所在区
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 