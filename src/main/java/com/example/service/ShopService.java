package com.example.service;

import com.example.model.Shop;
import org.springframework.web.multipart.MultipartFile;

public interface ShopService {
    
    /**
     * 创建店铺
     * @param shop 店铺信息
     * @param logo 店铺logo
     * @return 创建后的店铺
     */
    Shop createShop(Shop shop, MultipartFile logo);
    
    /**
     * 根据ID获取店铺信息
     * @param id 店铺ID
     * @return 店铺信息
     */
    Shop getShopById(Long id);
    
    /**
     * 根据用户ID获取店铺信息
     * @param userId 用户ID
     * @return 店铺信息，若不存在则返回null
     */
    Shop getShopByUserId(Long userId);
    
    /**
     * 更新店铺信息
     * @param shop 店铺信息
     * @param logo 店铺logo（可为null）
     * @return 更新后的店铺
     */
    Shop updateShop(Shop shop, MultipartFile logo);
    
    /**
     * 更新店铺状态
     * @param id 店铺ID
     * @param status 状态值（0-下线, 1-正常）
     * @return 更新后的店铺
     */
    Shop updateShopStatus(Long id, Integer status);
    
    /**
     * 上传店铺Logo
     * @param logo 店铺logo文件
     * @return 上传后的图片URL
     */
    String uploadShopLogo(MultipartFile logo);
}