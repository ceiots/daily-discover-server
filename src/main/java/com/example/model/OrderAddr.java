package com.example.model;

import lombok.Data;

/**
 * 订单收货地址实体类
 */
@Data
public class OrderAddr {
    /**
     * 收货地址ID
     */
    private Long orderAddrId;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 是否为默认地址
     */
    private Boolean isDefault;
    /**
     * 联系人姓名
     */
    private String name;
    /**
     * 联系人电话
     */
    private String phone;
    /**
     * 收货地址
     */
    private String address;
}