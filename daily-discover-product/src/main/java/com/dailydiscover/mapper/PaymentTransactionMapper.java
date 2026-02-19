package com.dailydiscover.mapper;

import com.dailydiscover.model.PaymentTransaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 支付交易记录表 Mapper
 */
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
    
    /**
     * 根据订单ID查询支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE order_id = #{orderId} ORDER BY created_at DESC LIMIT 1")
    PaymentTransaction getByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据交易号查询支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE transaction_no = #{transactionNo}")
    PaymentTransaction getByTransactionNo(@Param("transactionNo") String transactionNo);
    
    /**
     * 根据用户ID查询支付记录
     */
    @Select("SELECT * FROM payment_transactions WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<PaymentTransaction> getByUserId(@Param("userId") Long userId);
    
    /**
     * 创建支付交易
     */
    @Insert("INSERT INTO payment_transactions (order_id, payment_method_id, amount, currency, status, transaction_no, created_at) " +
            "VALUES (#{orderId}, #{paymentMethodId}, #{amount}, #{currency}, 'pending', UUID(), NOW())")
    int createTransaction(@Param("orderId") Long orderId, @Param("paymentMethodId") Long paymentMethodId, 
                         @Param("amount") BigDecimal amount, @Param("currency") String currency);
    
    /**
     * 更新交易状态
     */
    @Update("UPDATE payment_transactions SET status = #{status}, updated_at = NOW() WHERE id = #{transactionId}")
    int updateTransactionStatus(@Param("transactionId") Long transactionId, @Param("status") String status);
    
    /**
     * 处理支付回调
     */
    @Update("UPDATE payment_transactions SET status = 'completed', third_party_transaction_no = #{callbackData.thirdPartyTransactionNo}, " +
            "payment_completed_at = NOW(), updated_at = NOW() WHERE transaction_no = #{transactionNo}")
    int processPaymentCallback(@Param("transactionNo") String transactionNo, @Param("callbackData") Map<String, Object> callbackData);
    
    /**
     * 查询支付状态
     */
    @Select("SELECT status, third_party_transaction_no, payment_completed_at FROM payment_transactions WHERE transaction_no = #{transactionNo}")
    Map<String, Object> queryPaymentStatus(@Param("transactionNo") String transactionNo);
    
    /**
     * 获取支付统计信息
     */
    @Select("SELECT status, COUNT(*) as count, SUM(amount) as total_amount FROM payment_transactions " +
            "WHERE created_at BETWEEN #{startDate} AND #{endDate} GROUP BY status")
    List<Map<String, Object>> getPaymentStats(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 获取支付统计信息（单条记录）
     */
    @Select("SELECT COUNT(*) as total_count, SUM(amount) as total_amount, " +
            "SUM(CASE WHEN status = 'completed' THEN 1 ELSE 0 END) as completed_count, " +
            "SUM(CASE WHEN status = 'completed' THEN amount ELSE 0 END) as completed_amount " +
            "FROM payment_transactions WHERE created_at BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> getPaymentStatsSingle(@Param("startDate") String startDate, @Param("endDate") String endDate);
    
    /**
     * 验证支付请求
     */
    @Select("SELECT COUNT(*) > 0 FROM payment_methods pm " +
            "JOIN orders o ON o.id = #{orderId} " +
            "WHERE pm.id = #{paymentMethodId} AND pm.status = 'active' AND o.total_amount > 0")
    boolean validatePaymentRequest(@Param("orderId") Long orderId, @Param("paymentMethodId") Long paymentMethodId);
}