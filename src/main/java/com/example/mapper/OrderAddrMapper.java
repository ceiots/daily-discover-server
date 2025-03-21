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
     */
    @Insert("INSERT INTO order_addr (user_id, address, contact_name, contact_phone, is_default, ...) " +
            "VALUES (#{orderAddr.userId}, #{orderAddr.address}, #{orderAddr.contactName}, #{orderAddr.contactPhone}, #{orderAddr.isDefault}, ...)")
    void save(@Param("orderAddr") OrderAddr orderAddr);

    /**
     * 更新收货信息
     * @param orderAddr 收货信息
     */
    @Update("UPDATE order_addr " +
            "SET user_id = #{orderAddr.userId}, address = #{orderAddr.address}, contact_name = #{orderAddr.contactName}, " +
            "contact_phone = #{orderAddr.contactPhone}, is_default = #{orderAddr.isDefault}, ... " +
            "WHERE order_addr_id = #{orderAddr.orderAddrId}")
    void update(@Param("orderAddr") OrderAddr orderAddr);

    /**
     * 根据收货信息ID删除收货信息
     * @param orderAddrId 收货信息ID
     */
    @Delete("DELETE FROM order_addr WHERE orderAddrId = #{orderAddrId}")
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
     * 插入收货信息
     * @param orderAddr 收货信息
     */
    @Insert("INSERT INTO order_addr (user_id, address, contact_name, contact_phone, is_default) " +
            "VALUES (#{userId}, #{address}, #{contactName}, #{contactPhone}, #{isDefault})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(@Param("orderAddr") OrderAddr orderAddr);
}