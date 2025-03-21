package com.example.mapper;

import com.example.model.Address;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AddressMapper {
    /**
     * 根据ID查询地址
     * @param id 地址ID
     * @return 地址实体
     */
    Address getAddressById(Long id);

    /**
     * 查询所有地址
     * @return 地址列表
     */
    List<Address> getAllAddresses();

    /**
     * 插入地址
     * @param address 地址实体
     * @return 插入的记录数
     */
    @Insert("INSERT INTO address (name, phone, address, is_default, user_id) VALUES (#{name}, #{phone}, #{address}, #{isDefault}, #{userId})")
    void insertAddress(Address address);

    /**
     * 根据ID更新地址
     * @param address 地址实体
     * @return 更新的记录数
     */
    int updateAddress(Address address);

    /**
     * 根据ID删除地址
     * @param id 地址ID
     * @return 删除的记录数
     */
    int deleteAddress(Long id);
}