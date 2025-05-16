package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.model.Shop;
import com.example.service.ShopService;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private UserIdExtractor userIdExtractor;

    /**
     * 上传店铺Logo
     */
    @PostMapping("/upload-logo")
    public CommonResult<String> uploadShopLogo(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("file") MultipartFile file) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证文件不为空
            if (file == null || file.isEmpty()) {
                return CommonResult.failed("请选择要上传的图片");
            }
            
            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return CommonResult.failed("只能上传图片文件");
            }
            
            // 调用服务上传文件
            String logoUrl = shopService.uploadShopLogo(file);
            
            return CommonResult.success(logoUrl);
        } catch (Exception e) {
            log.error("上传店铺Logo时发生异常", e);
            return CommonResult.failed("上传店铺Logo失败：" + e.getMessage());
        }
    }

    /**
     * 创建店铺
     */
    @PostMapping("")
    public CommonResult<Shop> createShop(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("shopName") String shopName,
            @RequestParam("shopDescription") String shopDescription,
            @RequestParam("contactPhone") String contactPhone,
            @RequestParam("contactEmail") String contactEmail,
            @RequestParam(value = "shopLogo", required = false) MultipartFile shopLogo,
            @RequestParam(value = "shopLogoUrl", required = false) String shopLogoUrl) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }

            // 检查用户是否已有店铺
            Shop existingShop = shopService.getShopByUserId(userId);
            if (existingShop != null) {
                return CommonResult.failed("您已经拥有一个店铺，不能再创建新店铺");
            }

            Shop shop = new Shop();
            shop.setShopName(shopName);
            shop.setShopDescription(shopDescription);
            shop.setContactPhone(contactPhone);
            shop.setContactEmail(contactEmail);
            shop.setUserId(userId);
            shop.setStatus(1); // 默认状态为正常

            // 如果提供了shopLogoUrl，则直接使用该URL
            if (shopLogoUrl != null && !shopLogoUrl.isEmpty()) {
                shop.setShopLogo(shopLogoUrl);
                Shop createdShop = shopService.createShop(shop, null);
                return CommonResult.success(createdShop);
            } else {
                // 否则使用上传的文件
                Shop createdShop = shopService.createShop(shop, shopLogo);
                return CommonResult.success(createdShop);
            }
        } catch (Exception e) {
            log.error("创建店铺时发生异常", e);
            return CommonResult.failed("创建店铺失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定ID的店铺详情
     */
    @GetMapping("/{id}")
    public CommonResult<Shop> getShopById(@PathVariable Long id) {
        try {
            Shop shop = shopService.getShopById(id);
            if (shop == null) {
                return CommonResult.failed("店铺不存在");
            }
            return CommonResult.success(shop);
        } catch (Exception e) {
            log.error("获取店铺详情时发生异常", e);
            return CommonResult.failed("获取店铺详情失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户的店铺信息
     */
    @GetMapping("/user")
    public CommonResult<Shop> getUserShop(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }

            Shop shop = shopService.getShopByUserId(userId);
            if (shop == null) {
                return CommonResult.success(null); // 用户没有店铺，返回空
            }
            return CommonResult.success(shop);
        } catch (Exception e) {
            log.error("获取用户店铺信息时发生异常", e);
            return CommonResult.failed("获取用户店铺信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新店铺基本信息
     */
    @PutMapping("/{id}")
    public CommonResult<Shop> updateShop(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("shopName") String shopName,
            @RequestParam("shopDescription") String shopDescription,
            @RequestParam("contactPhone") String contactPhone,
            @RequestParam("contactEmail") String contactEmail,
            @RequestParam(value = "shopLogo", required = false) MultipartFile shopLogo,
            @RequestParam(value = "shopLogoUrl", required = false) String shopLogoUrl) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }

            // 验证是否为店铺的所有者
            Shop existingShop = shopService.getShopById(id);
            if (existingShop == null) {
                return CommonResult.failed("店铺不存在");
            }

            if (!existingShop.getUserId().equals(userId)) {
                return CommonResult.forbidden("您没有权限修改该店铺");
            }

            existingShop.setShopName(shopName);
            existingShop.setShopDescription(shopDescription);
            existingShop.setContactPhone(contactPhone);
            existingShop.setContactEmail(contactEmail);

            // 如果提供了shopLogoUrl，则直接使用该URL
            if (shopLogoUrl != null && !shopLogoUrl.isEmpty()) {
                existingShop.setShopLogo(shopLogoUrl);
                Shop updatedShop = shopService.updateShop(existingShop, null);
                return CommonResult.success(updatedShop);
            } else {
                // 否则使用上传的文件
                Shop updatedShop = shopService.updateShop(existingShop, shopLogo);
                return CommonResult.success(updatedShop);
            }
        } catch (Exception e) {
            log.error("更新店铺信息时发生异常", e);
            return CommonResult.failed("更新店铺信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新店铺状态
     */
    @PatchMapping("/{id}/status")
    public CommonResult<Shop> updateShopStatus(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader,
            @RequestParam("status") Integer status) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }

            // 验证是否为店铺的所有者
            Shop existingShop = shopService.getShopById(id);
            if (existingShop == null) {
                return CommonResult.failed("店铺不存在");
            }

            if (!existingShop.getUserId().equals(userId)) {
                return CommonResult.forbidden("您没有权限修改该店铺");
            }

            if (status != 0 && status != 1) {
                return CommonResult.failed("无效的状态值");
            }

            Shop updatedShop = shopService.updateShopStatus(id, status);
            return CommonResult.success(updatedShop);
        } catch (Exception e) {
            log.error("更新店铺状态时发生异常", e);
            return CommonResult.failed("更新店铺状态失败：" + e.getMessage());
        }
    }
}