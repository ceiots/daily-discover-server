package com.example.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String phoneNumber; // 手机号码
    private String password; // 密码
    private String nickname; // 昵称
}