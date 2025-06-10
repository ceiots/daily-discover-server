package com.example.user.domain.repository;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户查询条件
 */
@Data
@Accessors(chain = true)
public class UserQueryCondition {
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 状态列表
     */
    private List<Integer> statusList;
    
    /**
     * 用户类型列表
     */
    private List<Integer> userTypeList;
    
    /**
     * 注册开始时间
     */
    private LocalDateTime registerStartTime;
    
    /**
     * 注册结束时间
     */
    private LocalDateTime registerEndTime;
    
    /**
     * 最后登录开始时间
     */
    private LocalDateTime lastLoginStartTime;
    
    /**
     * 最后登录结束时间
     */
    private LocalDateTime lastLoginEndTime;
    
    /**
     * 角色ID列表
     */
    private List<Long> roleIdList;
    
    /**
     * 角色编码列表
     */
    private List<String> roleCodeList;
} 