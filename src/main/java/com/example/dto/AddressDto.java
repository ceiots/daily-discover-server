package com.example.dto;

import lombok.Data;

@Data
public class AddressDto {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private String province;
    private String city;
    private String district;
    private boolean isDefault;
}