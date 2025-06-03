package com.example.service;

import com.example.mapper.AddressMapper;
import com.example.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper userAddressMapper;

    /**
     * 获取用户的收货信息列表
     * @param userId 用户ID
     * @return 收货信息列表
     */
    public List<Address> listByUserId(Long userId) {
        return userAddressMapper.listByUserId(userId);
    }

    /**
     * 根据收货信息ID获取收货信息
     * @param id 收货信息ID
     * @return 收货信息
     */
    public Address getById(Long id) {
        return userAddressMapper.getById(id);
    }

    /**
     * 保存收货信息
     * @param userAddress 收货信息
     */
    @Transactional
    public void save(Address userAddress) {
        if (userAddress.getIsDefault()) {
            // 如果设置为默认地址，先取消该用户的所有默认地址
            userAddressMapper.cancelDefaultAddr(userAddress.getUserId());
        }
        userAddressMapper.save(userAddress);
    }

    /**
     * 更新收货信息
     * @param userAddress 收货信息
     */
    @Transactional
    public void update(Address userAddress) {
        if (userAddress.getIsDefault()) {
            // 如果设置为默认地址，先取消该用户的所有默认地址
            userAddressMapper.cancelDefaultAddr(userAddress.getUserId());
        }
        userAddressMapper.update(userAddress);
    }

    /**
     * 根据收货信息ID删除收货信息
     * @param id 收货信息ID
     */
    public void deleteById(Long id) {
        userAddressMapper.deleteById(id);
    }

    /**
     * 设置默认收货地址
     * @param userId 用户ID
     * @param id 收货信息ID
     */
    @Transactional
    public void setDefaultAddr(Long userId, Long id) {
        userAddressMapper.resetDefaultAddr(userId);
        userAddressMapper.setDefaultAddr(id, userId);
    }

    /**
     * 根据用户 ID 获取默认收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息
     */
    public Address getDefaultByUserId(Long userId) {
        return userAddressMapper.getDefaultByUserId(userId);
    }

    /**
     * 根据用户 ID 获取所有收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息列表
     */
    public List<Address> getAllByUserId(Long userId) {
        return userAddressMapper.getAllByUserId(userId);
    }

    /**
     * 根据订单ID查询订单地址
     * @param id 订单ID
     * @return 订单地址信息
     */
    public Address getAddressByOrderId(Long id) {
        return userAddressMapper.getAddressByOrderId(id);
    }
}
