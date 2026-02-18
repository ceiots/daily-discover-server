package com.dailydiscover.service;

import com.dailydiscover.model.AfterSalesApplication;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 售后服务申请服务接口
 */
public interface AfterSalesApplicationService extends IService<AfterSalesApplication> {
    
    /**
     * 根据订单ID查询售后服务申请
     */
    java.util.List<AfterSalesApplication> getByOrderId(Long orderId);
    
    /**
     * 根据用户ID查询售后服务申请
     */
    java.util.List<AfterSalesApplication> getByUserId(Long userId);
    
    /**
     * 根据申请状态查询售后服务申请
     */
    java.util.List<AfterSalesApplication> getByStatus(String status);
    
    /**
     * 创建售后服务申请
     */
    AfterSalesApplication createApplication(Long orderId, Long userId, String applicationType, 
                                          String reason, String description);
    
    /**
     * 更新申请状态
     */
    boolean updateApplicationStatus(Long applicationId, String status);
    
    /**
     * 处理售后服务申请
     */
    boolean processApplication(Long applicationId, String processor, String processResult);
    
    /**
     * 获取待处理的售后服务申请
     */
    java.util.List<AfterSalesApplication> getPendingApplications();
    
    /**
     * 获取售后服务申请统计
     */
    java.util.Map<String, Object> getApplicationStats();
}