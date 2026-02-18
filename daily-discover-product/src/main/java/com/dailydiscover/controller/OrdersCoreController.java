package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.OrdersCore;
import com.dailydiscover.service.OrdersCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders/core")
@RequiredArgsConstructor
public class OrdersCoreController {

    private final OrdersCoreService ordersCoreService;

    @GetMapping
    @ApiLog("获取所有订单核心信息")
    public ResponseEntity<List<OrdersCore>> getAllOrders() {
        try {
            List<OrdersCore> orders = ordersCoreService.list();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取订单核心信息")
    public ResponseEntity<OrdersCore> getOrderById(@PathVariable Long id) {
        try {
            OrdersCore order = ordersCoreService.getById(id);
            return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取订单列表")
    public ResponseEntity<List<OrdersCore>> getOrdersByUserId(@PathVariable Long userId) {
        try {
            List<OrdersCore> orders = ordersCoreService.getByUserId(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @ApiLog("根据订单状态获取订单列表")
    public ResponseEntity<List<OrdersCore>> getOrdersByStatus(@PathVariable String status) {
        try {
            List<OrdersCore> orders = ordersCoreService.getByStatus(status);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/number/{orderNumber}")
    @ApiLog("根据订单号获取订单")
    public ResponseEntity<OrdersCore> getOrderByNumber(@PathVariable String orderNumber) {
        try {
            OrdersCore order = ordersCoreService.getByOrderNumber(orderNumber);
            return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建订单核心信息")
    public ResponseEntity<OrdersCore> createOrder(@RequestBody OrdersCore order) {
        try {
            boolean success = ordersCoreService.save(order);
            return success ? ResponseEntity.ok(order) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新订单核心信息")
    public ResponseEntity<OrdersCore> updateOrder(@PathVariable Long id, @RequestBody OrdersCore order) {
        try {
            order.setId(id);
            boolean success = ordersCoreService.updateById(order);
            return success ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除订单核心信息")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            boolean success = ordersCoreService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}