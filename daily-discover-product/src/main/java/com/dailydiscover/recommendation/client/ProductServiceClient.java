package com.dailydiscover.recommendation.client;

import com.dailydiscover.recommendation.dto.RecommendationProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 产品服务Feign客户端
 * 通过HTTP调用外部产品服务，实现服务间解耦
 * 
 * 注意：当前配置为调用同一项目内的ProductController
 * 在生产环境中，应配置为调用独立的产品服务
 */
@FeignClient(name = "product-service", url = "http://localhost:8080")
public interface ProductServiceClient {
    
    /**
     * 根据商品ID获取商品信息
     * @param productId 商品ID
     * @return 商品信息DTO
     */
    @GetMapping("/{productId}")
    RecommendationProductDTO getProductById(@PathVariable("productId") Long productId);
    
    /**
     * 批量获取商品信息
     * @param productIds 商品ID列表
     * @return 商品信息DTO列表
     */
    @PostMapping("/batch")
    List<RecommendationProductDTO> getProductsByIds(@RequestBody List<Long> productIds);
    
    /**
     * 根据分类获取热门商品
     * @param category 商品分类
     * @param limit 限制数量
     * @return 商品信息DTO列表
     */
    @GetMapping("/category/{category}/hot")
    List<RecommendationProductDTO> getHotProductsByCategory(
            @PathVariable("category") String category,
            @PathVariable("limit") int limit);
    
    /**
     * 获取通用热门商品
     * @param limit 限制数量
     * @return 商品信息DTO列表
     */
    @GetMapping("/hot")
    List<RecommendationProductDTO> getGeneralHotProducts(@PathVariable("limit") int limit);
}