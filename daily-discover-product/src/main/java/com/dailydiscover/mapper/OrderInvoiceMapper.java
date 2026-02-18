package com.dailydiscover.mapper;

import com.dailydiscover.model.OrderInvoice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单发票表 Mapper
 */
@Mapper
public interface OrderInvoiceMapper extends BaseMapper<OrderInvoice> {
    
    /**
     * 根据订单ID查询发票信息
     */
    @Select("SELECT * FROM order_invoices WHERE order_id = #{orderId}")
    OrderInvoice findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据发票状态查询发票列表
     */
    @Select("SELECT * FROM order_invoices WHERE invoice_status = #{status} ORDER BY created_at DESC")
    List<OrderInvoice> findByStatus(@Param("status") String status);
    
    /**
     * 根据发票号码查询发票信息
     */
    @Select("SELECT * FROM order_invoices WHERE invoice_no = #{invoiceNo}")
    OrderInvoice findByInvoiceNo(@Param("invoiceNo") String invoiceNo);
    
    /**
     * 查询待开票的发票列表
     */
    @Select("SELECT * FROM order_invoices WHERE invoice_status = 'pending' ORDER BY created_at ASC")
    List<OrderInvoice> findPendingInvoices();
    
    /**
     * 查询已开票的发票列表
     */
    @Select("SELECT * FROM order_invoices WHERE invoice_status = 'issued' ORDER BY issued_at DESC")
    List<OrderInvoice> findIssuedInvoices();
}