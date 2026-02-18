package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.OrderInvoice;
import com.dailydiscover.service.OrderInvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-invoices")
@RequiredArgsConstructor
public class OrderInvoiceController {

    private final OrderInvoiceService orderInvoiceService;

    @GetMapping
    @ApiLog("获取所有订单发票")
    public ResponseEntity<List<OrderInvoice>> getAllOrderInvoices() {
        try {
            List<OrderInvoice> invoices = orderInvoiceService.list();
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取订单发票")
    public ResponseEntity<OrderInvoice> getOrderInvoiceById(@PathVariable Long id) {
        try {
            OrderInvoice invoice = orderInvoiceService.getById(id);
            return invoice != null ? ResponseEntity.ok(invoice) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取发票信息")
    public ResponseEntity<OrderInvoice> getOrderInvoiceByOrderId(@PathVariable Long orderId) {
        try {
            OrderInvoice invoice = orderInvoiceService.getByOrderId(orderId);
            return invoice != null ? ResponseEntity.ok(invoice) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/invoice-number/{invoiceNumber}")
    @ApiLog("根据发票号获取发票信息")
    public ResponseEntity<OrderInvoice> getOrderInvoiceByInvoiceNumber(@PathVariable String invoiceNumber) {
        try {
            OrderInvoice invoice = orderInvoiceService.getByInvoiceNumber(invoiceNumber);
            return invoice != null ? ResponseEntity.ok(invoice) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pending")
    @ApiLog("获取待开票订单列表")
    public ResponseEntity<List<OrderInvoice>> getPendingInvoices() {
        try {
            List<OrderInvoice> invoices = orderInvoiceService.getPendingInvoices();
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/issued")
    @ApiLog("获取已开票订单列表")
    public ResponseEntity<List<OrderInvoice>> getIssuedInvoices() {
        try {
            List<OrderInvoice> invoices = orderInvoiceService.getIssuedInvoices();
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建订单发票")
    public ResponseEntity<OrderInvoice> createOrderInvoice(@RequestBody OrderInvoice invoice) {
        try {
            boolean success = orderInvoiceService.save(invoice);
            return success ? ResponseEntity.ok(invoice) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/create")
    @ApiLog("创建发票信息")
    public ResponseEntity<OrderInvoice> createInvoice(
            @RequestParam Long orderId,
            @RequestParam String invoiceType,
            @RequestParam String invoiceTitle,
            @RequestParam(required = false) String taxNumber,
            @RequestParam(defaultValue = "商品明细") String invoiceContent) {
        try {
            OrderInvoice invoice = orderInvoiceService.createInvoice(orderId, invoiceType, invoiceTitle, taxNumber, invoiceContent);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新订单发票")
    public ResponseEntity<OrderInvoice> updateOrderInvoice(@PathVariable Long id, @RequestBody OrderInvoice invoice) {
        try {
            invoice.setId(id);
            boolean success = orderInvoiceService.updateById(invoice);
            return success ? ResponseEntity.ok(invoice) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{orderId}/status")
    @ApiLog("更新发票状态")
    public ResponseEntity<Void> updateInvoiceStatus(@PathVariable Long orderId, @RequestParam String status) {
        try {
            boolean success = orderInvoiceService.updateInvoiceStatus(orderId, status);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{orderId}/issue")
    @ApiLog("开具发票")
    public ResponseEntity<Void> issueInvoice(@PathVariable Long orderId, @RequestParam String invoiceNumber) {
        try {
            boolean success = orderInvoiceService.issueInvoice(orderId, invoiceNumber);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{orderId}/void")
    @ApiLog("作废发票")
    public ResponseEntity<Void> voidInvoice(@PathVariable Long orderId) {
        try {
            boolean success = orderInvoiceService.voidInvoice(orderId);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除订单发票")
    public ResponseEntity<Void> deleteOrderInvoice(@PathVariable Long id) {
        try {
            boolean success = orderInvoiceService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}