package com.example.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.model.StockLock;

@Mapper
public interface StockLockDao {

    /**
     * 插入库存锁定记录
     */
    @Insert("INSERT INTO stock_lock(product_id, order_id, quantity, lock_status, create_time) " +
            "VALUES(#{productId}, #{orderId}, #{quantity}, #{lockStatus}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(StockLock stockLock);

    /**
     * 根据订单ID查询库存锁定记录
     */
    @Select("SELECT * FROM stock_lock WHERE order_id = #{orderId}")
    List<StockLock> findByOrderId(@Param("orderId") Long orderId);

    /**
     * 更新锁定状态
     */
    @Update("UPDATE stock_lock SET lock_status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 根据订单ID和商品ID查询库存锁定记录
     */
    @Select("SELECT * FROM stock_lock WHERE order_id = #{orderId} AND product_id = #{productId}")
    StockLock findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
} 