package com.example.model;

import java.util.Date;

public class User {
    private Long id;
    private String phoneNumber;
    private String password;
    private String memberLevel;
    private String avatar;
    private Date registrationTime;
    private String nickname;
    
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
}