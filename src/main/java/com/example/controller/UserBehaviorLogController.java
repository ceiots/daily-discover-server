package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entity.UserBehaviorLog;
import com.example.service.UserBehaviorLogService;

@RestController
@RequestMapping("/api/user-behavior-logs")
public class UserBehaviorLogController {
    @Autowired
    private UserBehaviorLogService userBehaviorLogService;

    @GetMapping
    public ResponseEntity<List<UserBehaviorLog>> getAllUserBehaviorLogs() {
        return ResponseEntity.ok(userBehaviorLogService.getAllUserBehaviorLogs());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserBehaviorLog>> getUserBehaviorLogsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userBehaviorLogService.getUserBehaviorLogsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addUserBehaviorLog(@RequestBody UserBehaviorLog log) {
        userBehaviorLogService.addUserBehaviorLog(log);
        return ResponseEntity.ok().build();
    }
}