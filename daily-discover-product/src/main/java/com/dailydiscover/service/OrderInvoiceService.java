package com.dailydiscover.service;

import com.dailydiscover.model.OrderInvoice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 订单发票服务接口
 */
public interface OrderInvoiceService extends IService<OrderInvoice> {
    
    /**
     * 根据订单ID查询发票信息
     */
    OrderInvoice getByOrderId(Long orderId);
    
    /**
     * 根据发票号查询发票信息
     */
    OrderInvoice getByInvoiceNumber(String invoiceNumber);
    
    /**
     * 创建发票信息
     */
    OrderInvoice createInvoice(Long orderId, String invoiceType, String invoiceTitle, 
                              String taxNumber, String invoiceContent);
    
    /**
     * 更新发票状态
     */
    boolean updateInvoiceStatus(Long orderId, String status);
    
    /**
     * 开具发票
     */
    boolean issueInvoice(Long orderId, String invoiceNumber);
    
    /**
     * 作废发票
     */
    boolean voidInvoice(Long orderId);
    
    /**
     * 获取待开票订单列表
     */
    java.util.List<OrderInvoice> getPendingInvoices();
    
    /**
     * 获取已开票订单列表
     */
    java.util.List<OrderInvoice> getIssuedInvoices();
}