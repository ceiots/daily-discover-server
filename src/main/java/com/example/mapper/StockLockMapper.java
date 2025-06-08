package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.StockLock;

/**
 * 库存锁定数据访问接口
 */
@Mapper
public interface StockLockMapper extends BaseMapper<StockLock> {
    
    /**
     * 插入库存锁定记录
     */
    @Insert("INSERT INTO stock_lock(product_id, quantity, order_id, lock_status, create_time) " +
            "VALUES(#{productId}, #{quantity}, #{orderId}, #{lockStatus}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StockLock stockLock);
    
    /**
     * 更新锁定状态
     */
    @Update("UPDATE stock_lock SET lock_status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 根据订单ID查询锁定记录
     */
    @Select("SELECT * FROM stock_lock WHERE order_id = #{orderId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "lockStatus", column = "lock_status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<StockLock> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据商品ID查询锁定记录
     */
    @Select("SELECT * FROM stock_lock WHERE product_id = #{productId} AND lock_status = 1")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "lockStatus", column = "lock_status"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<StockLock> findByProductId(@Param("productId") Long productId);
} 