package com.dailydiscover.recommendation.dto;

import com.dailydiscover.content.domain.Content;
import com.dailydiscover.scene.domain.Scene;
import com.dailydiscover.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayDiscoverResponse {
    private List<DiscoverItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscoverItem {
        private Long contentId;
        private String title;
        private String summary;
        private String coverImageUrl;
        private String contentType;
        private Integer priority;
        private List<SceneInfo> scenes;
        private List<ProductInfo> products;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SceneInfo {
        private Long id;
        private String name;
        private String coverImageUrl;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private Long id;
        private String name;
        private String price;
        private String currency;
        private String imageUrl;
        private String productUrl;
    }

    public static DiscoverItem fromContent(Content content) {
        DiscoverItem item = new DiscoverItem();
        item.setContentId(content.getId());
        item.setTitle(content.getTitle());
        item.setSummary(content.getSummary());
        item.setCoverImageUrl(content.getCoverImageUrl());
        item.setContentType(content.getContentType());
        item.setPriority(content.getPriority());

        if (content.getScenes() != null) {
            item.setScenes(content.getScenes().stream()
                    .map(s -> new SceneInfo(s.getId(), s.getName(), s.getCoverImageUrl()))
                    .toList());
        }

        // 获取场景关联的商品
        if (content.getScenes() != null) {
            Set<Long> productIds = new java.util.HashSet<>();
            List<ProductInfo> products = new java.util.ArrayList<>();
            for (Scene scene : content.getScenes()) {
                if (scene.getProducts() != null) {
                    for (Product p : scene.getProducts()) {
                        if (productIds.add(p.getId())) {
                            products.add(new ProductInfo(
                                    p.getId(),
                                    p.getName(),
                                    p.getPrice().toString(),
                                    p.getCurrency(),
                                    p.getImageUrl(),
                                    p.getProductUrl()
                            ));
                        }
                    }
                }
            }
            item.setProducts(products);
        }

        return item;
    }
}