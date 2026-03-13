package com.dailydiscover.recommendation.client;

import com.dailydiscover.recommendation.dto.RecommendationProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 产品服务Feign客户端
 * 通过HTTP调用外部产品服务，实现服务间解耦
 * 
 * 注意：当前项目为单体Spring Boot应用，使用path配置
 * 调用同一项目内的ProductController（路径为根路径）
 * 禁用负载均衡，直接调用本地服务
 */
@FeignClient(name = "product-service", url = "http://localhost:8092")
public interface ProductServiceClient {
    
    /**
     * 根据商品ID获取商品信息
     * 对应ProductController.getProductById方法
     * @param productId 商品ID
     * @return 推荐服务专用商品DTO
     */
    @GetMapping("/{productId}")
    RecommendationProductDTO getProductById(@PathVariable("productId") Long productId);
    
    /**
     * 获取热门产品
     * 对应ProductController.getHotProducts方法
     * @return 推荐商品列表
     */
    @GetMapping("/hot")
    List<RecommendationProductDTO> getHotProducts();
    
    /**
     * 获取新品产品
     * 对应ProductController.getNewProducts方法
     * @return 推荐商品列表
     */
    @GetMapping("/new")
    List<RecommendationProductDTO> getNewProducts();
}