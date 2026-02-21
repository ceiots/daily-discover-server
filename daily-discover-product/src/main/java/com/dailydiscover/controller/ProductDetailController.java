package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductDetail;
import com.dailydiscover.service.ProductDetailService;
import com.dailydiscover.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品详情控制器（电商媒体管理）
 */
@RestController
@RequestMapping("/{productId}/details")
@RequiredArgsConstructor
public class ProductDetailController {
    
    private final ProductDetailService productDetailService;
    private final ProductService productService;

    @GetMapping("/all")
    @ApiLog("获取商品所有媒体详情")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductDetail>> getAllProductMedia(@PathVariable Long productId) {
        try {
            List<ProductDetail> mediaDetails = productDetailService.findByProductId(productId);
            return ResponseEntity.ok(mediaDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/carousel")
    @ApiLog("获取商品轮播图")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductDetail>> getProductCarousel(@PathVariable Long productId) {
        try {
            List<ProductDetail> carouselImages = productDetailService.getProductCarousel(productId);
            return ResponseEntity.ok(carouselImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/detail-images")
    @ApiLog("获取商品详情图")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductDetail>> getProductDetailImages(@PathVariable Long productId) {
        try {
            List<ProductDetail> detailImages = productDetailService.getProductDetailImages(productId);
            return ResponseEntity.ok(detailImages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/videos")
    @ApiLog("获取商品视频")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductDetail>> getProductVideos(@PathVariable Long productId) {
        try {
            List<ProductDetail> videos = productDetailService.getProductVideos(productId);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/images")
    @ApiLog("获取商品图片")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductDetail>> getProductImages(@PathVariable Long productId) {
        try {
            List<ProductDetail> images = productDetailService.getProductImages(productId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/media-type/{mediaType}")
    @ApiLog("根据媒体类型获取商品媒体")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ProductDetail>> getProductMediaByType(@PathVariable Long productId, 
                                                                    @PathVariable Integer mediaType) {
        try {
            List<ProductDetail> mediaDetails = productDetailService.findByProductIdAndMediaType(productId, mediaType);
            return ResponseEntity.ok(mediaDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/related")
    @ApiLog("获取相关商品推荐")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<Product>> getRelatedProducts(@PathVariable Long productId) {
        try {
            Product product = productService.findById(productId);
            if (product != null) {
                List<Product> products = productService.findAll();
                products = products.stream()
                    .filter(p -> !p.getId().equals(productId))
                    .limit(10)
                    .collect(Collectors.toList());
                return ResponseEntity.ok(products);
            }
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
