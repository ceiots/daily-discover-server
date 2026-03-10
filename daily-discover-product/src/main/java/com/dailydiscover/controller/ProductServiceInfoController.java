package com.dailydiscover.controller;

import com.dailydiscover.dto.ProductServiceInfoDTO;
import com.dailydiscover.service.ProductServiceInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品服务信息控制器
 */
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductServiceInfoController {
    
    private final ProductServiceInfoService productServiceInfoService;
    
    /**
     * 获取商品的产品服务信息
     */
    @GetMapping("/{productId}/service-info")
    public ResponseEntity<List<ProductServiceInfoDTO>> getProductServiceInfo(
            @PathVariable("productId") Long productId) {
        
        List<ProductServiceInfoDTO> serviceInfo = productServiceInfoService.getProductServiceInfo(productId);
        
        if (serviceInfo.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(serviceInfo);
    }
    
    /**
     * 获取所有启用的产品服务信息分类
     */
    @GetMapping("/service-info/categories")
    public ResponseEntity<List<ProductServiceInfoDTO.ServiceCategoryDTO>> getServiceInfoCategories() {
        
        List<ProductServiceInfoDTO.ServiceCategoryDTO> categories = 
            productServiceInfoService.getEnabledCategories();
        
        if (categories.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 根据分类ID获取信息项
     */
    @GetMapping("/service-info/categories/{categoryId}/items")
    public ResponseEntity<List<ProductServiceInfoDTO>> getInfoItemsByCategoryId(
            @PathVariable("categoryId") Long categoryId) {
        
        List<ProductServiceInfoDTO> infoItems = productServiceInfoService.getInfoItemsByCategoryId(categoryId);
        
        if (infoItems.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(infoItems);
    }
}