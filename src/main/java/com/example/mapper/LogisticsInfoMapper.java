package com.example.mapper;

import com.example.model.LogisticsInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 物流信息Mapper接口
 */
@Mapper
public interface LogisticsInfoMapper {
    
    /**
     * 根据ID查询物流信息
     */
    @Select("SELECT * FROM logistics_info WHERE id = #{id}")
    LogisticsInfo findById(Long id);
    
    /**
     * 根据订单ID查询物流信息
     */
    @Select("SELECT * FROM logistics_info WHERE order_id = #{orderId}")
    LogisticsInfo findByOrderId(Long orderId);
    
    /**
     * 根据物流单号查询物流信息
     */
    @Select("SELECT * FROM logistics_info WHERE tracking_number = #{trackingNumber}")
    LogisticsInfo findByTrackingNumber(String trackingNumber);
    
    /**
     * 查询所有物流信息
     */
    @Select("SELECT * FROM logistics_info")
    List<LogisticsInfo> findAll();
    
    /**
     * 插入物流信息
     */
    @Insert("INSERT INTO logistics_info (order_id, tracking_number, company_code, company_name, status, " +
            "shipping_time, estimated_delivery_time, actual_delivery_time, receiver_name, receiver_phone, receiver_address) " +
            "VALUES (#{orderId}, #{trackingNumber}, #{companyCode}, #{companyName}, #{status}, " +
            "#{shippingTime}, #{estimatedDeliveryTime}, #{actualDeliveryTime}, #{receiverName}, #{receiverPhone}, #{receiverAddress})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LogisticsInfo logisticsInfo);
    
    /**
     * 更新物流信息
     */
    @Update("UPDATE logistics_info SET tracking_number = #{trackingNumber}, company_code = #{companyCode}, " +
            "company_name = #{companyName}, status = #{status}, shipping_time = #{shippingTime}, " +
            "estimated_delivery_time = #{estimatedDeliveryTime}, actual_delivery_time = #{actualDeliveryTime}, " +
            "receiver_name = #{receiverName}, receiver_phone = #{receiverPhone}, receiver_address = #{receiverAddress} " +
            "WHERE id = #{id}")
    int update(LogisticsInfo logisticsInfo);
    
    /**
     * 更新物流状态
     */
    @Update("UPDATE logistics_info SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除物流信息
     */
    @Delete("DELETE FROM logistics_info WHERE id = #{id}")
    int delete(Long id);
} 