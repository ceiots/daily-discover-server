package com.example.mapper;

import com.example.model.OrderAddr;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface OrderAddrMapper {
    /**
     * 获取用户的收货信息列表
     * @param userId 用户ID
     * @return 收货信息列表
     */
    @Select("SELECT * FROM order_addr WHERE user_id = #{userId}")
    List<OrderAddr> listByUserId(@Param("userId") Long userId);

    /**
     * 根据收货信息ID获取收货信息
     * @param orderAddrId 收货信息ID
     * @return 收货信息
     */
    @Select("SELECT * FROM order_addr WHERE order_addr_id = #{orderAddrId}")
    OrderAddr getByOrderAddrId(@Param("orderAddrId") Long orderAddrId);

    /**
     * 保存收货信息
     * @param orderAddr 收货信息
     * @return 插入操作影响的行数，通常插入成功返回 1
     */
    @Insert("INSERT INTO order_addr (user_id, address, name, phone, is_default, province, city, district) " +
            "VALUES (#{orderAddr.userId}, #{orderAddr.address}, #{orderAddr.name}, #{orderAddr.phone}, #{orderAddr.isDefault}, #{orderAddr.province}, #{orderAddr.city}, #{orderAddr.district})")
    @Options(useGeneratedKeys = true, keyProperty = "orderAddr.orderAddrId", keyColumn = "order_addr_id")
    int save(@Param("orderAddr") OrderAddr orderAddr);

    /**
     * 更新收货信息
     * @param orderAddr 收货信息
     */
    @Update("UPDATE order_addr " +
            "SET user_id = #{orderAddr.userId}, address = #{orderAddr.address}, name = #{orderAddr.name}, " +
            "phone = #{orderAddr.phone}, is_default = #{orderAddr.isDefault}, province = #{orderAddr.province}, city = #{orderAddr.city}, district = #{orderAddr.district} " +
            "WHERE order_addr_id = #{orderAddr.orderAddrId}")
    void update(@Param("orderAddr") OrderAddr orderAddr);

    /**
     * 根据收货信息ID删除收货信息
     * @param orderAddrId 收货信息ID
     */
    @Delete("DELETE FROM order_addr WHERE order_addr_id = #{orderAddrId}")
    void deleteById(@Param("orderAddrId") Long orderAddrId);

    /**
     * 设置默认收货地址
     * @param userId 用户ID
     * @param orderAddrId 收货信息ID
     */
    @Update("UPDATE order_addr SET is_default = 0 WHERE user_id = #{userId}; " +
            "UPDATE order_addr SET is_default = 1 WHERE order_addr_id = #{orderAddrId} AND user_id = #{userId}")
    void setDefaultAddr(@Param("userId") Long userId, @Param("orderAddrId") Long orderAddrId);

    /**
     * 取消用户的所有默认收货地址
     * @param userId 用户ID
     */
    @Update("UPDATE order_addr SET is_default = 0 WHERE user_id = #{userId}")
    void cancelDefaultAddr(@Param("userId") Long userId);

    /**
     * 根据用户 ID 获取默认收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息
     */
    @Select("SELECT * FROM order_addr WHERE user_id = #{userId} AND is_default = 1")
    OrderAddr getDefaultByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 获取所有收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息列表
     */
    @Select("SELECT * FROM order_addr WHERE user_id = #{userId}")
    List<OrderAddr> getAllByUserId(@Param("userId") Long userId);

    // 根据订单ID查询订单地址
    @Select("SELECT * FROM order_addr WHERE order_id = #{orderId}")
    OrderAddr getAddressByOrderId(@Param("orderId") Long orderId);
}