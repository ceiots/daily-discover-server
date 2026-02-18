package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.SellerProfile;
import com.dailydiscover.service.SellerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller-profiles")
@RequiredArgsConstructor
public class SellerProfileController {

    private final SellerProfileService sellerProfileService;

    @GetMapping
    @ApiLog("获取所有卖家资料")
    public ResponseEntity<List<SellerProfile>> getAllSellerProfiles() {
        try {
            List<SellerProfile> profiles = sellerProfileService.list();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取卖家资料")
    public ResponseEntity<SellerProfile> getSellerProfileById(@PathVariable Long id) {
        try {
            SellerProfile profile = sellerProfileService.getById(id);
            return profile != null ? ResponseEntity.ok(profile) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/seller/{sellerId}")
    @ApiLog("根据卖家ID查询卖家资料")
    public ResponseEntity<SellerProfile> getSellerProfileBySellerId(@PathVariable Long sellerId) {
        try {
            SellerProfile profile = sellerProfileService.getBySellerId(sellerId);
            return profile != null ? ResponseEntity.ok(profile) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/high-rated")
    @ApiLog("获取高评分卖家列表")
    public ResponseEntity<List<SellerProfile>> getHighRatedSellers(@RequestParam(defaultValue = "4.0") Double minRating,
                                                                @RequestParam(defaultValue = "10") int limit) {
        try {
            List<SellerProfile> profiles = sellerProfileService.getHighRatedSellers(minRating, limit);
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/stats")
    @ApiLog("获取卖家统计信息")
    public ResponseEntity<Map<String, Object>> getSellerStats() {
        try {
            Map<String, Object> stats = sellerProfileService.getSellerStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建卖家资料")
    public ResponseEntity<SellerProfile> createSellerProfile(@RequestBody SellerProfile profile) {
        try {
            boolean success = sellerProfileService.save(profile);
            return success ? ResponseEntity.ok(profile) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新卖家资料")
    public ResponseEntity<SellerProfile> updateSellerProfile(@PathVariable Long id, @RequestBody SellerProfile profile) {
        try {
            profile.setId(id);
            boolean success = sellerProfileService.updateById(profile);
            return success ? ResponseEntity.ok(profile) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/rating")
    @ApiLog("更新卖家评分")
    public ResponseEntity<Void> updateSellerRating(@PathVariable Long id, 
                                                  @RequestParam Double rating,
                                                  @RequestParam Integer totalReviews) {
        try {
            boolean success = sellerProfileService.updateSellerRating(id, rating, totalReviews);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @ApiLog("更新卖家状态")
    public ResponseEntity<Void> updateSellerStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean success = sellerProfileService.updateSellerStatus(id, status);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除卖家资料")
    public ResponseEntity<Void> deleteSellerProfile(@PathVariable Long id) {
        try {
            boolean success = sellerProfileService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}