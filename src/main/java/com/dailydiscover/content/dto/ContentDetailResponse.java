package com.dailydiscover.content.dto;

import com.dailydiscover.content.domain.Content;
import com.dailydiscover.scene.domain.Scene;
import com.dailydiscover.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDetailResponse {
    private Long id;
    private String title;
    private String summary;
    private String body;
    private String coverImageUrl;
    private String contentType;
    private Integer priority;
    private LocalDateTime publishAt;
    private List<SceneInfo> scenes;
    private List<ProductInfo> products;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneInfo {
        private Long id;
        private String name;
        private String description;
        private String coverImageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private Long id;
        private String name;
        private String description;
        private String price;
        private String currency;
        private String imageUrl;
        private String productUrl;
    }

    public static ContentDetailResponse fromContent(Content content) {
        ContentDetailResponse resp = new ContentDetailResponse();
        resp.setId(content.getId());
        resp.setTitle(content.getTitle());
        resp.setSummary(content.getSummary());
        resp.setBody(content.getBody());
        resp.setCoverImageUrl(content.getCoverImageUrl());
        resp.setContentType(content.getContentType());
        resp.setPriority(content.getPriority());
        resp.setPublishAt(content.getPublishAt());

        if (content.getScenes() != null) {
            resp.setScenes(content.getScenes().stream()
                    .map(s -> new SceneInfo(s.getId(), s.getName(), s.getDescription(), s.getCoverImageUrl()))
                    .toList());

            // 聚合场景下的所有商品
            Set<Long> productIds = new java.util.HashSet<>();
            List<ProductInfo> products = new java.util.ArrayList<>();
            for (Scene scene : content.getScenes()) {
                if (scene.getProducts() != null) {
                    for (Product p : scene.getProducts()) {
                        if (productIds.add(p.getId())) {
                            products.add(new ProductInfo(
                                    p.getId(),
                                    p.getName(),
                                    p.getDescription(),
                                    p.getPrice().toString(),
                                    p.getCurrency(),
                                    p.getImageUrl(),
                                    p.getProductUrl()
                            ));
                        }
                    }
                }
            }
            resp.setProducts(products);
        }

        return resp;
    }
}