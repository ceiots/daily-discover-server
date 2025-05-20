package com.example.service;

import com.example.model.RefundRequest;
import java.util.List;

public interface RefundService {
    
    /**
     * 创建退款申请
     * @param refundRequest 退款申请信息
     * @return 创建的退款申请
     */
    RefundRequest createRefundRequest(RefundRequest refundRequest);
    
    /**
     * 根据ID查询退款申请
     * @param id 退款申请ID
     * @return 退款申请信息
     */
    RefundRequest getRefundRequestById(Long id);
    
    /**
     * 查询用户的所有退款申请
     * @param userId 用户ID
     * @return 退款申请列表
     */
    List<RefundRequest> getRefundRequestsByUserId(Long userId);
    
    /**
     * 查询订单的所有退款申请
     * @param orderId 订单ID
     * @return 退款申请列表
     */
    List<RefundRequest> getRefundRequestsByOrderId(Long orderId);
    
    /**
     * 商家处理退款申请
     * @param id 退款申请ID
     * @param status 处理结果状态
     * @param refusalReason 拒绝原因（当拒绝时需要提供）
     * @return 更新后的退款申请
     */
    RefundRequest processRefundRequest(Long id, Integer status, String refusalReason);
    
    /**
     * 取消退款申请
     * @param id 退款申请ID
     * @return 更新后的退款申请
     */
    RefundRequest cancelRefundRequest(Long id);
} 