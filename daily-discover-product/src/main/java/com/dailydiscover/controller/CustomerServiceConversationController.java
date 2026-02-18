package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.CustomerServiceConversation;
import com.dailydiscover.service.CustomerServiceConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-service-conversations")
@RequiredArgsConstructor
public class CustomerServiceConversationController {

    private final CustomerServiceConversationService customerServiceConversationService;

    @GetMapping
    @ApiLog("获取所有客服会话")
    public ResponseEntity<List<CustomerServiceConversation>> getAllCustomerServiceConversations() {
        try {
            List<CustomerServiceConversation> conversations = customerServiceConversationService.list();
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取客服会话")
    public ResponseEntity<CustomerServiceConversation> getCustomerServiceConversationById(@PathVariable Long id) {
        try {
            CustomerServiceConversation conversation = customerServiceConversationService.getById(id);
            return conversation != null ? ResponseEntity.ok(conversation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    @ApiLog("根据用户ID获取客服会话")
    public ResponseEntity<List<CustomerServiceConversation>> getCustomerServiceConversationsByUserId(@PathVariable Long userId) {
        try {
            List<CustomerServiceConversation> conversations = customerServiceConversationService.findByUserId(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/agent/{agentId}")
    @ApiLog("根据客服人员ID获取会话")
    public ResponseEntity<List<CustomerServiceConversation>> getCustomerServiceConversationsByAgentId(@PathVariable Long agentId) {
        try {
            List<CustomerServiceConversation> conversations = customerServiceConversationService.findByAgentId(agentId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @ApiLog("获取活跃会话")
    public ResponseEntity<List<CustomerServiceConversation>> getActiveConversations() {
        try {
            List<CustomerServiceConversation> conversations = customerServiceConversationService.findActiveConversations();
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/pending")
    @ApiLog("获取待处理会话")
    public ResponseEntity<List<CustomerServiceConversation>> getPendingConversations() {
        try {
            List<CustomerServiceConversation> conversations = customerServiceConversationService.findPendingConversations();
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @ApiLog("根据状态获取会话")
    public ResponseEntity<List<CustomerServiceConversation>> getConversationsByStatus(@PathVariable String status) {
        try {
            List<CustomerServiceConversation> conversations = customerServiceConversationService.findByStatus(status);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建客服会话")
    public ResponseEntity<CustomerServiceConversation> createCustomerServiceConversation(@RequestBody CustomerServiceConversation conversation) {
        try {
            boolean success = customerServiceConversationService.save(conversation);
            return success ? ResponseEntity.ok(conversation) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新客服会话")
    public ResponseEntity<CustomerServiceConversation> updateCustomerServiceConversation(@PathVariable Long id, @RequestBody CustomerServiceConversation conversation) {
        try {
            conversation.setId(id);
            boolean success = customerServiceConversationService.updateById(conversation);
            return success ? ResponseEntity.ok(conversation) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @ApiLog("更新会话状态")
    public ResponseEntity<Void> updateConversationStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean success = customerServiceConversationService.updateConversationStatus(id, status);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除客服会话")
    public ResponseEntity<Void> deleteCustomerServiceConversation(@PathVariable Long id) {
        try {
            boolean success = customerServiceConversationService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}