package com.example.model;

import lombok.Data;

@Data
public class OrderAddr {
    private Long orderAddrId;
    private Long userId;
    private Boolean isDefault;
    private String name;
    private String phone;
    private String address;
}