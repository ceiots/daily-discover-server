package com.example.mapper;

import com.example.model.RefundRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface RefundRequestMapper {

    @Insert("INSERT INTO refund_requests (order_number, order_id, user_id, shop_id, refund_type, "
            + "reason, reason_detail, amount, images, status, created_at, updated_at) "
            + "VALUES (#{orderNumber}, #{orderId}, #{userId}, #{shopId}, #{refundType}, "
            + "#{reason}, #{reasonDetail}, #{amount}, #{images}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(RefundRequest refundRequest);
    
    @Select("SELECT * FROM refund_requests WHERE id = #{id}")
    RefundRequest findById(Long id);
    
    @Select("SELECT * FROM refund_requests WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<RefundRequest> findByUserId(Long userId);
    
    @Select("SELECT * FROM refund_requests WHERE order_id = #{orderId}")
    List<RefundRequest> findByOrderId(Long orderId);
    
    @Select("SELECT * FROM refund_requests WHERE shop_id = #{shopId} AND status = #{status} ORDER BY created_at DESC")
    List<RefundRequest> findByShopIdAndStatus(@Param("shopId") Long shopId, @Param("status") Integer status);
    
    @Update("UPDATE refund_requests SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("updatedAt") java.util.Date updatedAt);
    
    @Update("UPDATE refund_requests SET status = #{status}, refusal_reason = #{refusalReason}, updated_at = #{updatedAt} WHERE id = #{id}")
    void rejectRefund(@Param("id") Long id, @Param("status") Integer status, @Param("refusalReason") String refusalReason, @Param("updatedAt") java.util.Date updatedAt);
} 