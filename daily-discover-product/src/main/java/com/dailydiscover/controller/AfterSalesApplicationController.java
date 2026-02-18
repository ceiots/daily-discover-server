package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.AfterSalesApplication;
import com.dailydiscover.service.AfterSalesApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/after-sales")
@RequiredArgsConstructor
public class AfterSalesApplicationController {

    private final AfterSalesApplicationService afterSalesApplicationService;

    @GetMapping
    @ApiLog("获取所有售后申请")
    public ResponseEntity<List<AfterSalesApplication>> getAllAfterSalesApplications() {
        try {
            List<AfterSalesApplication> applications = afterSalesApplicationService.list();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取售后申请")
    public ResponseEntity<AfterSalesApplication> getAfterSalesApplicationById(@PathVariable Long id) {
        try {
            AfterSalesApplication application = afterSalesApplicationService.getById(id);
            return application != null ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/order/{orderId}")
    @ApiLog("根据订单ID获取售后申请")
    public ResponseEntity<List<AfterSalesApplication>> getAfterSalesApplicationsByOrderId(@PathVariable Long orderId) {
        try {
            List<AfterSalesApplication> applications = afterSalesApplicationService.findByOrderId(orderId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取售后申请")
    public ResponseEntity<List<AfterSalesApplication>> getAfterSalesApplicationsByUserId(@PathVariable Long userId) {
        try {
            List<AfterSalesApplication> applications = afterSalesApplicationService.findByUserId(userId);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @ApiLog("根据状态获取售后申请")
    public ResponseEntity<List<AfterSalesApplication>> getAfterSalesApplicationsByStatus(@PathVariable String status) {
        try {
            List<AfterSalesApplication> applications = afterSalesApplicationService.findByStatus(status);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{type}")
    @ApiLog("根据类型获取售后申请")
    public ResponseEntity<List<AfterSalesApplication>> getAfterSalesApplicationsByType(@PathVariable String type) {
        try {
            List<AfterSalesApplication> applications = afterSalesApplicationService.findByType(type);
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pending")
    @ApiLog("获取待处理的售后申请")
    public ResponseEntity<List<AfterSalesApplication>> getPendingAfterSalesApplications() {
        try {
            List<AfterSalesApplication> applications = afterSalesApplicationService.findPendingApplications();
            return ResponseEntity.ok(applications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/application-no/{applicationNo}")
    @ApiLog("根据申请单号获取售后申请")
    public ResponseEntity<AfterSalesApplication> getAfterSalesApplicationByApplicationNo(@PathVariable String applicationNo) {
        try {
            AfterSalesApplication application = afterSalesApplicationService.findByApplicationNo(applicationNo);
            return application != null ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建售后申请")
    public ResponseEntity<AfterSalesApplication> createAfterSalesApplication(@RequestBody AfterSalesApplication application) {
        try {
            boolean success = afterSalesApplicationService.save(application);
            return success ? ResponseEntity.ok(application) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新售后申请")
    public ResponseEntity<AfterSalesApplication> updateAfterSalesApplication(@PathVariable Long id, @RequestBody AfterSalesApplication application) {
        try {
            application.setId(id);
            boolean success = afterSalesApplicationService.updateById(application);
            return success ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除售后申请")
    public ResponseEntity<Void> deleteAfterSalesApplication(@PathVariable Long id) {
        try {
            boolean success = afterSalesApplicationService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}