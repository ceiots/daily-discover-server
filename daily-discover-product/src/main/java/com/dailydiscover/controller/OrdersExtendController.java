package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.OrdersExtend;
import com.dailydiscover.service.OrdersExtendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders/extend")
@RequiredArgsConstructor
public class OrdersExtendController {

    private final OrdersExtendService ordersExtendService;

    @GetMapping
    @ApiLog("获取所有订单扩展信息")
    public ResponseEntity<List<OrdersExtend>> getAllOrdersExtend() {
        try {
            List<OrdersExtend> ordersExtend = ordersExtendService.list();
            return ResponseEntity.ok(ordersExtend);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{orderId}")
    @ApiLog("根据订单ID获取订单扩展信息")
    public ResponseEntity<OrdersExtend> getOrderExtendByOrderId(@PathVariable Long orderId) {
        try {
            OrdersExtend orderExtend = ordersExtendService.getById(orderId);
            return orderExtend != null ? ResponseEntity.ok(orderExtend) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/address/{addressId}")
    @ApiLog("根据地址ID获取订单扩展信息")
    public ResponseEntity<OrdersExtend> getOrderExtendByAddressId(@PathVariable Long addressId) {
        try {
            OrdersExtend orderExtend = ordersExtendService.findByAddressId(addressId);
            return orderExtend != null ? ResponseEntity.ok(orderExtend) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/coupon/{couponId}")
    @ApiLog("根据优惠券ID获取订单扩展信息")
    public ResponseEntity<List<OrdersExtend>> getOrdersExtendByCouponId(@PathVariable Long couponId) {
        try {
            List<OrdersExtend> ordersExtend = ordersExtendService.findByCouponId(couponId);
            return ResponseEntity.ok(ordersExtend);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建订单扩展信息")
    public ResponseEntity<OrdersExtend> createOrderExtend(@RequestBody OrdersExtend orderExtend) {
        try {
            boolean success = ordersExtendService.save(orderExtend);
            return success ? ResponseEntity.ok(orderExtend) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{orderId}")
    @ApiLog("更新订单扩展信息")
    public ResponseEntity<OrdersExtend> updateOrderExtend(@PathVariable Long orderId, @RequestBody OrdersExtend orderExtend) {
        try {
            orderExtend.setOrderId(orderId);
            boolean success = ordersExtendService.updateById(orderExtend);
            return success ? ResponseEntity.ok(orderExtend) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{orderId}")
    @ApiLog("删除订单扩展信息")
    public ResponseEntity<Void> deleteOrderExtend(@PathVariable Long orderId) {
        try {
            boolean success = ordersExtendService.removeById(orderId);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}