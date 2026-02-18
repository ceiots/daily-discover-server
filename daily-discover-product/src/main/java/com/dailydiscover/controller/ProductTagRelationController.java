package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductTagRelation;
import com.dailydiscover.service.ProductTagRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-tag-relations")
@RequiredArgsConstructor
public class ProductTagRelationController {

    private final ProductTagRelationService productTagRelationService;

    @GetMapping
    @ApiLog("获取所有商品标签关系")
    public ResponseEntity<List<ProductTagRelation>> getAllProductTagRelations() {
        try {
            List<ProductTagRelation> relations = productTagRelationService.list();
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取商品标签关系")
    public ResponseEntity<ProductTagRelation> getProductTagRelationById(@PathVariable Long id) {
        try {
            ProductTagRelation relation = productTagRelationService.getById(id);
            return relation != null ? ResponseEntity.ok(relation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取标签关系")
    public ResponseEntity<List<ProductTagRelation>> getProductTagRelationsByProductId(@PathVariable Long productId) {
        try {
            List<ProductTagRelation> relations = productTagRelationService.getByProductId(productId);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tag/{tagId}")
    @ApiLog("根据标签ID获取标签关系")
    public ResponseEntity<List<ProductTagRelation>> getProductTagRelationsByTagId(@PathVariable Long tagId) {
        try {
            List<ProductTagRelation> relations = productTagRelationService.getByTagId(tagId);
            return ResponseEntity.ok(relations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建商品标签关系")
    public ResponseEntity<ProductTagRelation> createProductTagRelation(@RequestBody ProductTagRelation relation) {
        try {
            boolean success = productTagRelationService.save(relation);
            return success ? ResponseEntity.ok(relation) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/batch")
    @ApiLog("批量创建商品标签关系")
    public ResponseEntity<Void> batchCreateProductTagRelations(@RequestBody List<ProductTagRelation> relations) {
        try {
            boolean success = productTagRelationService.saveBatch(relations);
            return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除商品标签关系")
    public ResponseEntity<Void> deleteProductTagRelation(@PathVariable Long id) {
        try {
            boolean success = productTagRelationService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/product/{productId}/tag/{tagId}")
    @ApiLog("根据商品ID和标签ID删除标签关系")
    public ResponseEntity<Void> deleteProductTagRelationByProductAndTag(@PathVariable Long productId, @PathVariable Long tagId) {
        try {
            boolean success = productTagRelationService.removeByProductIdAndTagId(productId, tagId);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}