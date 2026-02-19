package com.dailydiscover.mapper;

import com.dailydiscover.model.RefundRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 退款记录表 Mapper
 */
@Mapper
public interface RefundRecordMapper extends BaseMapper<RefundRecord> {
    
    /**
     * 根据订单ID查询退款记录
     */
    @Select("SELECT * FROM refund_records WHERE order_id = #{orderId} ORDER BY created_at DESC")
    List<RefundRecord> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据退款状态查询
     */
    @Select("SELECT * FROM refund_records WHERE status = #{status} ORDER BY created_at DESC")
    List<RefundRecord> findByStatus(@Param("status") String status);
    
    /**
     * 根据退款单号查询
     */
    @Select("SELECT * FROM refund_records WHERE refund_no = #{refundNo}")
    RefundRecord findByRefundNo(@Param("refundNo") String refundNo);
    
    /**
     * 根据支付交易ID查询
     */
    @Select("SELECT * FROM refund_records WHERE payment_transaction_id = #{transactionId} ORDER BY created_at DESC")
    List<RefundRecord> findByPaymentTransactionId(@Param("transactionId") Long transactionId);
    
    /**
     * 查询待处理的退款申请
     */
    @Select("SELECT * FROM refund_records WHERE status = 'pending' ORDER BY created_at ASC")
    List<RefundRecord> findPendingRefunds();
    
    /**
     * 统计退款总金额
     */
    @Select("SELECT SUM(refund_amount) FROM refund_records WHERE status = 'completed'")
    Double sumRefundAmount();
}