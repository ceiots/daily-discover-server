package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.model.OrderAddr;
import com.example.service.OrderAddrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orderAddr")
public class OrderAddrController {

    @Autowired
    private OrderAddrService orderAddrService;

    /**
     * 根据收货信息ID获取收货信息
     * @param orderAddrId 收货信息ID
     * @return 收货信息
     */
    @GetMapping("/{orderAddrId}")
    public CommonResult<OrderAddr> getByOrderAddrId(@PathVariable("orderAddrId") Long orderAddrId) {
        OrderAddr orderAddr = orderAddrService.getByOrderAddrId(orderAddrId);
        return CommonResult.success(orderAddr);
    }

    /**
     * 保存收货信息
     * @param orderAddr 收货信息
     * @return 操作结果
     */
    @PostMapping("/save")
    public CommonResult<Void> save(@RequestBody OrderAddr orderAddr) {
        orderAddrService.save(orderAddr);
        return CommonResult.success(null);
    }

    /**
     * 更新收货信息
     * @param orderAddr 收货信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public CommonResult<Void> update(@RequestBody OrderAddr orderAddr) {
        orderAddrService.update(orderAddr);
        return CommonResult.success(null);
    }

    /**
     * 根据收货信息ID删除收货信息
     * @param orderAddrId 收货信息ID
     * @return 操作结果
     */
    @PostMapping("/delete/{orderAddrId}")
    public CommonResult<Void> deleteById(@PathVariable("orderAddrId") Long orderAddrId) {
        orderAddrService.deleteById(orderAddrId);
        return CommonResult.success(null);
    }

    /**
     * 设置默认收货地址
     * @param userId 用户ID
     * @param orderAddrId 收货信息ID
     * @return 操作结果
     */
    @PostMapping("/setDefault")
    public CommonResult<Void> setDefaultAddr(@RequestParam("userId") Long userId, @RequestParam("orderAddrId") Long orderAddrId) {
        orderAddrService.setDefaultAddr(userId, orderAddrId);
        return CommonResult.success(null);
    }

    /**
     * 根据用户 ID 获取默认收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息
     */
    @GetMapping("/getDefaultByUserId")
    public CommonResult<OrderAddr> getDefaultByUserId(@RequestParam("userId") Long userId) {
        OrderAddr orderAddr = orderAddrService.getDefaultByUserId(userId);
        return CommonResult.success(orderAddr);
    }

    /**
     * 根据用户 ID 获取所有收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息列表
     */
    @GetMapping("/getAllByUserId")
    public CommonResult<List<OrderAddr>> getAllByUserId(@RequestParam("userId") Long userId) {
        List<OrderAddr> orderAddrList = orderAddrService.getAllByUserId(userId);
        return CommonResult.success(orderAddrList);
    }
}