package com.example.model;

import java.util.Date;
import java.util.List;

public class User {
    private Long id;
    private String phoneNumber;
    private String password;
    private String memberLevel;
    private String avatar;
    private Date registrationTime;
    private String nickname;
    private String paymentPassword; // 支付密码
    private Boolean isOfficial; // 是否为官方账号（同时具备管理员权限）
    
    // 构造函数、getter和setter方法
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getMemberLevel() {
        return memberLevel;
    }
    
    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public Date getRegistrationTime() {
        return registrationTime;
    }
    
    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getPaymentPassword() {
        return paymentPassword;
    }
    
    public void setPaymentPassword(String paymentPassword) {
        this.paymentPassword = paymentPassword;
    }
    
    public Boolean getIsOfficial() {
        return isOfficial;
    }
    
    public void setIsOfficial(Boolean isOfficial) {
        this.isOfficial = isOfficial;
    }
}