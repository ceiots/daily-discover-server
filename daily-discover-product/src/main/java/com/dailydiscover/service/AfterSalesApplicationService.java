package com.dailydiscover.service;

import com.dailydiscover.model.AfterSalesApplication;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 售后服务申请服务接口
 */
public interface AfterSalesApplicationService extends IService<AfterSalesApplication> {
    
    /**
     * 根据订单ID查询售后申请
     */
    List<AfterSalesApplication> findByOrderId(Long orderId);
    
    /**
     * 根据用户ID查询售后申请
     */
    List<AfterSalesApplication> findByUserId(Long userId);
    
    /**
     * 根据售后状态查询申请
     */
    List<AfterSalesApplication> findByStatus(String status);
    
    /**
     * 根据售后类型查询申请
     */
    List<AfterSalesApplication> findByType(String afterSalesType);
    
    /**
     * 查询待处理的售后申请
     */
    List<AfterSalesApplication> findPendingApplications();
    
    /**
     * 根据申请单号查询申请
     */
    AfterSalesApplication findByApplicationNo(String applicationNo);
}