package com.example.service;

import com.example.mapper.OrderAddrMapper;
import com.example.model.OrderAddr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderAddrService {

    @Autowired
    private OrderAddrMapper orderAddrMapper;

    /**
     * 获取用户的收货信息列表
     * @param userId 用户ID
     * @return 收货信息列表
     */
    public List<OrderAddr> listByUserId(Long userId) {
        return orderAddrMapper.listByUserId(userId);
    }

    /**
     * 根据收货信息ID获取收货信息
     * @param orderAddrId 收货信息ID
     * @return 收货信息
     */
    public OrderAddr getByOrderAddrId(Long orderAddrId) {
        return orderAddrMapper.getByOrderAddrId(orderAddrId);
    }

    /**
     * 保存收货信息
     * @param orderAddr 收货信息
     */
    @Transactional
    public void save(OrderAddr orderAddr) {
        if (orderAddr.getIsDefault()) {
            // 如果设置为默认地址，先取消该用户的所有默认地址
            orderAddrMapper.cancelDefaultAddr(orderAddr.getUserId());
        }
        orderAddrMapper.save(orderAddr);
    }

    /**
     * 更新收货信息
     * @param orderAddr 收货信息
     */
    @Transactional
    public void update(OrderAddr orderAddr) {
        if (orderAddr.getIsDefault()) {
            // 如果设置为默认地址，先取消该用户的所有默认地址
            orderAddrMapper.cancelDefaultAddr(orderAddr.getUserId());
        }
        orderAddrMapper.update(orderAddr);
    }

    /**
     * 根据收货信息ID删除收货信息
     * @param orderAddrId 收货信息ID
     */
    public void deleteById(Long orderAddrId) {
        orderAddrMapper.deleteById(orderAddrId);
    }

    /**
     * 设置默认收货地址
     * @param userId 用户ID
     * @param orderAddrId 收货信息ID
     */
    @Transactional
    public void setDefaultAddr(Long userId, Long orderAddrId) {
        // 先取消该用户的所有默认地址
        orderAddrMapper.cancelDefaultAddr(userId);
        // 再设置指定地址为默认地址
        orderAddrMapper.setDefaultAddr(userId, orderAddrId);
    }

    /**
     * 插入收货信息
     * @param orderAddr 收货信息
     */
    @Transactional
    public void insertOrderAddr(OrderAddr orderAddr) {
        if (orderAddr.getIsDefault()) {
            // 如果设置为默认地址，先取消该用户的所有默认地址
            orderAddrMapper.cancelDefaultAddr(orderAddr.getUserId());
        }
        orderAddrMapper.save(orderAddr);
    }
}
