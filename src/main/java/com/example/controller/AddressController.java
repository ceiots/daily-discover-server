package com.example.controller;

import com.example.common.result.Result;
import com.example.model.Address;
import com.example.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressService userAddressService;

    /**
     * 保存收货信息
     * @param userAddress 收货信息
     * @return 操作结果
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody Address userAddress) {
        userAddressService.save(userAddress);
        return Result.success(null);
    }

    /**
     * 更新收货信息
     * @param userAddress 收货信息
     * @return 操作结果
     */
    @PostMapping("/update")
    public Result<Void> update(@RequestBody Address userAddress) {
        userAddressService.update(userAddress);
        return Result.success(null);
    }

    /**
     * 根据收货信息ID删除收货信息
     * @param userAddressId 收货信息ID
     * @return 操作结果
     */
    @PostMapping("/delete/{userAddressId}")
    public Result<Void> deleteById(@PathVariable("userAddressId") Long userAddressId) {
        userAddressService.deleteById(userAddressId);
        return Result.success(null);
    }

    /**
     * 设置默认收货地址
     * @param userId 用户ID
     * @param userAddressId 收货信息ID
     * @return 操作结果
     */
    @PostMapping("/setDefault")
    public Result<Void> setDefaultAddr(@RequestParam("userId") Long userId, @RequestParam("userAddressId") Long userAddressId) {
        userAddressService.setDefaultAddr(userId, userAddressId);
        return Result.success(null);
    }

    /**
     * 根据用户 ID 获取默认收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息
     */
    @GetMapping("/getDefaultByUserId")
    public Result<Address> getDefaultByUserId(@RequestParam("userId") Long userId) {
        Address userAddress = userAddressService.getDefaultByUserId(userId);
        return Result.success(userAddress);
    }

    /**
     * 根据用户 ID 获取所有收货地址信息
     * @param userId 用户 ID
     * @return 收货地址信息列表
     */
    @GetMapping("/getAllByUserId")
    public Result<List<Address>> getAllByUserId(@RequestParam("userId") Long userId) {
        List<Address> userAddressList = userAddressService.getAllByUserId(userId);
        return Result.success(userAddressList);
    }
}