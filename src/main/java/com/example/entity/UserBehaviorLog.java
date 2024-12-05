package com.example.entity;

import java.util.Date;

import lombok.Data;

@Data
public class UserBehaviorLog {
    private Long id;
    private Long userId;
    private String actionType;
    private Date timestamp;
    private String details;
}