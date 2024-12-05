package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.UserBehaviorLog;
import com.example.mapper.UserBehaviorLogMapper;

@Service
public class UserBehaviorLogService {
    @Autowired
    private UserBehaviorLogMapper userBehaviorLogMapper;

    public List<UserBehaviorLog> getAllUserBehaviorLogs() {
        return userBehaviorLogMapper.findAll();
    }

    public List<UserBehaviorLog> getUserBehaviorLogsByUserId(Long userId) {
        return userBehaviorLogMapper.findByUserId(userId);
    }

    public void addUserBehaviorLog(UserBehaviorLog log) {
        userBehaviorLogMapper.insert(log);
    }
}