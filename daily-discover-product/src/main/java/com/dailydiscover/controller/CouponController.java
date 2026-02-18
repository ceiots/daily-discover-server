package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.Coupon;
import com.dailydiscover.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    @ApiLog("获取所有优惠券")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        try {
            List<Coupon> coupons = couponService.list();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取优惠券")
    public ResponseEntity<Coupon> getCouponById(@PathVariable Long id) {
        try {
            Coupon coupon = couponService.getById(id);
            return coupon != null ? ResponseEntity.ok(coupon) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @ApiLog("获取有效优惠券")
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        try {
            List<Coupon> coupons = couponService.findActiveCoupons();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/available")
    @ApiLog("获取可用优惠券")
    public ResponseEntity<List<Coupon>> getAvailableCoupons() {
        try {
            List<Coupon> coupons = couponService.findAvailableCoupons();
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{couponType}")
    @ApiLog("根据优惠券类型获取优惠券")
    public ResponseEntity<List<Coupon>> getCouponsByType(@PathVariable String couponType) {
        try {
            List<Coupon> coupons = couponService.findByCouponType(couponType);
            return ResponseEntity.ok(coupons);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{couponCode}")
    @ApiLog("根据优惠券代码获取优惠券")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String couponCode) {
        try {
            Coupon coupon = couponService.findByCouponCode(couponCode);
            return coupon != null ? ResponseEntity.ok(coupon) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建优惠券")
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        try {
            boolean success = couponService.save(coupon);
            return success ? ResponseEntity.ok(coupon) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新优惠券")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        try {
            coupon.setId(id);
            boolean success = couponService.updateById(coupon);
            return success ? ResponseEntity.ok(coupon) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除优惠券")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        try {
            boolean success = couponService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}