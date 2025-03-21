package com.example.model;

import lombok.Data;

@Data
public class OrderAddr {
    private Long orderAddrId;
    private Long userId;
    private String province;
    private String city;
    private String area;
    private String addr;
    private String consignee;
    private String mobile;
    private Boolean isDefault;
}