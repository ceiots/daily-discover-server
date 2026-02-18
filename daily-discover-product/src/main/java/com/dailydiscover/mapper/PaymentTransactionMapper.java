package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.PaymentTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentTransactionMapper extends BaseMapper<PaymentTransaction> {
    
    /**
     * 根据订单ID查询支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE order_id = #{orderId} ORDER BY created_at DESC")
    List<PaymentTransaction> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据支付状态查询记录
     */
    @Select("SELECT * FROM payment_transactions WHERE status = #{status} ORDER BY created_at DESC")
    List<PaymentTransaction> findByStatus(@Param("status") String status);
    
    /**
     * 根据交易号查询支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE transaction_no = #{transactionNo}")
    PaymentTransaction findByTransactionNo(@Param("transactionNo") String transactionNo);
    
    /**
     * 根据第三方交易号查询支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE third_party_transaction_no = #{thirdPartyTransactionNo}")
    PaymentTransaction findByThirdPartyTransactionNo(@Param("thirdPartyTransactionNo") String thirdPartyTransactionNo);
    
    /**
     * 查询待处理的支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE status = 'pending' ORDER BY payment_request_at ASC")
    List<PaymentTransaction> findPendingTransactions();
    
    /**
     * 查询已过期的支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE status = 'pending' AND payment_expired_at < NOW()")
    List<PaymentTransaction> findExpiredTransactions();
}