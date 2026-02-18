package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.OrderShipping;
import com.dailydiscover.service.OrderShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-shipping")
@RequiredArgsConstructor
public class OrderShippingController {

    private final OrderShippingService orderShippingService;

    @GetMapping
    @ApiLog("获取所有订单物流信息")
    public ResponseEntity<List<OrderShipping>> getAllOrderShipping() {
        try {
            List<OrderShipping> shipping = orderShippingService.list();
            return ResponseEntity.ok(shipping);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取订单物流信息")
    public ResponseEntity<OrderShipping> getOrderShippingById(@PathVariable Long id) {
        try {
            OrderShipping shipping = orderShippingService.getById(id);
            return shipping != null ? ResponseEntity.ok(shipping) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取物流信息")
    public ResponseEntity<OrderShipping> getOrderShippingByOrderId(@PathVariable Long orderId) {
        try {
            OrderShipping shipping = orderShippingService.findByOrderId(orderId);
            return shipping != null ? ResponseEntity.ok(shipping) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建订单物流信息")
    public ResponseEntity<OrderShipping> createOrderShipping(@RequestBody OrderShipping shipping) {
        try {
            boolean success = orderShippingService.save(shipping);
            return success ? ResponseEntity.ok(shipping) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新订单物流信息")
    public ResponseEntity<OrderShipping> updateOrderShipping(@PathVariable Long id, @RequestBody OrderShipping shipping) {
        try {
            shipping.setId(id);
            boolean success = orderShippingService.updateById(shipping);
            return success ? ResponseEntity.ok(shipping) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除订单物流信息")
    public ResponseEntity<Void> deleteOrderShipping(@PathVariable Long id) {
        try {
            boolean success = orderShippingService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}