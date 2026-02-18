package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.OrderShippingTrack;
import com.dailydiscover.service.OrderShippingTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-shipping-tracks")
@RequiredArgsConstructor
public class OrderShippingTrackController {

    private final OrderShippingTrackService orderShippingTrackService;

    @GetMapping
    @ApiLog("获取所有物流跟踪记录")
    public ResponseEntity<List<OrderShippingTrack>> getAllOrderShippingTracks() {
        try {
            List<OrderShippingTrack> tracks = orderShippingTrackService.list();
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取物流跟踪记录")
    public ResponseEntity<OrderShippingTrack> getOrderShippingTrackById(@PathVariable Long id) {
        try {
            OrderShippingTrack track = orderShippingTrackService.getById(id);
            return track != null ? ResponseEntity.ok(track) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取物流跟踪记录")
    public ResponseEntity<List<OrderShippingTrack>> getOrderShippingTracksByOrderId(@PathVariable Long orderId) {
        try {
            List<OrderShippingTrack> tracks = orderShippingTrackService.findByOrderId(orderId);
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tracking-number/{trackingNumber}")
    @ApiLog("根据物流单号获取跟踪记录")
    public ResponseEntity<List<OrderShippingTrack>> getOrderShippingTracksByTrackingNumber(@PathVariable String trackingNumber) {
        try {
            List<OrderShippingTrack> tracks = orderShippingTrackService.findByTrackingNumber(trackingNumber);
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}/latest")
    @ApiLog("查询最新的物流状态")
    public ResponseEntity<OrderShippingTrack> getLatestTrackByOrderId(@PathVariable Long orderId) {
        try {
            OrderShippingTrack track = orderShippingTrackService.findLatestTrackByOrderId(orderId);
            return track != null ? ResponseEntity.ok(track) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @ApiLog("根据物流状态查询")
    public ResponseEntity<List<OrderShippingTrack>> getOrderShippingTracksByStatus(@PathVariable String status) {
        try {
            List<OrderShippingTrack> tracks = orderShippingTrackService.findByStatus(status);
            return ResponseEntity.ok(tracks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建物流跟踪记录")
    public ResponseEntity<OrderShippingTrack> createOrderShippingTrack(@RequestBody OrderShippingTrack track) {
        try {
            boolean success = orderShippingTrackService.save(track);
            return success ? ResponseEntity.ok(track) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新物流跟踪记录")
    public ResponseEntity<OrderShippingTrack> updateOrderShippingTrack(@PathVariable Long id, @RequestBody OrderShippingTrack track) {
        try {
            track.setId(id);
            boolean success = orderShippingTrackService.updateById(track);
            return success ? ResponseEntity.ok(track) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除物流跟踪记录")
    public ResponseEntity<Void> deleteOrderShippingTrack(@PathVariable Long id) {
        try {
            boolean success = orderShippingTrackService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}