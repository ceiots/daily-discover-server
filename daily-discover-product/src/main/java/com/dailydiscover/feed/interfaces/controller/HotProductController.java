package com.dailydiscover.feed.interfaces.controller;

import com.dailydiscover.common.logging.ApiLog;
import com.dailydiscover.feed.application.dto.HotProductQuery;
import com.dailydiscover.feed.application.service.HotProductApplicationService;
import com.dailydiscover.feed.domain.model.HotProduct;
import com.dailydiscover.feed.interfaces.dto.HotProductCardResponse;
import com.dailydiscover.feed.interfaces.dto.HotProductPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feed/hot")
@RequiredArgsConstructor
public class HotProductController {

    private final HotProductApplicationService hotProductApplicationService;

    @GetMapping
    @ApiLog("获取今日热点列表")
    @PreAuthorize("permitAll()")
    public ResponseEntity<HotProductPageResponse> getHotProducts(
            @RequestParam(defaultValue = "trending") String tab,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            HotProductQuery query = HotProductQuery.of(tab, page, size);
            List<HotProduct> products = hotProductApplicationService.getHotProducts(query);
            long total = hotProductApplicationService.countProducts(query);

            List<HotProductCardResponse> items = products.stream()
                    .map(HotProductCardResponse::from)
                    .collect(Collectors.toList());

            boolean hasMore = (long) (page - 1) * size + items.size() < total;

            HotProductPageResponse response = HotProductPageResponse.builder()
                    .items(items)
                    .hasMore(hasMore)
                    .total(total)
                    .page(page)
                    .size(size)
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{productId}")
    @ApiLog("获取今日热点商品卡片详情")
    @PreAuthorize("permitAll()")
    public ResponseEntity<HotProductCardResponse> getHotProductDetail(@PathVariable Long productId) {
        try {
            HotProduct product = hotProductApplicationService.getProductDetail(productId);
            if (product != null) {
                return ResponseEntity.ok(HotProductCardResponse.from(product));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}