package com.example.mapper;

import com.example.model.Address;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

@Mapper
public interface AddressMapper {
    /**
     * 获取用户的收货信息列表
     * @param userId 用户ID
     * @return 收货信息列表
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId}")
    List<Address> listByUserId(@Param("userId") Long userId);

    /**
     * 根据收货信息ID获取收货信息
     * @param id 收货信息ID
     * @return 收货信息
     */
    @Select("SELECT * FROM address WHERE id = #{id}")
    Address getById(@Param("id") Long id);

    /**
     * 保存收货信息
     * @param userAddress 收货信息
     * @return 插入操作影响的行数，通常插入成功返回 1
     */
    @Insert("INSERT INTO address (user_id, address, name, phone, is_default, province, city, district) " +
            "VALUES (#{userAddress.userId}, #{userAddress.address}, #{userAddress.name}, #{userAddress.phone}, #{userAddress.isDefault}, #{userAddress.province}, #{userAddress.city}, #{userAddress.district})")
    @Options(useGeneratedKeys = true, keyProperty = "userAddress.id", keyColumn = "id")
    int save(@Param("userAddress") Address userAddress);

    /**
     * 更新收货信息
     * @param userAddress 收货信息
     */
    @Update("UPDATE address SET user_id = #{userAddress.userId}, address = #{userAddress.address}, name = #{userAddress.name}, phone = #{userAddress.phone}, is_default = #{userAddress.isDefault}, province = #{userAddress.province}, city = #{userAddress.city}, district = #{userAddress.district} WHERE id = #{userAddress.id}")
    void update(@Param("userAddress") Address userAddress);

    /**
     * 根据收货信息ID删除收货信息
     * @param id 收货信息ID
     */
    @Delete("DELETE FROM address WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    /**
     * 取消用户的所有默认收货地址
     * @param userId 用户ID
     */
    @Update("UPDATE address SET is_default = 0 WHERE user_id = #{userId}")
    void cancelDefaultAddr(@Param("userId") Long userId);

    /**
     * 根据用户 ID 获取默认收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId} AND is_default = 1")
    Address getDefaultByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 获取所有收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息列表
     */
    @Select("SELECT * FROM address WHERE user_id = #{userId}")
    List<Address> getAllByUserId(@Param("userId") Long userId);

    // 根据订单ID查询订单地址
    @Select("SELECT * FROM address WHERE id = #{id}")
    Address getAddressByOrderId(@Param("id") Long id);

    @Update("UPDATE address SET is_default = 0 WHERE user_id = #{userId}")
    void resetDefaultAddr(@Param("userId") Long userId);

    @Update("UPDATE address SET is_default = 1 WHERE id = #{id} AND user_id = #{userId}")
    void setDefaultAddr(@Param("id") Long id, @Param("userId") Long userId);
}