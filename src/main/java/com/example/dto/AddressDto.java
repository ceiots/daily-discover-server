package com.example.dto;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private boolean isDefault;
    private String city;
    private String area;
    private String addr;
    private String consignee;
}