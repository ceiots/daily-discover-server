package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductSearchKeyword;
import com.dailydiscover.service.ProductSearchKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-search-keywords")
@RequiredArgsConstructor
public class ProductSearchKeywordController {

    private final ProductSearchKeywordService productSearchKeywordService;

    @GetMapping
    @ApiLog("获取所有商品搜索关键词")
    public ResponseEntity<List<ProductSearchKeyword>> getAllProductSearchKeywords() {
        try {
            List<ProductSearchKeyword> keywords = productSearchKeywordService.list();
            return ResponseEntity.ok(keywords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品搜索关键词")
    public ResponseEntity<ProductSearchKeyword> getProductSearchKeywordById(@PathVariable Long id) {
        try {
            ProductSearchKeyword keyword = productSearchKeywordService.getById(id);
            return keyword != null ? ResponseEntity.ok(keyword) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/keyword/{keyword}")
    @ApiLog("根据关键词获取搜索记录")
    public ResponseEntity<ProductSearchKeyword> getProductSearchKeywordByKeyword(@PathVariable String keyword) {
        try {
            ProductSearchKeyword searchKeyword = productSearchKeywordService.getByKeyword(keyword);
            return searchKeyword != null ? ResponseEntity.ok(searchKeyword) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/popular")
    @ApiLog("获取热门搜索关键词")
    public ResponseEntity<List<ProductSearchKeyword>> getPopularProductSearchKeywords(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<ProductSearchKeyword> keywords = productSearchKeywordService.getPopularKeywords(limit);
            return ResponseEntity.ok(keywords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/trending")
    @ApiLog("获取趋势搜索关键词")
    public ResponseEntity<List<ProductSearchKeyword>> getTrendingProductSearchKeywords(@RequestParam(defaultValue = "10") int limit) {
        try {
            List<ProductSearchKeyword> keywords = productSearchKeywordService.getTrendingKeywords(limit);
            return ResponseEntity.ok(keywords);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建商品搜索关键词")
    public ResponseEntity<ProductSearchKeyword> createProductSearchKeyword(@RequestBody ProductSearchKeyword keyword) {
        try {
            boolean success = productSearchKeywordService.save(keyword);
            return success ? ResponseEntity.ok(keyword) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新商品搜索关键词")
    public ResponseEntity<ProductSearchKeyword> updateProductSearchKeyword(@PathVariable Long id, @RequestBody ProductSearchKeyword keyword) {
        try {
            keyword.setId(id);
            boolean success = productSearchKeywordService.updateById(keyword);
            return success ? ResponseEntity.ok(keyword) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/increment")
    @ApiLog("增加搜索次数")
    public ResponseEntity<Void> incrementSearchCount(@PathVariable Long id) {
        try {
            boolean success = productSearchKeywordService.incrementSearchCount(id);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除商品搜索关键词")
    public ResponseEntity<Void> deleteProductSearchKeyword(@PathVariable Long id) {
        try {
            boolean success = productSearchKeywordService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}