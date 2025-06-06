package com.example.service.impl;

import com.example.mapper.ShopMapper;
import com.example.model.Shop;
import com.example.service.ShopService;
import com.example.util.FileUploadUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopMapper shopMapper;
    
    @Autowired
    private FileUploadUtil fileUploadUtil;

    @Override
    public Shop createShop(Shop shop, MultipartFile logo) {
        try {
            // 设置店铺创建和更新时间
            LocalDateTime now = LocalDateTime.now();
            shop.setCreatedAt(now);
            shop.setUpdatedAt(now);
            
            // 如果有上传Logo，则处理上传
            if (logo != null && !logo.isEmpty()) {
                String logoPath = fileUploadUtil.uploadFile(logo, "shop/logo");
                shop.setShopLogo(logoPath);
            }
            
            // 插入店铺信息到数据库
            shopMapper.insert(shop);
            
            return shop;
        } catch (Exception e) {
            log.error("创建店铺时发生异常", e);
            throw new RuntimeException("创建店铺失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Shop getShopById(Long id) {
        try {
            return shopMapper.findById(id);
        } catch (Exception e) {
            log.error("根据ID获取店铺时发生异常", e);
            throw new RuntimeException("获取店铺信息失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Shop getShopByUserId(Long userId) {
        try {
            List<Shop> shops = shopMapper.findByUserId(userId);
            // 一个用户只能有一个店铺，所以取第一个
            return shops.isEmpty() ? null : shops.get(0);
        } catch (Exception e) {
            log.error("根据用户ID获取店铺时发生异常", e);
            throw new RuntimeException("获取用户店铺失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Shop updateShop(Shop shop, MultipartFile logo) {
        try {
            // 设置更新时间
            shop.setUpdatedAt(LocalDateTime.now());
            
            // 如果有上传新Logo，则处理上传
            if (logo != null && !logo.isEmpty()) {
                String logoPath = fileUploadUtil.uploadFile(logo, "shop/logo");
                shop.setShopLogo(logoPath);
            }
            
            // 更新店铺信息
            shopMapper.update(shop);
            
            return shopMapper.findById(shop.getId());
        } catch (Exception e) {
            log.error("更新店铺信息时发生异常", e);
            throw new RuntimeException("更新店铺信息失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Shop updateShopStatus(Long id, Integer status) {
        try {
            // 更新店铺状态
            shopMapper.updateStatus(id, status);
            
            return shopMapper.findById(id);
        } catch (Exception e) {
            log.error("更新店铺状态时发生异常", e);
            throw new RuntimeException("更新店铺状态失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public String uploadShopLogo(MultipartFile logo) {
        try {
            if (logo == null || logo.isEmpty()) {
                throw new IllegalArgumentException("上传的Logo不能为空");
            }
            
            // 使用FileUploadUtil上传文件到shop/logo目录
            return fileUploadUtil.uploadFile(logo, "shop/logo");
        } catch (Exception e) {
            log.error("上传店铺Logo时发生异常", e);
            throw new RuntimeException("上传店铺Logo失败：" + e.getMessage(), e);
        }
    }

    @Override
    public Long getShopOwnerIdByShopId(Long shopId) {
        try {
            Shop shop = shopMapper.findById(shopId);
            return shop != null ? shop.getUserId() : null;
        } catch (Exception e) {
            log.error("根据店铺ID获取店铺拥有者ID时发生异常", e);
            throw new RuntimeException("获取店铺拥有者ID失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Shop> findPendingShops() {
        try {
            // 获取状态为0(待审核)的店铺列表
            return shopMapper.findByStatus(0);
        } catch (Exception e) {
            log.error("获取待审核店铺列表时发生异常", e);
            throw new RuntimeException("获取待审核店铺列表失败：" + e.getMessage(), e);
        }
    }
    
    @Override
    public int countShops() {
        try {
            return shopMapper.countShops();
        } catch (Exception e) {
            log.error("统计店铺总数时发生异常", e);
            throw new RuntimeException("统计店铺总数失败：" + e.getMessage(), e);
        }
    }
}