package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductTag;
import com.dailydiscover.service.ProductTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-tags")
@RequiredArgsConstructor
public class ProductTagController {

    private final ProductTagService productTagService;

    @GetMapping
    @ApiLog("获取所有商品标签")
    public ResponseEntity<List<ProductTag>> getAllProductTags() {
        try {
            List<ProductTag> tags = productTagService.list();
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品标签")
    public ResponseEntity<ProductTag> getProductTagById(@PathVariable Long id) {
        try {
            ProductTag tag = productTagService.getById(id);
            return tag != null ? ResponseEntity.ok(tag) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/name/{tagName}")
    @ApiLog("根据标签名称获取商品标签")
    public ResponseEntity<ProductTag> getProductTagByName(@PathVariable String tagName) {
        try {
            ProductTag tag = productTagService.getByTagName(tagName);
            return tag != null ? ResponseEntity.ok(tag) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/popular")
    @ApiLog("获取热门商品标签")
    public ResponseEntity<List<ProductTag>> getPopularProductTags(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<ProductTag> tags = productTagService.getPopularTags(limit);
            return ResponseEntity.ok(tags);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建商品标签")
    public ResponseEntity<ProductTag> createProductTag(@RequestBody ProductTag tag) {
        try {
            boolean success = productTagService.save(tag);
            return success ? ResponseEntity.ok(tag) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新商品标签")
    public ResponseEntity<ProductTag> updateProductTag(@PathVariable Long id, @RequestBody ProductTag tag) {
        try {
            tag.setId(id);
            boolean success = productTagService.updateById(tag);
            return success ? ResponseEntity.ok(tag) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除商品标签")
    public ResponseEntity<Void> deleteProductTag(@PathVariable Long id) {
        try {
            boolean success = productTagService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}