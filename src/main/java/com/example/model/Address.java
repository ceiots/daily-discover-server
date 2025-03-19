package com.example.model;

import lombok.Data;

/**
 * 地址实体类，对应数据库中的 addresses 表
 */
@Data
public class Address {
    /**
     * 地址 ID，主键，自增
     */
    private Long id;

    /**
     * 关联的用户
     */
    private Long userId;

    /**
     * 收件人姓名
     */
    private String name;
    /**
     * 收件人电话
     */
    private String phone;
    /**
     * 收件地址
     */
    private String address;
    /**
     * 是否为默认地址
     */
    private boolean isDefault;
}