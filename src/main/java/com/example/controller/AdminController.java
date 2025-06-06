package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.common.api.CommonResult;
import com.example.model.Product;
import com.example.model.Shop;
import com.example.model.User;
import com.example.service.ProductService;
import com.example.service.ShopService;
import com.example.service.UserService;
import com.example.util.UserIdExtractor;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器 - 仅官方账号可访问
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;

    /**
     * 检查是否为官方账号（管理员）
     */
    private boolean isOfficialAccount(Long userId) {
        User user = userService.findUserById(userId);
        return user != null && user.getIsOfficial() != null && user.getIsOfficial();
    }

    /**
     * 获取平台统计数据
     */
    @GetMapping("/dashboard")
    public CommonResult<Map<String, Object>> getDashboardStats(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证是否为官方账号
            if (!isOfficialAccount(userId)) {
                return CommonResult.forbidden("非官方账号无权访问");
            }
            
            // 收集统计数据
            Map<String, Object> stats = new HashMap<>();
            
            // 用户总数
            int userCount = userService.countUsers();
            stats.put("userCount", userCount);
            
            // 店铺总数
            int shopCount = shopService.countShops();
            stats.put("shopCount", shopCount);
            
            // 商品总数
            int productCount = productService.countProducts();
            stats.put("productCount", productCount);
            
            // 待审核商品数
            List<Product> pendingProducts = productService.getPendingAuditProducts();
            stats.put("pendingProductCount", pendingProducts.size());
            
            return CommonResult.success(stats);
        } catch (Exception e) {
            log.error("获取管理员统计数据时发生异常", e);
            return CommonResult.failed("获取管理员统计数据失败：" + e.getMessage());
        }
    }

    /**
     * 获取待审核的商品列表
     */
    @GetMapping("/products/pending")
    public CommonResult<List<Product>> getPendingProducts(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证是否为官方账号
            if (!isOfficialAccount(userId)) {
                return CommonResult.forbidden("非官方账号无权访问");
            }
            
            List<Product> pendingProducts = productService.getPendingAuditProducts();
            return CommonResult.success(pendingProducts);
        } catch (Exception e) {
            log.error("获取待审核商品列表时发生异常", e);
            return CommonResult.failed("获取待审核商品列表失败：" + e.getMessage());
        }
    }

    /**
     * 审核商品
     */
    @PostMapping("/products/{id}/audit")
    public CommonResult<Product> auditProduct(
            @PathVariable Long id,
            @RequestParam Integer auditStatus,
            @RequestParam(required = false) String auditRemark,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证是否为官方账号
            if (!isOfficialAccount(userId)) {
                return CommonResult.forbidden("非官方账号无权访问");
            }
            
            // 验证审核状态值
            if (auditStatus != 1 && auditStatus != 2) {
                return CommonResult.failed("无效的审核状态");
            }
            
            Product product = productService.auditProduct(id, auditStatus, auditRemark);
            return CommonResult.success(product);
        } catch (Exception e) {
            log.error("审核商品时发生异常", e);
            return CommonResult.failed("审核商品失败：" + e.getMessage());
        }
    }

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public CommonResult<List<User>> getAllUsers(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证是否为官方账号
            if (!isOfficialAccount(userId)) {
                return CommonResult.forbidden("非官方账号无权访问");
            }
            
            List<User> users = userService.getAllUsers();
            return CommonResult.success(users);
        } catch (Exception e) {
            log.error("获取所有用户列表时发生异常", e);
            return CommonResult.failed("获取所有用户列表失败：" + e.getMessage());
        }
    }

    /**
     * 设置用户为官方账号
     */
    @PostMapping("/users/{id}/set-official")
    public CommonResult<User> setUserOfficial(
            @PathVariable Long id,
            @RequestParam Boolean isOfficial,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证是否为官方账号
            if (!isOfficialAccount(userId)) {
                return CommonResult.forbidden("非官方账号无权访问");
            }
            
            User user = userService.findUserById(id);
            if (user == null) {
                return CommonResult.failed("用户不存在");
            }
            
            user.setIsOfficial(isOfficial);
            boolean success = userService.updateUser(user);
            
            if (success) {
                return CommonResult.success(user);
            } else {
                return CommonResult.failed("设置官方账号状态失败");
            }
        } catch (Exception e) {
            log.error("设置用户官方账号状态时发生异常", e);
            return CommonResult.failed("设置用户官方账号状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取待审核的店铺列表
     */
    @GetMapping("/shops/pending")
    public CommonResult<List<Shop>> getPendingShops(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证是否为官方账号
            if (!isOfficialAccount(userId)) {
                return CommonResult.forbidden("非官方账号无权访问");
            }
            
            List<Shop> pendingShops = shopService.findPendingShops();
            return CommonResult.success(pendingShops);
        } catch (Exception e) {
            log.error("获取待审核店铺列表时发生异常", e);
            return CommonResult.failed("获取待审核店铺列表失败：" + e.getMessage());
        }
    }

    /**
     * 审核店铺
     */
    @PostMapping("/shops/{id}/audit")
    public CommonResult<Shop> auditShop(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestHeader(value = "userId", required = false) String userIdHeader) {
        try {
            Long userId = userIdExtractor.extractUserId(token, userIdHeader);
            if (userId == null) {
                return CommonResult.unauthorized(null);
            }
            
            // 验证是否为官方账号
            if (!isOfficialAccount(userId)) {
                return CommonResult.forbidden("非官方账号无权访问");
            }
            
            // 验证状态值
            if (status != 0 && status != 1) {
                return CommonResult.failed("无效的状态值");
            }
            
            Shop shop = shopService.updateShopStatus(id, status);
            return CommonResult.success(shop);
        } catch (Exception e) {
            log.error("审核店铺时发生异常", e);
            return CommonResult.failed("审核店铺失败：" + e.getMessage());
        }
    }
}