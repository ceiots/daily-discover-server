package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.OrderItem;
import com.dailydiscover.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping
    @ApiLog("获取所有订单项")
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        try {
            List<OrderItem> orderItems = orderItemService.list();
            return ResponseEntity.ok(orderItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取订单项")
    public ResponseEntity<OrderItem> getOrderItemById(@PathVariable Long id) {
        try {
            OrderItem orderItem = orderItemService.getById(id);
            return orderItem != null ? ResponseEntity.ok(orderItem) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取订单项")
    public ResponseEntity<List<OrderItem>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderItem> orderItems = orderItemService.getByOrderId(orderId);
            return ResponseEntity.ok(orderItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/product/{productId}")
    @ApiLog("根据商品ID获取订单项")
    public ResponseEntity<List<OrderItem>> getOrderItemsByProductId(@PathVariable Long productId) {
        try {
            List<OrderItem> orderItems = orderItemService.getByProductId(productId);
            return ResponseEntity.ok(orderItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sku/{skuId}")
    @ApiLog("根据SKU ID获取订单项")
    public ResponseEntity<List<OrderItem>> getOrderItemsBySkuId(@PathVariable Long skuId) {
        try {
            List<OrderItem> orderItems = orderItemService.getBySkuId(skuId);
            return ResponseEntity.ok(orderItems);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}/total-amount")
    @ApiLog("计算订单总金额")
    public ResponseEntity<BigDecimal> calculateOrderTotalAmount(@PathVariable Long orderId) {
        try {
            BigDecimal totalAmount = orderItemService.calculateOrderTotalAmount(orderId);
            return ResponseEntity.ok(totalAmount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}/stats")
    @ApiLog("获取订单项统计信息")
    public ResponseEntity<Map<String, Object>> getOrderItemStats(@PathVariable Long orderId) {
        try {
            Map<String, Object> stats = orderItemService.getOrderItemStats(orderId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建订单项")
    public ResponseEntity<OrderItem> createOrderItem(@RequestBody OrderItem orderItem) {
        try {
            boolean success = orderItemService.save(orderItem);
            return success ? ResponseEntity.ok(orderItem) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/add")
    @ApiLog("添加订单项")
    public ResponseEntity<OrderItem> addOrderItem(
            @RequestParam Long orderId,
            @RequestParam Long productId,
            @RequestParam Long skuId,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal unitPrice,
            @RequestParam BigDecimal totalPrice) {
        try {
            OrderItem orderItem = orderItemService.addOrderItem(orderId, productId, skuId, quantity, unitPrice, totalPrice);
            return ResponseEntity.ok(orderItem);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新订单项")
    public ResponseEntity<OrderItem> updateOrderItem(@PathVariable Long id, @RequestBody OrderItem orderItem) {
        try {
            orderItem.setId(id);
            boolean success = orderItemService.updateById(orderItem);
            return success ? ResponseEntity.ok(orderItem) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/quantity")
    @ApiLog("更新订单项数量")
    public ResponseEntity<Void> updateOrderItemQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            boolean success = orderItemService.updateOrderItemQuantity(id, quantity);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除订单项")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        try {
            boolean success = orderItemService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}