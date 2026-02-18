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
    @Select("SELECT * FROM refund_records WHERE order_id = #{orderId} ORDER BY refund_date DESC")
    List<RefundRecord> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据退款状态查询
     */
    @Select("SELECT * FROM refund_records WHERE refund_status = #{status} ORDER BY refund_date DESC")
    List<RefundRecord> findByStatus(@Param("status") String status);
    
    /**
     * 查询用户退款记录
     */
    @Select("SELECT * FROM refund_records WHERE user_id = #{userId} ORDER BY refund_date DESC")
    List<RefundRecord> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询待处理的退款申请
     */
    @Select("SELECT * FROM refund_records WHERE refund_status = 'pending' ORDER BY refund_date ASC")
    List<RefundRecord> findPendingRefunds();
    
    /**
     * 统计退款总金额
     */
    @Select("SELECT SUM(refund_amount) FROM refund_records WHERE refund_status = 'completed'")
    Double sumRefundAmount();
}