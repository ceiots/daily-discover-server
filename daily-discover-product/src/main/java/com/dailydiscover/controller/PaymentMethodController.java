package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.PaymentMethod;
import com.dailydiscover.service.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    @GetMapping
    @ApiLog("获取所有支付方式")
    public ResponseEntity<List<PaymentMethod>> getAllPaymentMethods() {
        try {
            List<PaymentMethod> methods = paymentMethodService.list();
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取支付方式")
    public ResponseEntity<PaymentMethod> getPaymentMethodById(@PathVariable Long id) {
        try {
            PaymentMethod method = paymentMethodService.getById(id);
            return method != null ? ResponseEntity.ok(method) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/available")
    @ApiLog("获取可用的支付方式")
    public ResponseEntity<List<PaymentMethod>> getAvailablePaymentMethods() {
        try {
            List<PaymentMethod> methods = paymentMethodService.getAvailableMethods();
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{methodType}")
    @ApiLog("根据支付方式类型获取支付方式")
    public ResponseEntity<List<PaymentMethod>> getPaymentMethodsByType(@PathVariable String methodType) {
        try {
            List<PaymentMethod> methods = paymentMethodService.getByMethodType(methodType);
            return ResponseEntity.ok(methods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/code/{methodCode}")
    @ApiLog("根据支付方式代码获取支付方式")
    public ResponseEntity<PaymentMethod> getPaymentMethodByCode(@PathVariable String methodCode) {
        try {
            PaymentMethod method = paymentMethodService.getByMethodCode(methodCode);
            return method != null ? ResponseEntity.ok(method) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/supported-types")
    @ApiLog("获取支持的支付方式类型")
    public ResponseEntity<List<String>> getSupportedMethodTypes() {
        try {
            List<String> types = paymentMethodService.getSupportedMethodTypes();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{methodCode}/validate")
    @ApiLog("验证支付方式是否可用")
    public ResponseEntity<Boolean> validateMethodAvailability(@PathVariable String methodCode) {
        try {
            boolean available = paymentMethodService.validateMethodAvailability(methodCode);
            return ResponseEntity.ok(available);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建支付方式")
    public ResponseEntity<PaymentMethod> createPaymentMethod(@RequestBody PaymentMethod method) {
        try {
            boolean success = paymentMethodService.save(method);
            return success ? ResponseEntity.ok(method) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新支付方式")
    public ResponseEntity<PaymentMethod> updatePaymentMethod(@PathVariable Long id, @RequestBody PaymentMethod method) {
        try {
            method.setId(id);
            boolean success = paymentMethodService.updateById(method);
            return success ? ResponseEntity.ok(method) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/toggle")
    @ApiLog("启用/禁用支付方式")
    public ResponseEntity<Void> toggleMethodEnabled(@PathVariable Long id, @RequestParam Boolean enabled) {
        try {
            boolean success = paymentMethodService.toggleMethodEnabled(id, enabled);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/config")
    @ApiLog("更新支付方式配置")
    public ResponseEntity<Void> updateMethodConfig(@PathVariable Long id, @RequestParam String configJson) {
        try {
            boolean success = paymentMethodService.updateMethodConfig(id, configJson);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除支付方式")
    public ResponseEntity<Void> deletePaymentMethod(@PathVariable Long id) {
        try {
            boolean success = paymentMethodService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}